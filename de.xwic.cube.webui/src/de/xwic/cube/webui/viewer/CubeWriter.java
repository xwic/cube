/*
 * de.xwic.cube.webui.viewer.CubeWriter 
 */
package de.xwic.cube.webui.viewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.jwic.base.ControlContainer;
import de.jwic.base.IControlContainer;
import de.jwic.controls.ButtonControl;
import de.jwic.controls.InputBoxControl;
import de.jwic.controls.ListBoxControl;
import de.jwic.events.SelectionEvent;
import de.jwic.events.SelectionListener;
import de.xwic.cube.ICube;
import de.xwic.cube.IDimension;
import de.xwic.cube.IMeasure;
import de.xwic.cube.Key;
import de.xwic.cube.webui.controls.ClassicDimensionSelectorControl;

/**
 * @author lippisch
 */
public class CubeWriter extends ControlContainer {

	
	private final CubeViewerModel model;
	private ListBoxControl lbcMeasures;
	private InputBoxControl inpValue;
	
	private Map<IDimension, ClassicDimensionSelectorControl> selectors = new HashMap<IDimension, ClassicDimensionSelectorControl>();

	/**
	 * @param container
	 * @param name
	 */
	public CubeWriter(IControlContainer container, String name, CubeViewerModel model) {
		super(container, name);
		this.model = model;
		
		lbcMeasures = new ListBoxControl(this, "lbcMeasure");
		inpValue = new InputBoxControl(this, "inpValue");
		
		ButtonControl btWrite = new ButtonControl(this, "btWrite");
		btWrite.setTitle("Update");
		btWrite.addSelectionListener(new SelectionListener() {
			public void objectSelected(SelectionEvent event) {
				onWriteAction();
			}
		});
		
		createControls(model.getCube());
		
	}

	/**
	 * 
	 */
	protected void onWriteAction() {
		
		ICube cube = model.getCube();
		if (cube != null) {
			
			// build a key
			StringBuilder sbKey = new StringBuilder();
			for (IDimension dim : cube.getDimensions() ) {
				sbKey.append(selectors.get(dim).getSelectedId());
			}
			
			Key key = cube.createKey(sbKey.toString());
			
			String sValue = inpValue.getText();
			if (sValue.trim().length() == 0) {
				IMeasure measure = cube.getDataPool().getMeasure(lbcMeasures.getSelectedKey());
				cube.clear(measure, key);
				model.notifyCubeUpdated();
			} else {
				double value = Double.parseDouble(sValue);
				IMeasure measure = cube.getDataPool().getMeasure(lbcMeasures.getSelectedKey());
				cube.setCellValue(key, measure, value);
				model.notifyCubeUpdated();
			}
		}
		
	}

	/**
	 * @param cube
	 */
	private void createControls(ICube cube) {

		if (cube == null) {
			// maybe later implement a listener to cube changes for later updates.
			throw new IllegalStateException("Cube must be set in the model.");
			//return; 
		}
		for (IMeasure measure : cube.getMeasures()) {
			if (!measure.isFunction()) {
				lbcMeasures.addElement(measure.getTitle() != null && measure.getTitle().length() > 0 ? measure.getTitle() : measure.getKey(), measure.getKey());
			}
		}
		if (model.getMeasure() != null) {
			lbcMeasures.setSelectedKey(model.getMeasure().getKey());
		}
		
		
		for (IDimension dim : cube.getDimensions()) {
			
			ClassicDimensionSelectorControl dimSel = new ClassicDimensionSelectorControl(this, "sel_" + dim.getKey(), dim);
			selectors.put(dim, dimSel);
			
		}
		
		
	}

	/**
	 * Returns the list of dimensions.
	 * @return
	 */
	public Collection<IDimension> getDimensions() {
		if (model.getCube() != null) {
			return model.getCube().getDimensions();
		}
		return new ArrayList<IDimension>();
	}
	
}
