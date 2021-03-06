/**
 * 
 */
package de.xwic.cube.webui.controls.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import de.jwic.controls.InputBox;
import de.jwic.controls.Label;
import de.jwic.controls.ListBox;
import de.jwic.controls.ToolBarGroup;
import de.jwic.controls.Window;
import de.jwic.data.ISelectElement;
import de.jwic.events.ElementSelectedEvent;
import de.jwic.events.ElementSelectedListener;
import de.jwic.events.SelectionEvent;
import de.jwic.events.SelectionListener;

/**
 * @author bogdan
 *
 */
public class FilterGroup {

	public static final String PLEASE_SELECT_A_FILTER = "Default Filter";
	public static final String PLEASE_SELECT_A_FILTER_KEY = "__EMPTY__";

	private final Log log = LogFactory.getLog(FilterGroup.class);
	private final Map<String, FilterState> filters;
	private final Map<String, String> filterProfiles;
	private final List<IFilterGroupListener> listeners;
	private LoadProfileControl loadProfileControl;
	private ListBox listBox;

	/**
	 * 
	 * @author bogdan
	 *
	 */
	private static final class FilterState {

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
		filters = new LinkedHashMap<String, FilterState>();
		listeners = new ArrayList<IFilterGroupListener>();
		filterProfiles = new HashMap<String, String>();
	}

	/** Adds a new filter to the filter group.
	 *  The order in which the filters are added is the order in which applyFilter is executed.
	 *
	 * @param filter
	 */
	public void addFilter(IFilter filter) {
		filter.setInGroup(true);
		this.filters.put(filter.getId(), new FilterState(filter, filter.save()));
	}

	/**
	 * Removes all the filters and resets the profiles default
	 */
	public void clearFilters() {
		for (IFilter f : getFilters()) {
			this.removeFilter(f);
		}
		this.setFilterProfies(new ArrayList<FilterGroupProfile>());
	}

	/**
	 * @param filter
	 */
	public void removeFilter(IFilter filter) {
		filter.setInGroup(false);
		this.filters.remove(filter.getId());
	}

	/**
	 * @param filterGroupListener
	 */
	public void addListener(IFilterGroupListener filterGroupListener) {
		this.listeners.add(filterGroupListener);
	}

	/**
	 * @param filterGroupListener
	 */
	public void removeListener(IFilterGroupListener filterGroupListener) {
		this.listeners.remove(filterGroupListener);
	}

	/**
	 * 
	 */
	public void applyAllFilters() {

		for (FilterState f : this.filters.values()) {
			IFilter filter = f.filter;
			this.notifyFilterListeners(filter);
			filter.applyFilter();
		}
	}

	/**
	 * Load filterProfiles
	 *
	 * @param filterProfiles
	 */
	public void setFilterProfies(List<FilterGroupProfile> profiles) {
		this.filterProfiles.clear();
		if (listBox != null) {
			listBox.clear();
			listBox.addElement(PLEASE_SELECT_A_FILTER, PLEASE_SELECT_A_FILTER_KEY);
		}
		for (FilterGroupProfile fgp : profiles) {
			this.filterProfiles.put(fgp.getName(), fgp.getProfile());
			if (listBox != null) {
				listBox.addElement(fgp.getName(), fgp.getName());
			}
		}
		if (listBox != null)
			listBox.requireRedraw();

	}

	public List<FilterGroupProfile> getFilterProfiles() {
		List<FilterGroupProfile> profiles = new ArrayList<FilterGroupProfile>();
		for (Entry<String, String> profs : this.filterProfiles.entrySet()) {
			profiles.add(new FilterGroupProfile(profs.getKey(), profs.getValue()));
		}
		return profiles;
	}

	/**
	 * 
	 */
	public void resetFilters() {
		for (FilterState fs : this.filters.values()) {
			String state = fs.defaultState;
			IFilter filter = fs.filter;
			filter.load(state);
			if (listBox != null)
				listBox.selectedByKey(PLEASE_SELECT_A_FILTER_KEY);
		}
		this.applyAllFilters();
	}

