/**
 * 
 */
package de.xwic.cube;

import junit.framework.TestCase;
import de.xwic.cube.util.DataDump;

/**
 * @author Florian Lippisch
 */
public class CubeTest extends TestCase {

	ICube cube = null;
	private IMeasure meBookings;
	private IDimension dimOT;
	private IDimension dimLOB;
	private IDimension dimTime;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		
		IDataPoolManager manager = DataPoolManagerFactory.createDataPoolManager();
		IDataPool pool = manager.createDataPool("test");
		
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
		cube = pool.createCube("test", new IDimension[] { dimOT, dimLOB, dimTime }, new IMeasure[] { meBookings });
		
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
		
		long duration = System.currentTimeMillis() - start;
		System.out.println("Duration: " + duration);
		
		assertEquals(220.0, cube.getCellValue("[AOO][Hardware][2008/Q1]", meBookings));
		assertEquals(220.0, cube.getCellValue("[*][Hardware]", meBookings));
		assertEquals(270.0, cube.getCellValue("[AOO][*]", meBookings));
		assertEquals(250.0, cube.getCellValue("[*][PS]", meBookings));
		assertEquals(250.0, cube.getCellValue("[LOB:PS]", meBookings));
		assertEquals(470.0, cube.getCellValue("", meBookings));

		DataDump.printValues(System.out, cube, dimLOB, dimOT , meBookings);
		
	}

	
	public void testSplash() {
		
		Key key = cube.createKey("[AOO][Hardware][2008]");
		System.out.printf("write to %s modified %d cells.%n", key, cube.setCellValue(key, meBookings, 300.0));

		assertEquals(300.0, cube.getCellValue("[AOO][Hardware][2008]", meBookings));
		assertEquals(150.0, cube.getCellValue("[AOO][Hardware][2008/Q1]", meBookings));

		key = cube.createKey("[AOO][*][2008/Q1/Feb]");
		System.out.printf("write to %s modified %d cells.%n", key, cube.setCellValue(key, meBookings, 360.0));

		System.out.println(cube.getCellValue("[AOO][Hardware]", meBookings));

		key = cube.createKey("");
		System.out.printf("write to %s modified %d cells.%n", key, cube.setCellValue(key, meBookings, 1248 * 2));

		DataDump.printValues(System.out, cube, dimLOB, dimOT , meBookings);
		DataDump.printValues(System.out, cube, dimTime, dimOT , meBookings);

		key = cube.createKey("[AOO]");
		System.out.printf("write to %s modified %d cells.%n", key, cube.setCellValue(key, meBookings, 1600));

		DataDump.printValues(System.out, cube, dimLOB, dimOT , meBookings);
		DataDump.printValues(System.out, cube, dimTime, dimOT , meBookings);

	}

}
