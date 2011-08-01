/**
 * 
 */
package de.xwic.cube;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

import de.xwic.cube.formatter.PercentageValueFormatProvider;
import de.xwic.cube.functions.DifferenceFunction;
import de.xwic.cube.storage.impl.FileDataPoolStorageProvider;
import de.xwic.cube.util.DataDump;

/**
 * @author Florian Lippisch
 */
public class CubeSwapIndexedTest extends TestCase {

	ICube cube = null;
	private IMeasure meBookings;
	private IMeasure mePlan;
	private IMeasure meDiff;
	private IDimension dimOT;
	private IDimension dimLOB;
	private IDimension dimTime;
	private IDataPool pool;
	private IDataPoolManager manager;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		
		BasicConfigurator.configure();
		
		File dataDir = new File("data");
		if (!dataDir.exists()) {
			dataDir.mkdirs();
		}
		IDataPoolStorageProvider storageProvider = new FileDataPoolStorageProvider(dataDir);
		manager = DataPoolManagerFactory.createDataPoolManager(storageProvider);
		pool = manager.createDataPool("test");
		dimOT = pool.createDimension("OrderType");
		dimOT.createDimensionElement("AOO");	
		dimOT.createDimensionElement("COO");
		
		
		dimLOB = pool.createDimension("LOB");
		dimLOB.createDimensionElement("Hardware");
		IDimensionElement elmPS = dimLOB.createDimensionElement("PS");
		elmPS.createDimensionElement("Installation");
		elmPS.createDimensionElement("Consulting");
		elmPS.createDimensionElement("Service");
		dimLOB.createDimensionElement("Education");
		 
		
		dimTime = pool.createDimension("Time");
		IDimensionElement deY2008 = dimTime.createDimensionElement("2008");
		IDimensionElement deQ1 = deY2008.createDimensionElement("Q1");
		IDimensionElement deQ2 = deY2008.createDimensionElement("Q2");
		deQ1.createDimensionElement("Jan");
		deQ1.createDimensionElement("Feb");
		deQ1.createDimensionElement("Mar");

		deQ2.createDimensionElement("Apr");
		deQ2.createDimensionElement("May");
		deQ2.createDimensionElement("Jun");
		
		meBookings = pool.createMeasure("Bookings");
		mePlan = pool.createMeasure("Plan");
		meDiff = pool.createMeasure("Diff");

		DifferenceFunction function = new DifferenceFunction(meBookings, mePlan, true); 
		meDiff.setFunction(function);
		meDiff.setValueFormatProvider(new PercentageValueFormatProvider());
		
