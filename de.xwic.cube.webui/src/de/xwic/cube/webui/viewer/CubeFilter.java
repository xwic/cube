/**
 * 
 */
package de.xwic.cube.webui.viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jwic.base.ControlContainer;
import de.jwic.base.IControlContainer;
import de.jwic.controls.ListBoxControl;
import de.jwic.events.ElementSelectedEvent;
import de.jwic.events.ElementSelectedListener;
import de.xwic.cube.IDimension;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.IMeasure;
import de.xwic.cube.webui.controls.DimensionSelectorControl;

/**
 * @author Florian Lippisch
 */
public class CubeFilter extends ControlContainer {

	private static final long serialVersionUID = 1L;
	private final CubeViewerModel model;

	private ListBoxControl lbcMeasures = null;
	private List<IDimension> dimensions = new ArrayList<IDimension>(); 
	private Map<String, String> dimCtrlMap = new HashMap<String, String>();
	private boolean selectMeasure = true;
	
	/**
	 * @param container
	 * @param name
	 */
	public CubeFilter(IControlContainer container, String name, CubeViewerModel model) {
		super(container, name);
		this.model = model;
	
		model.addCubeViewerModelListener(new CubeViewerModelAdapter() {
			public void filterUpdated(CubeViewerModelEvent event) {
				onFilterUpdate(event);
			}
		});
		
		lbcMeasures = new ListBoxControl(this, "lbcMeasure");
		lbcMeasures.setChangeNotification(true);
		for (IMeasure measure : model.getCube().getMeasures()) {
			lbcMeasures.addElement(measure.getTitle() != null && measure.getTitle().length() > 0 ? measure.getTitle() : measure.getKey(), measure.getKey());
		}
		if (model.getMeasure() != null) {
			lbcMeasures.setSelectedKey(model.getMeasure().getKey());
		}
		lbcMeasures.addElementSelectedListener(new ElementSelectedListener() {
			public void elementSelected(ElementSelectedEvent event) {
				onMeasureSelect((String)event.getElement());
			}
		});
		
	}

	/**
	 * @param event
	 */
	protected void onFilterUpdate(CubeViewerModelEvent event) {
		
		if (isSelectMeasure() && !model.getMeasure().getKey().equals(lbcMeasures.getSelectedKey())) {
			lbcMeasures.setSelectedKey(model.getMeasure().getKey());
		}
		
	}

	/**
	 * @param element
	 */
	protected void onMeasureSelect(String key) {
		if (!key.equals(model.getMeasure().getKey())) {
			model.setMeasure(model.getCube().getDataPool().getMeasure(key));
		}
	}

	/**
	 * Add a dimension to the filter control. Creates a combobox so that the user can filter
	 * that dimension.
	 * @param dimension
	 */
	public void addDimension(IDimension dimension) {
		dimensions.add(dimension);
		
		DimensionSelectorControl dsc = new DimensionSelectorControl(this, null, dimension);
		dsc.addElementSelectedListener(new ElementSelectedListener() {
			public void elementSelected(ElementSelectedEvent event) {
				filterSelection((String)event.getElement());
			}
		});
		dimCtrlMap.put(dimension.getKey(), dsc.getName());
		
	}
	
	/**
	 * @param element
	 */
	protected void filterSelection(String id) {
		
		IDimensionElement selected = model.getCube().getDataPool().parseDimensionElementId(id); 
		model.applyFilter(selected);
	}

	/**
	 * Returns the name of the control that contains the filter
	 * selection for the specified dimension.
	 * @param dimensionKey
	 * @return
	 */
	public String getControlName(String dimensionKey) {
		return dimCtrlMap.get(dimensionKey);
	}

	/**
	 * @return the dimensions
	 */
	public List<IDimension> getDimensions() {
		return dimensions;
	}

	/**
	 * @return the selectMeasure
	 */
	public boolean isSelectMeasure() {
		return selectMeasure;
	}

	/**
	 * @param selectMeasure the selectMeasure to set
	 */
	public void setSelectMeasure(boolean selectMeasure) {
		this.selectMeasure = selectMeasure;
	}
	
	
}
