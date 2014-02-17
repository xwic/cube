/**
 * 
 */
package de.xwic.cube.webui.viewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.jwic.base.ControlContainer;
import de.jwic.base.IControlContainer;
import de.jwic.controls.ListBox;
import de.jwic.events.ElementSelectedEvent;
import de.jwic.events.ElementSelectedListener;
import de.xwic.cube.ICube;
import de.xwic.cube.IDimension;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.IMeasure;
import de.xwic.cube.webui.controls.DimensionElementSelector;
import de.xwic.cube.webui.controls.filter.IFilter;

/**
 * @author Florian Lippisch
 */
public class CubeFilter extends ControlContainer implements IFilter{

	private static final long serialVersionUID = 1L;
	private final CubeViewerModel model;

	private ListBox lbcMeasures = null;
	private List<IDimension> dimensions = new ArrayList<IDimension>(); 
	private final Map<String, DimensionElementSelector> dimCtrlMap = new HashMap<String, DimensionElementSelector>();
	private boolean selectMeasure = true;
	private boolean inGroup;
	
	/**
	 * @param container
	 * @param name
	 */
	public CubeFilter(IControlContainer container, String name, CubeViewerModel model) {
		this(container, name, model, model.getCube().getMeasures());
	}

	/**
	 * 
	 * @param container
	 * @param name
	 * @param model
	 * @param measures that should be selectable in the combobox.
	 */
	public CubeFilter(IControlContainer container, String name, CubeViewerModel model, Collection<IMeasure> measures) {
		super(container, name);
		this.model = model;
	
		model.addCubeViewerModelListener(new CubeViewerModelAdapter() {
			public void filterUpdated(CubeViewerModelEvent event) {
				onFilterUpdate(event);
			}
		});
		
		lbcMeasures = new ListBox(this, "lbcMeasure");
		lbcMeasures.setConfirmMsg("");
		
		for (IMeasure measure : measures) {
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
		lbcMeasures.setChangeNotification(true);
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
	public DimensionElementSelector addDimension(IDimension dimension) {
		return addDimension(dimension, null);
	}
	
	/**
	 * 
	 * @param dimension
	 * @param filter
	 * @return
	 */
	public DimensionElementSelector addDimension(IDimension dimension, IDimensionFilter filter) {
		dimensions.add(dimension);
		
		final DimensionElementSelector dsc = new DimensionElementSelector(this, null, dimension, filter);
		dsc.addElementSelectedListener(new ElementSelectedListener() {
			public void elementSelected(ElementSelectedEvent event) {
				if(!isInGroup())//not in group
					filterSelection(dsc);
			}
		});
		dimCtrlMap.put(dimension.getKey(), dsc);
		return dsc;
		
	}
	
	/**
	 * Add a LeafDimensionSelector for the specified dimension.
	 * @param dimension
	 * @param filter
	 * @deprecated Use addDimension(..).setSelectedLeafsOnly(true);
	 */
	public DimensionElementSelector addDimensionLeafSelector(IDimension dimension, IDimensionFilter filter) {
		DimensionElementSelector dsc = addDimension(dimension, filter);
		dsc.setSelectLeafsOnly(true);
		return dsc;
	}
	
	/**
	 * @param element
	 */
	protected void filterSelection(DimensionElementSelector selector) {
		List<IDimensionElement> elements = selector.getDimensionElements();
		handleSingleSelect(elements.get(0));
	}

	/**
	 * @param element
	 */
	private void handleSingleSelect(IDimensionElement element){
		model.applyFilter(element);
	}
	
	/**
	 * Returns the name of the control that contains the filter
	 * selection for the specified dimension.
	 * @param dimensionKey
	 * @return
	 */
	public String getControlName(String dimensionKey) {
		return dimCtrlMap.get(dimensionKey).getName();
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

	@Override
	public String getId() {
		return this.getControlID();
	}

	@Override
	public String save() {
		JSONObject object = new JSONObject(); 
		for(DimensionElementSelector selector : this.dimCtrlMap.values()){
			String id = selector.getDimension().getKey();
			List<String> dimElements = new ArrayList<String>();
			for(IDimensionElement elm : selector.getDimensionElements()){
				dimElements.add(elm.getPath());
			}
			try {
				object.put(id, dimElements);
			} catch (JSONException e) {
				log.error(e);
			}
		}
		
		return object.toString();
	}

	@Override
	public void load(String state) {
		try {
			JSONObject object = new JSONObject(state);
			Iterator<String> keys = object.keys();
			final ICube cube = model.getCube();
			while(keys.hasNext()){
				String dimId = keys.next();
				IDimension dim = null;
				for(IDimension d : cube.getDimensions()){
					if(d.getKey().equals(dimId)){
						dim = d;
						break;
					}
				}
				if(dim == null){
					continue;
				}
			
				JSONArray dimElements = object.getJSONArray(dimId);
				List<IDimensionElement> elements = new ArrayList<IDimensionElement>();
				for (int i = 0; i < dimElements.length(); i++) {
					try{
						elements.add(dim.parsePath(dimElements.getString(i)));
					}catch(IllegalArgumentException ex){
						log.error(ex);
						continue;
					}
				}
				this.dimCtrlMap.get(dimId).setDimensionElements(elements);
			}
			this.requireRedraw();
		} catch (JSONException e) {
			log.error(e);
		}
		
	}

	@Override
	public void applyFilter(){ 
	//apply the whole filter
		for(DimensionElementSelector selector : this.dimCtrlMap.values()){
			filterSelection(selector);
		}
	}

	@Override
	public boolean isInGroup() {
		return this.inGroup;
	}

	@Override
	public void setInGroup(boolean inGroup) {
		this.inGroup = inGroup;
	}
	
	
}