		cube = pool.createCube("test", 
				new IDimension[] { dimOT, dimLOB, dimTime }, 
				new IMeasure[] { meBookings, mePlan, meDiff }, 
				IDataPool.CubeType.INDEXED_SWAP);
		
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		pool.close();
	}
	
	public void testCreateKey() {
		
		Key key = cube.createKey("[AOO][Hardware]");
		System.out.println("Key: " + key);
		assertNotNull(key);
		assertEquals(dimOT.getDimensionElement("AOO"), key.getDimensionElement(0));
		assertEquals(dimLOB.getDimensionElement("Hardware"), key.getDimensionElement(1));

		key = cube.createKey("[AOO][PS/Consulting]");
		System.out.println("Key: " + key);
		assertNotNull(key);
		assertEquals(dimOT.getDimensionElement("AOO"), key.getDimensionElement(0));
		assertEquals(dimLOB.getDimensionElement("PS").getDimensionElement("Consulting"), key.getDimensionElement(1));

		
		// test with named dimensions
		key = cube.createKey("[LOB:Hardware][OrderType:AOO]");
		System.out.println("Key: " + key);
		assertNotNull(key);
		assertEquals(dimOT.getDimensionElement("AOO"), key.getDimensionElement(0));
		assertEquals(dimLOB.getDimensionElement("Hardware"), key.getDimensionElement(1));

		key = cube.createKey("[LOB:*][LOB:PS]");
		System.out.println("Key: " + key);

		
	}
	
	public void testWriteAndRead() {
		
		long start = System.currentTimeMillis();
		
		cube.beginMassUpdate();
		
		Key key = cube.createKey("[AOO][Hardware][2008/Q1/Jan]");
		cube.setCellValue(key, meBookings, 100.0);

		key = cube.createKey("[AOO][Hardware][2008/Q1/Feb]");
		cube.setCellValue(key, meBookings, 40.0);

		key = cube.createKey("[AOO][Hardware][2008/Q1/Mar]");
		cube.setCellValue(key, meBookings, 80.0);
		
		key = cube.createKey("[AOO][PS/Consulting][2008/Q1/Feb]");
		cube.setCellValue(key, meBookings, 50.0);

		key = cube.createKey("[COO][PS/Consulting][2008/Q1/Mar]");
		cube.setCellValue(key, meBookings, 200.0);
		
		cube.massUpdateFinished(); // causes dump

		Double val = cube.getCellValue("[*][Hardware]", meBookings);
		System.out.println(val);
		
		long duration = System.currentTimeMillis() - start;
		System.out.println("Duration: " + duration);
		
		DataDump.printValues(System.out, cube, dimLOB, dimOT , meBookings);
		
		assertEquals(220.0, cube.getCellValue("[AOO][Hardware][2008/Q1]", meBookings));
		assertEquals(220.0, cube.getCellValue("[*][Hardware]", meBookings));
		assertEquals(270.0, cube.getCellValue("[AOO][*]", meBookings));
		assertEquals(250.0, cube.getCellValue("[*][PS]", meBookings));
		assertEquals(250.0, cube.getCellValue("[LOB:PS]", meBookings));
		assertEquals(470.0, cube.getCellValue("", meBookings));

	}
	
	public void testStoreRestore() throws StorageException {
		
		cube.beginMassUpdate();
		
		Key key = cube.createKey("[AOO][Hardware][2008/Q1/Jan]");
		cube.setCellValue(key, meBookings, 100.0);

		key = cube.createKey("[AOO][Hardware][2008/Q1/Feb]");
		cube.setCellValue(key, meBookings, 40.0);

		key = cube.createKey("[AOO][Hardware][2008/Q1/Mar]");
		cube.setCellValue(key, meBookings, 80.0);
		
		key = cube.createKey("[AOO][PS/Consulting][2008/Q1/Feb]");
		cube.setCellValue(key, meBookings, 50.0);

		key = cube.createKey("[COO][PS/Consulting][2008/Q1/Mar]");
		cube.setCellValue(key, meBookings, 200.0);
		
		cube.massUpdateFinished(); // causes dump

		Double val = cube.getCellValue("[*][Hardware]", meBookings);
		System.out.println(val);
		

		pool.save(); // saved

		DataDump.printValues(System.out, cube, dimLOB, dimOT , meBookings);
		
		assertEquals(220.0, cube.getCellValue("[AOO][Hardware][2008/Q1]", meBookings));
		assertEquals(220.0, cube.getCellValue("[*][Hardware]", meBookings));
		assertEquals(270.0, cube.getCellValue("[AOO][*]", meBookings));
		assertEquals(250.0, cube.getCellValue("[*][PS]", meBookings));
		assertEquals(250.0, cube.getCellValue("[LOB:PS]", meBookings));
		assertEquals(470.0, cube.getCellValue("", meBookings));

		manager.releaseDataPool(pool);
		
		pool = manager.getDataPool("test"); // restore
		

		pool.close();
		
	}
	
	public void testCache() throws Exception {
		
		cube.beginMassUpdate();
		
		Key key = cube.createKey("[AOO][Hardware][2008/Q1/Jan]");
		cube.setCellValue(key, meBookings, 100.0);

		key = cube.createKey("[AOO][Hardware][2008/Q1/Feb]");
		cube.setCellValue(key, meBookings, 40.0);

		key = cube.createKey("[AOO][Hardware][2008/Q1/Mar]");
		cube.setCellValue(key, meBookings, 80.0);
		
		key = cube.createKey("[AOO][PS/Consulting][2008/Q1/Feb]");
		cube.setCellValue(key, meBookings, 50.0);

		key = cube.createKey("[COO][PS/Consulting][2008/Q1/Mar]");
		cube.setCellValue(key, meBookings, 200.0);
		
		cube.massUpdateFinished(); // causes dump


		ICubeCacheControl ccc = (ICubeCacheControl)cube;
		System.out.println("Cache Size: " + ccc.getCacheSize());

		Double val = cube.getCellValue("[*][Hardware]", meBookings);
		System.out.println(val);

		assertEquals(1, ccc.getCacheSize());
		System.out.println("Cache Size: " + ccc.getCacheSize());
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ccc.printCacheProfile(new PrintStream(bos));
		
		System.out.println(bos.toString());
		// now clear the whole cube
		cube.beginMassUpdate();
		cube.clear();
		key = cube.createKey("[AOO][Hardware][2008/Q1/Jan]");
		cube.setCellValue(key, meBookings, 100.0);

		key = cube.createKey("[AOO][Hardware][2008/Q1/Feb]");
		cube.setCellValue(key, meBookings, 40.0);

		key = cube.createKey("[AOO][Hardware][2008/Q1/Mar]");
		cube.setCellValue(key, meBookings, 80.0);
		
		key = cube.createKey("[AOO][PS/Consulting][2008/Q1/Feb]");
		cube.setCellValue(key, meBookings, 50.0);

		key = cube.createKey("[COO][PS/Consulting][2008/Q1/Mar]");
		cube.setCellValue(key, meBookings, 200.0);
		
		assertEquals(0, ccc.getCacheSize());

		cube.massUpdateFinished();

		// refresh the cache
		ccc.buildCacheFromStats(new ByteArrayInputStream(bos.toByteArray()));		
		
		
		assertEquals(1, ccc.getCacheSize());
		cube.printCacheProfile(System.out);
		
	}

	
	public void testMeasureFunction() {
		Key key = cube.createKey("[AOO][Hardware][2008/Q1/Jan]");
		cube.beginMassUpdate();
		
		cube.setCellValue(key, meBookings, 100.0);

		key = cube.createKey("[AOO][Hardware][2008/Q1/Feb]");
		cube.setCellValue(key, meBookings, 40.0);

		key = cube.createKey("[AOO][Hardware][2008/Q1/Mar]");
		cube.setCellValue(key, meBookings, 80.0);
		
		key = cube.createKey("[AOO][PS/Consulting][2008/Q1/Feb]");
		cube.setCellValue(key, meBookings, 50.0);

		key = cube.createKey("[COO][PS/Consulting][2008/Q1/Mar]");
		cube.setCellValue(key, meBookings, 500.0);
		
		key = cube.createKey("[COO][PS/Service][2008/Q1/Mar]");
		cube.setCellValue(key, meBookings, 20.0);

		// now with other measure
		key = cube.createKey("[AOO][Hardware][2008/Q1/Jan]");
		cube.setCellValue(key, mePlan, 250.0);

		key = cube.createKey("[AOO][Hardware][2008/Q1/Feb]");
		cube.setCellValue(key, mePlan, 30.0);

		key = cube.createKey("[AOO][Hardware][2008/Q1/Mar]");
		cube.setCellValue(key, mePlan, 50.0);
		
		key = cube.createKey("[AOO][PS/Consulting][2008/Q1/Feb]");
		cube.setCellValue(key, mePlan, 120.0);

		key = cube.createKey("[COO][PS/Consulting][2008/Q1/Mar]");
		cube.setCellValue(key, mePlan, 250.0);

		key = cube.createKey("[COO][PS/Installation][2008/Q1/Mar]");
		cube.setCellValue(key, mePlan, 150.0);

		cube.massUpdateFinished();
		
		// show
		System.out.println("Bookings");
		DataDump.printValues(System.out, cube, dimLOB, dimOT, meBookings);

		System.out.println("Plan");
		DataDump.printValues(System.out, cube, dimLOB, dimOT, mePlan);
		
		System.out.println("Difference");
		DataDump.printValues(System.out, cube, dimLOB, dimOT, meDiff);
		
	}

	
	public void testSplash() {
		
		cube.beginMassUpdate();

		Key key = cube.createKey("[AOO][PS][2008/Q1]");

		cube.getCellValue(key, meBookings); // READ
		
		System.out.printf("write to %s modified %d cells.%n", key, cube.addCellValue(key, meBookings, 300.0));

		
		DataDump.printValues(System.out, cube, dimLOB, dimOT , meBookings);
		DataDump.printValues(System.out, cube, dimTime, dimOT , meBookings);
		
		System.out.println(cube.getCellValue("[AOO][PS][2008/Q1]", meBookings));
		
		//assertEquals(300.0, cube.getCellValue("[AOO][PS][2008]", meBookings));
		//assertEquals(300.0, cube.getCellValue("[AOO][PS][2008/Q1]", meBookings));

		key = cube.createKey("[AOO][*][2008/Q1/Feb]");
		System.out.printf("write to %s modified %d cells.%n", key, cube.setCellValue(key, meBookings, 360.0));

		System.out.println(cube.getCellValue("[AOO][Hardware]", meBookings));

		key = cube.createKey("");
		System.out.printf("write to %s modified %d cells.%n", key, cube.setCellValue(key, meBookings, (double)(1248 * 2)));

		DataDump.printValues(System.out, cube, dimLOB, dimOT , meBookings);
		DataDump.printValues(System.out, cube, dimTime, dimOT , meBookings);

		key = cube.createKey("[AOO]");
		System.out.printf("write to %s modified %d cells.%n", key, cube.setCellValue(key, meBookings, 1600d));

		cube.massUpdateFinished();
		
		DataDump.printValues(System.out, cube, dimLOB, dimOT , meBookings);
		DataDump.printValues(System.out, cube, dimTime, dimOT , meBookings);

	}
	

	public void testClear() {

		cube.beginMassUpdate();
		Key key = cube.createKey("[AOO][Hardware][2008]");
		System.out.printf("write to %s modified %d cells.%n", key, cube.setCellValue(key, meBookings, 300.0));

		assertEquals(300.0, cube.getCellValue("[AOO][Hardware][2008]", meBookings));
		assertEquals(150.0, cube.getCellValue("[AOO][Hardware][2008/Q1]", meBookings));
		
		System.out.println(" ---------------------- BEFORE CLEAR --------------------");
		DataDump.printValues(System.out, cube, dimLOB, dimTime, meBookings);
		cube.massUpdateFinished();

		cube.clear();
		

		System.out.println(" ---------------------- AFTER CLEAR --------------------");
		DataDump.printValues(System.out, cube, dimLOB, dimTime, meBookings);
		
		assertNull(cube.getCellValue("[AOO][Hardware][2008]", meBookings));
		assertNull(cube.getCellValue("[AOO][Hardware][2008/Q1]", meBookings));
		
	}
	
	public void testClearMeasure() {

		cube.beginMassUpdate();

		Key key = cube.createKey("[AOO][Hardware][2008]");
		System.out.printf("write to %s modified %d cells.%n", key, cube.setCellValue(key, meBookings, 300.0));
		System.out.printf("Cube size: %d%n", cube.getSize());
		
		key = cube.createKey("[AOO][PS][2008]");
		System.out.printf("write to %s modified %d cells.%n", key, cube.setCellValue(key, mePlan, 600.0));
		System.out.printf("Cube size: %d%n", cube.getSize());
		
		assertEquals(300.0, cube.getCellValue("[AOO][Hardware][2008]", meBookings));
		assertEquals(150.0, cube.getCellValue("[AOO][Hardware][2008/Q1]", meBookings));
		
		System.out.println(" ---------------------- BEFORE CLEAR Bookings --------------------");
		DataDump.printValues(System.out, cube, dimLOB, dimOT, meBookings);
		cube.clear(meBookings);

		System.out.println(" ---------------------- AFTER CLEAR Bookings --------------------");
		DataDump.printValues(System.out, cube, dimLOB, dimOT, meBookings);
		System.out.printf("Cube size: %d%n", cube.getSize());

		cube.massUpdateFinished();
		
		assertNull(cube.getCellValue("[AOO][Hardware][2008]", meBookings));
		assertNull(cube.getCellValue("[AOO][Hardware][2008/Q1]", meBookings));
		assertEquals(600.0, cube.getCellValue("[AOO][PS][2008]", mePlan));
		assertTrue(Math.abs(300.0 - cube.getCellValue("[AOO][PS][2008/Q1]", mePlan)) < 0.01);
		
	}

	public void testClearMeasureAndKey() {

		cube.beginMassUpdate();
		
		Key key = cube.createKey("[AOO][Hardware][2008]");
		System.out.printf("write to %s modified %d cells.%n", key, cube.setCellValue(key, meBookings, 300.0));
		System.out.printf("Cube size: %d%n", cube.getSize());
		
		key = cube.createKey("[AOO][PS][2008]");
		System.out.printf("write to %s modified %d cells.%n", key, cube.setCellValue(key, meBookings, 600.0));
		System.out.printf("Cube size: %d%n", cube.getSize());
		
		assertEquals(900, cube.getCellValue("[AOO][*][2008]", meBookings).intValue());
		assertEquals(300.0, cube.getCellValue("[AOO][Hardware][2008]", meBookings));
		assertEquals(150.0, cube.getCellValue("[AOO][Hardware][2008/Q1]", meBookings));
		
		System.out.println(" ---------------------- BEFORE CLEAR Bookings --------------------");
		DataDump.printValues(System.out, cube, dimLOB, dimOT, meBookings);
		
		cube.clear(meBookings, cube.createKey("[AOO][Hardware][2008]"));

		System.out.println(" ---------------------- AFTER CLEAR Bookings --------------------");
		DataDump.printValues(System.out, cube, dimLOB, dimOT, meBookings);
		System.out.printf("Cube size: %d%n", cube.getSize());
		
		cube.massUpdateFinished();

		
		assertEquals(600, cube.getCellValue("[AOO][*][2008]", meBookings).intValue());
		assertNull(cube.getCellValue("[AOO][Hardware][2008]", meBookings));
		assertNull(cube.getCellValue("[AOO][Hardware][2008/Q1]", meBookings));
		
	}
	
	public void testQuery() {

		cube.beginMassUpdate();
		
		cube.setCellValue(cube.createKey(""), meBookings, 1000000.0d);
		
		
		
		IQuery query = cube.createQuery();
		
		List<Key> keys = query.createKeys();
		System.out.println("Keys: " + keys.toString());
		assertEquals(1, keys.size());

		cube.massUpdateFinished();

		
		query.selectDimensionElements(dimOT.parsePath("AOO"));
		keys = query.createKeys();
		System.out.println("Keys: " + keys.toString());
		System.out.println("Value: " + cube.getCellValue(query, meBookings));
		assertEquals(1, keys.size());
		
		query.selectDimensionElements(dimTime.parsePath("2008/Q1/Jan"), dimTime.parsePath("2008/Q1/Feb"));
		keys = query.createKeys();
		System.out.println("Keys: " + keys.toString());
		System.out.println("Value: " + cube.getCellValue(query, meBookings));
		assertEquals(2, keys.size());

		query.selectDimensionElements(dimOT.parsePath("COO"));
		keys = query.createKeys();
		System.out.println("Keys: " + keys.toString());
		System.out.println("Value: " + cube.getCellValue(query, meBookings));
		assertEquals(4, keys.size());
		
		
	}

	public void testBehaiviorNoSplash() {
		
		cube.setDimensionBehavior(dimLOB, DimensionBehavior.NO_SPLASH);
		
		Key key = cube.createKey("[AOO][PS][2008]");
		
		cube.beginMassUpdate();
		
		cube.setCellValue(key, meBookings, 1000d);
		
		cube.setCellValue(cube.createKey("[AOO][Hardware]"), meBookings, 500d);
		
		cube.massUpdateFinished();

		
		DataDump.printValues(System.out, cube, dimLOB, dimOT , meBookings);
		
		Double value = cube.getCellValue("[AOO][PS/Service][2008]", meBookings);	// as it was not splashed, PS/Service must be NULL
		assertNull(value);
		
		
		assertEquals(1500d, cube.getCellValue("[AOO][*][2008]", meBookings));
		
		
	}
	
	public void testBehaiviorNoAggregate() {
		
		cube.setDimensionBehavior(dimLOB, DimensionBehavior.NO_AGGREGATION);
		
		Key key = cube.createKey("[AOO][PS][2008]");

		cube.beginMassUpdate();
		cube.setCellValue(key, meBookings, 1000d);
		
		cube.setCellValue(cube.createKey("[AOO][Hardware]"), meBookings, 500d);
		
		DataDump.printValues(System.out, cube, dimLOB, dimOT , meBookings);
		
		Double value = cube.getCellValue("[AOO][*][2008]", meBookings);	// as it was not splashed, PS must be NULL
		
		cube.massUpdateFinished();

		assertNull(value);
		
	}

	public void testBehaiviorFlat() {
		
		cube.beginMassUpdate();
		cube.setDimensionBehavior(dimLOB, DimensionBehavior.FLAT);
		
		Key key = cube.createKey("[AOO][PS][2008]");
		
		cube.setCellValue(key, meBookings, 1000d);
		
		cube.setCellValue(cube.createKey("[AOO][Hardware]"), meBookings, 500d);
		
		cube.massUpdateFinished();

		
		Double value = cube.getCellValue("[AOO][*][2008]", meBookings);	// as it was not splashed, PS must be NULL
		assertNull(value);

		DataDump.printValues(System.out, cube, dimLOB, dimOT , meBookings);
		

		value = cube.getCellValue("[AOO][PS/Service][2008]", meBookings);	// as it was not splashed, PS must be NULL
		assertNull(value);
	}


}
