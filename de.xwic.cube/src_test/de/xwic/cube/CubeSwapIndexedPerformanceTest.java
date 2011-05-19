/**
 * 
 */
package de.xwic.cube;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import au.com.bytecode.opencsv.CSVReader;
import de.xwic.cube.impl.CubeFlexCalc;
import de.xwic.cube.impl.CubeIndexed;
import de.xwic.cube.storage.impl.FileDataPoolStorageProvider;
import de.xwic.cube.util.CubeImportUtil;
import de.xwic.cube.util.ImportException;

/**
 * @author Florian Lippisch
 */
public class CubeSwapIndexedPerformanceTest extends TestCase {

	private final static String TEST_FILE = "c:\\users\\lippisch\\documents\\reporting\\Bookings_Cube2.csv";
	
	private final static String[] QUERIES = {
		"[Time:2010/Q1][GEO:EMEA][LOB:PS]",
		"[Time:2010/Q1/May][GEO:EMEA][LOB:PS]",
		"[Time:2010/Q1/Jun][GEO:EMEA][LOB:PS]",
		"[Time:2010/Q1/Jul][GEO:EMEA][LOB:PS]",
		"[Time:2010/Q2/Aug][GEO:EMEA][LOB:PS]",
		"[Time:2010/Q2/Sep][GEO:EMEA][LOB:PS]",
		"[Time:2010/Q2/Oct][GEO:EMEA][LOB:PS]",
		"[Time:2010/Q2][GEO:EMEA][LOB:PS]",
		"[Time:2009/Q1][GEO:EMEA][LOB:PS]",
		"[Time:2009/Q1/May][GEO:EMEA][LOB:PS]",
		"[Time:2009/Q1/Jun][GEO:EMEA][LOB:PS]",
		"[Time:2009/Q1/Jul][GEO:EMEA][LOB:PS]",
		"[Time:2010/Q1/May][GEO:EMEA][LOB:PS]",
		"[Time:2010/Q1/May][GEO:EMEA][LOB:PS][SupportType:PREMIUM]",
		"[Time:*][GEO:EMEA/Enterprise/Germany][LOB:PS/Installation]",
		"[Time:*][GEO:EMEA/Enterprise/UK][LOB:PS/Installation]",
		"[Time:*][GEO:EMEA/Enterprise/France][LOB:PS/Installation]"
	};
	
	ICube cube = null;
	private IMeasure meBookings;
	private IMeasure meListprice;
	private IMeasure meBookingsFR;
	private IDimension dimOT;
	private IDimension dimLOB;
	private IDimension dimTime;
	private IDataPool pool;
	private IDataPoolManager manager;
	private IDimension dimGEO;
	private IDimension dimAccount;
	private IDimension dimLocalAccount;
	private IDimension dimSupportType;
	private IDimension dimProductGrouping;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		
		File dataDir = new File("data");
		if (!dataDir.exists()) {
			dataDir.mkdirs();
		}
		IDataPoolStorageProvider storageProvider = new FileDataPoolStorageProvider(dataDir);
		manager = DataPoolManagerFactory.createDataPoolManager(storageProvider);
		pool = manager.createDataPool("test");
		
		//"Time","GEO","LOB","Account","LocalAccount","OrderType","SupportType","ProductGrouping"
		dimOT = pool.createDimension("OrderType");
		dimLOB = pool.createDimension("LOB");
		dimTime = pool.createDimension("Time");

		dimGEO = pool.createDimension("GEO");
		dimAccount = pool.createDimension("Account");
		dimLocalAccount = pool.createDimension("LocalAccount");
		dimSupportType = pool.createDimension("SupportType");
		dimProductGrouping = pool.createDimension("ProductGrouping");

		IDimension dimBookingsType = pool.createDimension("BookingsType");