	/**
	 * @param toolBar
	 * @param saveButtonText
	 *            the text of the save Button
	 * @param saveButtonIcon
	 *            the icon of the save Button
	 * @param saveWindowText
	 *            the text on the popup window
	 * @return
	 */
	public Button createSaveButton(ToolBarGroup toolBar, final String saveButtonText, final ImageRef saveButtonIcon,
			final String saveWindowText) {
		final Button save = toolBar.addButton();
		//first container is the toolbar. the second one is the 'page'
		final IControlContainer toolBarParent = toolBar.getContainer();
		final Window window = new Window(toolBarParent);
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

			/**
			 *
			 */
			private static final long serialVersionUID = -6169486348404140682L;

			@Override
			public void objectSelected(SelectionEvent arg0) {
				final String filterName = windowContent.getFilterName().getText();

				if (!"".equals(filterName)) {

					final String filter = saveInternal();
					final FilterGroupProfile fgp = new FilterGroupProfile(filterName, filter);
					try {
						filterProfiles.put(filterName, filter);
						for (IFilterGroupListener fgl : listeners) {
							fgl.saveFilterProfile(fgp);
						}
					} catch (Exception ex) {
						log.error(ex.getMessage(), ex);
						filterProfiles.remove(filterName);
					}
					window.setVisible(false);
					windowContent.getFilterName().setText("");
					if (loadProfileControl != null) {
						loadProfileControl.getProfileListBox().addElement(fgp.getName(), fgp.getName());
					}

				} else {
					windowContent.getErrorText().setText("Profile name must not be empty!");
					windowContent.getErrorText().setVisible(true);
				}
			}
		});
		cancel.addSelectionListener(new SelectionListener() {

			/**
			 *
			 */
			private static final long serialVersionUID = 2961108068441208283L;

			@Override
			public void objectSelected(SelectionEvent arg0) {
				window.setVisible(false);
			}
		});

		save.setTitle(saveButtonText);
		save.setIconEnabled(saveButtonIcon);
		save.addSelectionListener(new SelectionListener() {

			/**
			 *
			 */
			private static final long serialVersionUID = 6406327521909445169L;

			@Override
			public void objectSelected(SelectionEvent arg0) {
				windowContent.getErrorText().setVisible(false);
				prePopulateFilterNameField(windowContent);
				window.setVisible(true);
			}
		});

		return ok;
	}

	/**
	 * @param toolBar
	 * @return
	 */
	public Button createSaveButton(ToolBarGroup toolBar) {
		return this.createSaveButton(toolBar, "Save Profile", null, "Save the current profile as:");
	}

	/**
	 * @param toolBarGroup
	 * @param loadButtonText
	 * @param loadButtonImage
	 * @return
	 */
	public Button createLoadButton(ToolBarGroup toolBarGroup, String loadButtonText, ImageRef loadButtonImage) {
		loadProfileControl = new LoadProfileControl(toolBarGroup);
		Button load = loadProfileControl.getLoadButton();
		listBox = loadProfileControl.getProfileListBox();//setup the initial stuff if any
		listBox.addElement(PLEASE_SELECT_A_FILTER, PLEASE_SELECT_A_FILTER_KEY);
		for (Entry<String, String> filter : this.filterProfiles.entrySet()) {
			listBox.addElement(filter.getKey(), filter.getKey());
		}

		load.setTitle(loadButtonText);
		load.setIconEnabled(loadButtonImage);
		load.addSelectionListener(new SelectionListener() {

			/**
			 *
			 */
			private static final long serialVersionUID = -7487554149107137750L;

			@Override
			public void objectSelected(SelectionEvent arg0) {
				ISelectElement selectedElement = listBox.getSelectedElement();
				if (selectedElement != null) {
					String key = selectedElement.getKey();
					if (!PLEASE_SELECT_A_FILTER_KEY.equals(key)) {
						loadInternal(filterProfiles.get(key));

					} else {
						resetFilters();
					}
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

	public Button createDeleteButton(ToolBarGroup group) {

		Button delete = group.addButton();

		loadProfileControl.getProfileListBox().addElementSelectedListener(new ElementSelectedListener() {

			/**
			 *
			 */
			private static final long serialVersionUID = 8575744004971747801L;

			@Override
			public void elementSelected(ElementSelectedEvent event) {
				if (loadProfileControl.getProfileListBox().getSelectedKey().equals(FilterGroup.PLEASE_SELECT_A_FILTER_KEY)
						|| "".equals(loadProfileControl.getProfileListBox().getSelectedKey())) {

					delete.setVisible(false);
				} else {
					delete.setVisible(true);
				}
			}
		});

		delete.setTitle("Delete filter");
		delete.setIconEnabled(new ImageRef("icons/delete_a.png"));
		delete.addSelectionListener(new SelectionListener() {

			/**
			 *
			 */
			private static final long serialVersionUID = -6357747490096189511L;

			@Override
			public void objectSelected(SelectionEvent arg0) {
				String currentSelection = FilterGroup.this.loadProfileControl.getProfileListBox().getSelectedKey();

				filterProfiles.remove(currentSelection);

				try {

					for (IFilterGroupListener fgl : listeners) {
						fgl.saveFilterProfile(null);
					}

					loadProfileControl.getProfileListBox().removeElementByKey(currentSelection);
					loadProfileControl.requireRedraw();

				} catch (FilterGroupException ex) {
					log.error(ex.getMessage(), ex);
				}
			}
		});

		delete.setVisible(false);
		delete.setIconDisabled(new ImageRef("icons/delete_i.png"));
		return delete;
	}

	/**
	 * @return
	 */
	public List<IFilter> getFilters() {
		List<IFilter> theFilters = new ArrayList<IFilter>();
		for (FilterState fs : this.filters.values()) {
			theFilters.add(fs.filter);
		}
		return Collections.unmodifiableList(theFilters);
	}

	/**
	 * @param filter
	 */
	private void notifyFilterListeners(IFilter filter) {
		for (IFilterGroupListener fl : this.listeners) {
			fl.preApply(filter);
		}
	}

	/**
	 * @return
	 */
	private String saveInternal() {
		JSONObject object = new JSONObject();

		for (FilterState filter : this.filters.values()) {
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
	private void loadInternal(String s) {
		try {
			JSONObject object = new JSONObject(s);
			Iterator<String> keys = object.keys();
			while (keys.hasNext()) {
				String string = keys.next();
				FilterState filterState = filters.get(string);

				//the view filters are null if the view is not displayed until required global filters are entered
				if (null != filterState) {
					IFilter filter = filterState.filter;
					filter.load(object.getString(string));
				}
			}
			applyAllFilters();
		} catch (JSONException e) {
			log.error(e);
		}

	}

	private void prePopulateFilterNameField(PopUpContent windowContent) {

		InputBox filterNameField = windowContent.getFilterName();
		Label infoText = windowContent.getInfoText();

		ISelectElement selectedElement = listBox.getSelectedElement();
		if (selectedElement != null) {
			String key = selectedElement.getKey();
			if (!PLEASE_SELECT_A_FILTER_KEY.equals(key)) {
				filterNameField.setText(key);
				infoText.setText("Overwrite the current profile:");
			} else {
				filterNameField.setText("");
				infoText.setText("Save the current profile as:");
			}
		}
	}
}
