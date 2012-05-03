/*
 * Copyright (c) 2009 Network Appliance, Inc.
 * All rights reserved.
 */
package de.xwic.cube;

/**
 * A function implementation for measure with usage of query (IQuery).
 * 
 * @author mirceas
 * 
 */
public interface IMeasureFunctionQuerySupport extends IMeasureFunction {

	/**
	 * Compute value from cube using query.
	 * @param cube
	 * @param query
	 * @return
	 */
	Double computeValue(ICube cube, IQuery query);

}
