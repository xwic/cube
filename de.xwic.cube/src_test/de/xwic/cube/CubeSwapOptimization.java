/**
 * 
 */
package de.xwic.cube;

import java.io.File;

import junit.framework.TestCase;

import de.xwic.cube.impl.CubeSwapIndexed;
import de.xwic.cube.storage.impl.FileDataPoolStorageProvider;

/**
 * @author lippisch
 *
 */
public class CubeSwapOptimization extends TestCase {

	/**
	 * @param args
	 */
	public void testSpeed() throws Exception {

		File dataDir = new File("C:/Users/lippisch/development/ngsdb2/workspace/com.netapp.ngsdb.server/web_root/WEB-INF/xcube/2011-05-19 213612.datapool");
		System.out.println(dataDir.getAbsolutePath());
		
		IDataPoolStorageProvider storageProvider = new FileDataPoolStorageProvider(dataDir);
		IDataPoolManager dpm = DataPoolManagerFactory.createDataPoolManager(storageProvider);
		long start = System.currentTimeMillis();
		IDataPool pool = dpm.getDataPool("ngsdb");
		System.out.println("Load time: " + (System.currentTimeMillis() - start) + "ms");
		
		IDimension dimEmpl = pool.getDimension("UtilEmployee");
		IMeasure meHours = pool.getMeasure("Hours");
		ICube cube = pool.getCube("Utilization");
		// start scan
		
		Key key = cube.createKey("");//[TimeFW:2011/Q4/Mar]");
		int emplIdx = cube.getDimensionIndex(dimEmpl);
		
		start = System.currentTimeMillis();
		// loop through all employees
//		for (IDimensionElement de : dimEmpl.getDimensionElements()) {
//			key.setDimensionElement(emplIdx, de);
//			Double d = cube.getCellValue(key, meHours);
//		}
		Double d = cube.getCellValue(key, meHours);
		
		System.out.println("Total Time: " + (System.currentTimeMillis() - start) + "ms");
		((CubeSwapIndexed)cube).printStats(System.out);
		

	}

}
