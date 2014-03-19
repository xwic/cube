/**
 * 
 */
package de.xwic.cube.webui.controls.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import de.jwic.base.IControlContainer;
import de.jwic.base.ImageRef;
import de.jwic.controls.Button;
import de.jwic.controls.ListBox;
import de.jwic.controls.ToolBarGroup;
import de.jwic.controls.Window;
import de.jwic.data.ISelectElement;
import de.jwic.events.SelectionEvent;
import de.jwic.events.SelectionListener;

/**
 * @author bogdan
 *
 */
public class FilterGroup  {
	private final Log log = LogFactory.getLog(FilterGroup.class);
	private final Map<String,FilterState> filters;
	private final Map<String, String> filterProfiles;
	private final List<IFilterGroupListener> listeners;
	private LoadProfileControl loadProfileControl;
	
	
	/**
	 * 
	 * @author bogdan
	 *
	 */
	private static final class FilterState{
		final IFilter filter;
		final String defaultState;
		public FilterState(IFilter filter, String defaultState) {
			super();
			this.filter = filter;
			this.defaultState = defaultState;
		}
		
	}
	
	/**
	 * 
	 */
	public FilterGroup() {
		filters = new HashMap<String, FilterState>();
		listeners = new ArrayList<IFilterGroupListener>();
		filterProfiles = new HashMap<String, String>();
	}
	
	/**
	 * @param filter
	 */
	public void addFilter(IFilter filter){
		filter.setInGroup(true);
		this.filters.put(filter.getId(),new FilterState(filter, filter.save()));
	}
	
	/**
	 * @param filter
	 */
	public void removeFilter(IFilter filter){
		filter.setInGroup(false);
		
		this.filters.remove(filter);
	}
	
	/**
	 * @param filterGroupListener
	 */
	public void addListener(IFilterGroupListener filterGroupListener){
		this.listeners.add(filterGroupListener);
	}
	
	/**
	 * @param filterGroupListener
	 */
	public void removeListener(IFilterGroupListener filterGroupListener){
		this.listeners.remove(filterGroupListener);
	}
	
	/**
	 * @param filter
	 */
	private void notifyFilterListeners(IFilter filter){
		for(IFilterGroupListener fl : this.listeners){
			fl.preApply(filter);
		}
	}
	
	/**
	 * 
	 */
	public void applyAllFilters(){
		
		for(FilterState f : this.filters.values()){
			IFilter filter = f.filter;
			this.notifyFilterListeners(filter);
			filter.applyFilter();
		}
	}
	
	/**
	 * @return
	 */
	public String save(){
		JSONObject object = new JSONObject();
		for(FilterState filter : this.filters.values()){
			IFilter f = filter.filter;
			try {
				object.put(f.getId(), f.save());
			} catch (JSONException e) {
				
			}
		}
		return object.toString();
	}
	
	/**
	 * @param s
	 */
	public void load(String s){
		try {
			JSONObject object = new JSONObject(s);
			Iterator<String> keys = object.keys();
			while (keys.hasNext()) {
				String string = keys.next();
				FilterState filterState =  filters.get(string);
				IFilter filter=  filterState.filter;
				filter.load(object.getString(string));
			}
		} catch (JSONException e) {
			log.error(e);
		}
		
	}
	/**
	 * Load filterProfiles
	 * @param filterProfiles
	 */
	public void setFilterProfies(List<FilterGroupProfile> profiles){
		this.filterProfiles.clear();
		for(FilterGroupProfile fgp : profiles){
			this.filterProfiles.put(fgp.getName(), fgp.getProfile());
		}
	}
	
	/**
	 * 
	 */
	public void resetFilters(){
		for(FilterState fs : this.filters.values()){
			String state = fs.defaultState;
			IFilter filter= fs.filter;
			filter.load(state);
		}
	}
	
	/**
	 * @param toolBar
	 * @param saveButtonText the text of the save Button
	 * @param saveButtonIcon the icon of the save Button
	 * @param saveWindowText the text on the popup window
	 * @return
	 */
	public Button createSaveButton(ToolBarGroup toolBar,final String saveButtonText, final ImageRef saveButtonIcon, final String saveWindowText){
		final Button save = toolBar.addButton();
		//first container is the toolbar. the second one is the 'page'
		final IControlContainer toolBarParent = toolBar.getContainer();
		final Window window = new Window(toolBarParent,"popUp");
		window.setVisible(false);
		window.setMaximizable(false);
		window.setMinimizable(false);
		window.setResizable(false);
		window.setDraggable(false);
		window.setCloseable(false);
		window.setWidth(500);
		window.setTitle(saveButtonText);
		
		
		final PopUpContent windowContent = new PopUpContent(window);
		windowContent.getInfoText().setText(saveWindowText);
		Button ok = windowContent.getOk();
		Button cancel = windowContent.getCancel();
		ok.addSelectionListener(new SelectionListener() {
			
			@Override
			public void objectSelected(SelectionEvent arg0) {
				final String filterName = windowContent.getFilterName().getText();
				final String filter = save();
				final FilterGroupProfile fgp = new FilterGroupProfile(filterName, filter);
				try{
					for(IFilterGroupListener fgl : listeners){
						fgl.saveFilterProfile(fgp);
					}
					filterProfiles.put(filterName, filter);
				}catch(Exception ex){
					log.error(ex.getMessage(),ex);
				}
				window.setVisible(false);
				windowContent.getFilterName().setText("");
				if(loadProfileControl!=null){
					loadProfileControl.getProfileListBox().addElement(fgp.getName(), fgp.getProfile());
				}
			}
		});
		cancel.addSelectionListener(new SelectionListener() {
			
			@Override
			public void objectSelected(SelectionEvent arg0) {
				window.setVisible(false);
			}
		});
		
		save.setTitle(saveButtonText);
		save.setIconEnabled(saveButtonIcon);
		save.addSelectionListener(new SelectionListener() {
			
			@Override
			public void objectSelected(SelectionEvent arg0) {
				window.setVisible(true);
			}
		});
		return ok;
	}
	
	/**
	 * @param toolBar
	 * @return
	 */
	public Button createSaveButton(ToolBarGroup toolBar){
		return this.createSaveButton(toolBar, "Save Profile", null, "Save the current profile as:");
	}
	
	/**
	 * @param toolBarGroup
	 * @param loadButtonText
	 * @param loadButtonImage
	 * @return
	 */
	public Button createLoadButton(ToolBarGroup toolBarGroup, String loadButtonText, ImageRef loadButtonImage){
		loadProfileControl = new LoadProfileControl(toolBarGroup);
		Button load = loadProfileControl.getLoadButton();
		final ListBox listBox = loadProfileControl.getProfileListBox();
		for(Entry<String, String> filter : this.filterProfiles.entrySet()){
			listBox.addElement(filter.getKey(), filter.getValue());
		}
		
		load.setTitle(loadButtonText);
		load.setIconDisabled(loadButtonImage);
		load.addSelectionListener(new SelectionListener() {
			
			@Override
			public void objectSelected(SelectionEvent arg0) {
				ISelectElement selectedElement = listBox.getSelectedElement();
				if(selectedElement != null){
					String key = selectedElement.getKey();
					load(key);
				}
			}
		});
		
		return load;
	}
	
	
	/**
	 * @param group
	 * @return
	 */
	public Button createLoadButton(ToolBarGroup group) {
		return this.createLoadButton(group, "Load Profile", null);
	}

}