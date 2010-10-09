/**
 * 
 */
package de.xwic.cube;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;
import de.xwic.cube.formatter.PercentageValueFormatProvider;
import de.xwic.cube.functions.DifferenceFunction;
import de.xwic.cube.impl.Cell;
import de.xwic.cube.impl.IndexedDataTable;
import de.xwic.cube.storage.impl.FileDataPoolStorageProvider;
import de.xwic.cube.util.DataDump;

/**
 * @author Florian Lippisch
 */
public class IndexedDataTableTest extends TestCase {

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
		
		cube = pool.createCube("test", new IDimension[] { dimOT, dimLOB, dimTime }, new IMeasure[] { meBookings, mePlan, meDiff });
		
	}
	
	public void testIndex() {
		
		IndexedDataTable table = new IndexedDataTable(cube.getDimensions().size(), cube.getMeasures().size());
		
		// now create 10 leaf entries
		Key[] keys = {
				cube.createKey("[OrderType:AOO][LOB:Hardware][Time:2008/Q1/Jan]"),
				cube.createKey("[OrderType:AOO][LOB:Education][Time:2008/Q1/Jan]"),
				cube.createKey("[OrderType:COO][LOB:Hardware][Time:2008/Q2/Jun]"),
				cube.createKey("[OrderType:AOO][LOB:PS/Installation][Time:2008/Q1/Jan]"),
				cube.createKey("[OrderType:AOO][LOB:Hardware][Time:2008/Q1/Jan]"),
				cube.createKey("[OrderType:AOO][LOB:PS/Consulting][Time:2008/Q1/Feb]"),
				cube.createKey("[OrderType:COO][LOB:Hardware][Time:2008/Q1/Jan]"),
				cube.createKey("[OrderType:COO][LOB:Hardware][Time:2008/Q2/Apr]"),
				cube.createKey("[OrderType:AOO][LOB:PS/Consulting][Time:2008/Q1/Jan]"),
				cube.createKey("[OrderType:COO][LOB:Hardware][Time:2008/Q2/May]"),
				cube.createKey("[OrderType:COO][LOB:PS/Installation][Time:2008/Q1/Jan]"),
				cube.createKey("[OrderType:COO][LOB:PS/Installation][Time:2008/Q1/Jan]"),
				cube.createKey("[OrderType:COO][LOB:PS/Service][Time:2008/Q1/Jan]")
		};
		
		for (Key key : keys) {
			cube.setCellValue(key, meBookings, 100);
			ICell cell = cube.getCell(key);
			
			table.put(key, cell);
			
		}
		
		table.dumpElements();
		System.out.println("---");
		table.buildIndex();
		table.dumpElements();
		
		// now read! :)
		Key key = cube.createKey("[OrderType:COO][LOB:Hardware]");
		System.out.println("Search for " + key);
		ICell cell = table.calcCell(key);
		Double value =  cell.getValue(cube.getMeasureIndex(meBookings));
		System.out.println("Result: " + value);
		
	}
		
}
