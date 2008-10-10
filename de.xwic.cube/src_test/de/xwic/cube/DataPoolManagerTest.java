/**
 * 
 */
package de.xwic.cube;

import junit.framework.TestCase;
import de.xwic.cube.util.DataDump;

/**
 * @author Florian Lippisch
 */
public class DataPoolManagerTest extends TestCase {

	@SuppressWarnings("unused")
	public void testCreateDataPool() {
		
		IDataPoolManager manager = DataPoolManagerFactory.createDataPoolManager();
		IDataPool pool = manager.createDataPool("test");
		assertNotNull(pool);
		assertEquals("test", pool.getKey());
		
		IDimension dimOT = pool.createDimension("OrderType");
		assertNotNull(dimOT);
		assertEquals("OrderType", dimOT.getKey());
		
		assertEquals(1, pool.getDimensions().size());
		
		IDimensionElement elmAOO = dimOT.createDimensionElement("AOO");	
		IDimensionElement elmCOO = dimOT.createDimensionElement("COO");
		
		assertEquals(2, dimOT.getDimensionElements().size());
		
		
		IDimension dimLOB = pool.createDimension("LOB");
		IDimensionElement elmHW = dimLOB.createDimensionElement("Hardware");
		IDimensionElement elmPS = dimLOB.createDimensionElement("PS");
		IDimensionElement elmInst = elmPS.createDimensionElement("Installation");
		IDimensionElement elmConsulting = elmPS.createDimensionElement("Consulting");
		elmPS.createDimensionElement("Service");
		IDimensionElement elmED = dimLOB.createDimensionElement("Education");
		 
		DataDump.printStructure(System.out, dimLOB);
		
		IMeasure meBookings = pool.createMeasure("Bookings");
		
		// now create a cube
		
		ICube cube = pool.createCube("test", new IDimension[] { dimOT, dimLOB }, new IMeasure[] { meBookings });
		assertNotNull(cube);
		
		Key key = new Key(new IDimensionElement[] { elmAOO, elmInst });
		cube.setCellValue(key, meBookings, 100);
		
		key = new Key(new IDimensionElement[] { elmAOO, elmED });
		cube.setCellValue(key, meBookings, 200);

		Double value = cube.getCellValue(key, meBookings);
		assertNotNull(value);
		assertEquals(200.0, value.doubleValue());
		
		key = new Key(new IDimensionElement[] { elmAOO, elmConsulting });
		cube.setCellValue(key, meBookings, 50);

		
		key = new Key(new IDimensionElement[] { elmAOO, dimLOB });
		value = cube.getCellValue(key, meBookings);
		assertNotNull(value);
		assertEquals(350.0, value.doubleValue());
		
	}
	
}