		meBookings = pool.createMeasure("Bookings");
		meListprice = pool.createMeasure("Listprice");
		meBookingsFR = pool.createMeasure("BookingsFR");

		
		cube = pool.createCube("Bookings", 
				new IDimension[] { dimTime, dimGEO, dimLOB, dimAccount, dimLocalAccount, dimOT, dimSupportType, dimProductGrouping, dimBookingsType},
				new IMeasure[] { meBookings, meListprice, meBookingsFR },
				IDataPool.CubeType.INDEXED_SWAP);
		
	}
		
	public void testImport() throws Exception {
		
		File file = new File(TEST_FILE);
		assertTrue(file.exists());
		
		cube.beginMassUpdate();
		FileInputStream fin = new FileInputStream(file);
		try {
			long start = System.currentTimeMillis();
			int lines = CubeImportUtil.importCSV(fin, cube, true);
			long duration = System.currentTimeMillis() - start;
			System.out.println("Loaded " + lines + " in time: " + duration);
			System.out.println("Cube Size: " + cube.getSize());
		} finally {
			fin.close();
		}
		{ // finish, which causes the index builder to run.
			long start = System.currentTimeMillis();
			cube.massUpdateFinished();
			long duration = System.currentTimeMillis() - start;
			System.out.println("Mass Update Finished: " + duration);
		}
		
		{ // fist time access
			long start = System.currentTimeMillis();
			
			// loop through all accounts
			long reads = 0;
			int acid = cube.getDimensionIndex(dimAccount);
			for (String sk : QUERIES) {
				Key key = cube.createKey(sk);
				cube.getCellValue(key, meBookings);
				reads++;
				for (IDimensionElement deAc : dimAccount.getDimensionElements()) {
					key.setDimensionElement(acid, deAc); //
					cube.getCellValue(key, meBookings);
					reads++;
					
					// check children
					if (!deAc.isLeaf()) {
						for (IDimensionElement deAcL2 : deAc.getDimensionElements()) {
							key.setDimensionElement(acid, deAcL2); //
							cube.getCellValue(key, meBookings);	
							reads++;
							
							// check children
							if (!deAcL2.isLeaf()) {
								for (IDimensionElement deAcL3 : deAcL2.getDimensionElements()) {
									key.setDimensionElement(acid, deAcL3); //
									cube.getCellValue(key, meBookings);	
									reads++;
								}
							}

						}
					}
					
				}
			}
			
			long duration = System.currentTimeMillis() - start;
			System.out.println(reads + " read operations took " + duration);
			((CubeIndexed)cube).printStats(System.out);
			
		}
		{  // reading 50 leafs
			long start = System.currentTimeMillis();
			testReadLeafData(50);
			long duration = System.currentTimeMillis() - start;
			System.out.println("50 leaf-read operations took " + duration);
			((CubeIndexed)cube).printStats(System.out);
		}
		{  // reading 50 leafs (again.
			long start = System.currentTimeMillis();
			testReadLeafData(50);
			long duration = System.currentTimeMillis() - start;
			System.out.println("50 leaf-read operations took again " + duration);
			((CubeIndexed)cube).printStats(System.out);
		}
		
		{ // size comparison
			
			if (cube instanceof ICubeCacheControl) {
				ICubeCacheControl ccc = (ICubeCacheControl)cube;
				System.out.println("Cache-Size: " + ccc.getCacheSize());
			}
			if (cube instanceof CubeFlexCalc) {
				((CubeFlexCalc)cube).printStats(System.out);
			}
			
		}
		
		{ // serialize 
			
			long start = System.currentTimeMillis();
			
			pool.save();
			
			long duration = System.currentTimeMillis() - start;
			System.out.println("Serialization Time: " + duration);
			
			File dpFile = new File("data/test.datapool");
			if (dpFile.exists()) {
				System.out.println("Pool size: " + dpFile.length());
			} else {
				System.out.println("Can not find file: " + dpFile.getName());
			}
			
		}
		
		{ // deserialize
			manager.releaseDataPool(pool);

			long start = System.currentTimeMillis();
			pool = manager.getDataPool("test");
			long duration = System.currentTimeMillis() - start;
			System.out.println("Deserialization Time: " + duration);
			
		}
	}

	/**
	 * @param i
	 * @throws ImportException 
	 * @throws Exception 
	 */
	private void testReadLeafData(int testReads) throws Exception {

		InputStream in = new FileInputStream(new File(TEST_FILE));
		CSVReader csvIn = new CSVReader(new BufferedReader(new InputStreamReader(in)));
		
		String[] header = csvIn.readNext();
		
		// validate that all dimensions are there and build a measure map.
		Map<IDimension, Integer> dimRef = new HashMap<IDimension, Integer>();
		Map<IMeasure, Integer> meRef = new HashMap<IMeasure, Integer>();
		
		IDataPool dataPool = cube.getDataPool();
		
		int idx = 0;
		for (String key : header) {
			// check if its a dimension
			if (dataPool.containsDimension(key)) {
				IDimension dim = dataPool.getDimension(key);
				if (!dimRef.containsKey(dim)) {
					dimRef.put(dim, idx);
				}
			} else if (dataPool.containsMeasure(key)) {
				IMeasure measure = dataPool.getMeasure(key);
				if (!meRef.containsKey(measure)) {
					meRef.put(measure, idx);
				}
			}
			idx++;
		}
		
		// check if all dimensions in the cube are represented in the file
		
		for (IDimension dim : cube.getDimensions()) {
			if (!dimRef.containsKey(dim)) {
				throw new ImportException("The file does not contain the dimension '" + dim.getKey() + "', which is defined in the cube.");
			}
		}
		
		if (meRef.size() == 0) {
			throw new ImportException("The file does not contain any measure data that is defined by the cube.");
		}
		
		// start reading
		String[] data;
		Key key = cube.createKey("");
		
		int lines = 0;
		while ((data = csvIn.readNext()) != null && lines < testReads) {
			
			int keyIdx = 0;
			for (IDimension dim : cube.getDimensions()) {
				idx = dimRef.get(dim);
				String[] path = data[idx].split("/");
				IDimensionElement elm = dim;
				for (String s : path) {
					if (elm.containsDimensionElement(s)) {
						elm = elm.getDimensionElement(s);
					} else {
						elm = null;
					}
				}
				if (elm == null) { // not found & no auto create
					continue; // skip this record.
				}
				key.setDimensionElement(keyIdx, elm);
				keyIdx++;
			}
			if (!key.isLeaf()) {
				System.err.println("Key is not a leaf: " + key);
			}
			cube.getCellValue(key, meBookings);
			lines++;
		}
		
		csvIn.close();
		in.close();
	}

}
