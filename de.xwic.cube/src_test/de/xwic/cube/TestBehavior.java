/**
 * 
 */
package de.xwic.cube;

import junit.framework.TestCase;

/**
 * @author lippisch
 *
 */
public class TestBehavior extends TestCase {

	public void testBehavior() {
		
		assertFalse(DimensionBehavior.DEFAULT.isFlagged(DimensionBehavior.FLAG_NO_AGGREGATION));
		assertFalse(DimensionBehavior.DEFAULT.isFlagged(DimensionBehavior.FLAG_NO_SPLASH));
		assertTrue(DimensionBehavior.NO_SPLASH.isFlagged(DimensionBehavior.FLAG_NO_SPLASH));
		assertFalse(DimensionBehavior.NO_SPLASH.isFlagged(DimensionBehavior.FLAG_NO_AGGREGATION));
		assertTrue(DimensionBehavior.FLAT.isFlagged(DimensionBehavior.FLAG_NO_AGGREGATION));
		assertTrue(DimensionBehavior.FLAT.isFlagged(DimensionBehavior.FLAG_NO_SPLASH));
		
	}
	
}
