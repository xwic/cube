/**
 * 
 */
package de.xwic.cube;

import java.io.Serializable;

/**
 * Controls how a dimension is managed within a cube.  
 * 
 * @author lippisch
 */
public class DimensionBehavior implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2231495297452220437L;
	
	public final static int FLAG_DEFAULT = 0;
	public final static int FLAG_NO_SPLASH = 1;
	public final static int FLAG_NO_AGGREGATION = 2 * 1;

	
	public final static DimensionBehavior DEFAULT = new DimensionBehavior(FLAG_DEFAULT);
	public final static DimensionBehavior NO_SPLASH = new DimensionBehavior(FLAG_NO_SPLASH);
	public final static DimensionBehavior NO_AGGREGATION = new DimensionBehavior(FLAG_NO_AGGREGATION);
	public final static DimensionBehavior FLAT = new DimensionBehavior(FLAG_NO_SPLASH | FLAG_NO_AGGREGATION);
	
	protected int behavior = FLAG_DEFAULT;
	
	/**
	 * Constructor.
	 * @param behaivior
	 */
	public DimensionBehavior(int behaivior) {
		this.behavior = behaivior;
	}

	/**
	 * Check if the specified flag is defined in this behaivior.
	 * @param flag
	 * @return
	 */
	public boolean isFlagged(int flag) {
		return (behavior & flag) != 0;
	}

	/**
	 * @return the behavior
	 */
	public int getBehavior() {
		return behavior;
	}
	
}
