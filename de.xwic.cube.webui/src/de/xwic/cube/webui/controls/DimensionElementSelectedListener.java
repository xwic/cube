/*
 * Copyright (c) 2009 Network Appliance, Inc.
 * All rights reserved.
 */
package de.xwic.cube.webui.controls;

import java.io.Serializable;

/**
 * 
 * This listener listens to the selection of an dimension element.
 * 
 * @author mirceas
 * 
 */
public interface DimensionElementSelectedListener extends Serializable {

	/**
	 * Listens to the selection of an dimension element.
	 * @param event
	 */
	public abstract void elementSelected(DimensionElementSelectedEvent event);

}
