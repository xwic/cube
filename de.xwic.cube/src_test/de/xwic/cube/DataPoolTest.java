/**
 * 
 */
package de.xwic.cube;

import java.io.File;

import junit.framework.TestCase;
import de.xwic.cube.formatter.PercentageValueFormatProvider;
import de.xwic.cube.functions.DifferenceFunction;
import de.xwic.cube.storage.impl.FileDataPoolStorageProvider;

/**
 * @author Florian Lippisch
 */
public class DataPoolTest extends TestCase {

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
		
	}
	
	public void testDimensions() {

		dimOT = pool.createDimension("OrderType");
		IDimensionElement deAOO = dimOT.createDimensionElement("AOO");	
		IDimensionElement deCOO = dimOT.createDimensionElement("COO");
		
		System.out.println(deAOO + ", " + deCOO);
		
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

		IDimensionElement deOther = (IDimensionElement) pool.getObject(deAOO.getObjectId());
		assertSame(deOther, deAOO);
		
		deCOO.remove();
		assertNull(pool.getObject(deCOO.getObjectId()));
		
	}
	
	
}
