package de.xwic.cube.webui.samples.demo1;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;

import de.jwic.base.Application;
import de.jwic.base.Control;
import de.jwic.base.IControlContainer;
import de.jwic.base.Page;
import de.jwic.controls.Button;
import de.jwic.controls.CheckBox;
import de.jwic.controls.ToolBar;
import de.jwic.events.SelectionEvent;
import de.jwic.events.SelectionListener;
import de.jwic.events.ValueChangedEvent;
import de.jwic.events.ValueChangedListener;
import de.xwic.cube.DataPoolManagerFactory;
import de.xwic.cube.ICube;
import de.xwic.cube.IDataPool;
import de.xwic.cube.IDataPoolManager;
import de.xwic.cube.IDimension;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.IMeasure;
import de.xwic.cube.Key;
import de.xwic.cube.formatter.PercentageValueFormatProvider;
import de.xwic.cube.functions.DifferenceFunction;
import de.xwic.cube.storage.impl.FileDataPoolStorageProvider;
import de.xwic.cube.webui.controls.DimensionElementSelector;
import de.xwic.cube.webui.controls.filter.FilterGroup;
import de.xwic.cube.webui.controls.filter.FilterGroupProfile;
import de.xwic.cube.webui.viewer.CubeFilter;
import de.xwic.cube.webui.viewer.CubeViewer;
import de.xwic.cube.webui.viewer.CubeViewer.ColumnExpand;
import de.xwic.cube.webui.viewer.CubeViewer.RowExpand;
import de.xwic.cube.webui.viewer.CubeViewerModel;
import de.xwic.cube.webui.viewer.CubeViewerModelAdapter;
import de.xwic.cube.webui.viewer.CubeViewerModelEvent;
import de.xwic.cube.webui.viewer.CubeWriter;
import de.xwic.cube.webui.viewer.DimensionNavigationProvider;
import de.xwic.cube.webui.viewer.EmptyLineNavigationProvider;
import de.xwic.cube.webui.viewer.SectionLineNavigationProvider;
import de.xwic.cube.webui.viewer.TotalNavigationProvider;

/**
 * Sample Application.
 * @author lippisch
 */
public class DemoApplication1 extends Application {

	
	private CubeViewerModel model;
	private CheckBox expandLeft;
	private CheckBox expandDown;
	private Button expandAll;
	private Button collapseAll;

	@Override
	public Control createRootControl(IControlContainer container) {
		
		Page page = new Page(container, "root");
		ToolBar toolBar = new ToolBar(page,"toolbar");
		
		page.setTitle("xwic cube demo");
		page.setTemplateName(getClass().getName());
		ICube cube = createCube();
		
		
		
		final CubeViewer viewer = new CubeViewer(page, "viewer");
		this.expandLeft = new CheckBox(page, "expandLeft");
		this.expandLeft.setChecked(viewer.getColumnExpand()==ColumnExpand.LEFT);
		viewer.setFrozenColumnFixWidth(200);
		expandLeft.addValueChangedListener(new ValueChangedListener() {			
			@Override
			public void valueChanged(ValueChangedEvent event) {
				if(expandLeft.isChecked()){
					viewer.setColumnExpand(ColumnExpand.LEFT);
				}else{
					viewer.setColumnExpand(ColumnExpand.RIGHT);
				}
			}
		});
		this.expandDown = new CheckBox(page, "expandRight");
		
		this.expandDown.setChecked(viewer.getRowExpand() == RowExpand.DOWN);
		this.expandDown.addValueChangedListener(new ValueChangedListener() {
			@Override
			public void valueChanged(ValueChangedEvent event) {
				boolean checked = expandDown.isChecked();
				if(checked){
					viewer.setRowExpand(RowExpand.DOWN);
				}else{
					viewer.setRowExpand(RowExpand.UP);
				}
			}
		});
		
		this.expandAll = new Button(page,"expandAll");
		this.expandAll.addSelectionListener(new SelectionListener() {
			@Override
			public void objectSelected(SelectionEvent arg0) {
				viewer.getModel().expandAll();
			}
		});
		this.collapseAll = new Button(page,"collapseAll");
		collapseAll.addSelectionListener(new SelectionListener() {
			
			@Override
			public void objectSelected(SelectionEvent arg0) {
				viewer.getModel().collapseAll();
			}
		});
		viewer.setColumnWidth(60);
		model = viewer.getModel();
		model.setCube(cube);
		model.setMeasure(cube.getDataPool().getMeasure("Actual"));
		model.addCubeViewerModelListener(new CubeViewerModelAdapter() {
			/* (non-Javadoc)
			 * @see de.xwic.cube.webui.viewer.CubeViewerModelAdapter#cellSelected(de.xwic.cube.webui.viewer.CubeViewerModelEvent)
			 */
			@Override
			public void cellSelected(CubeViewerModelEvent event) {
				onCellSelection(event.getSelectionKey(), event.getSelectionArguments());
			}
		});
		
		IDimension dimGEO = cube.getDataPool().getDimension("GEO");
		IDimension dimTime = cube.getDataPool().getDimension("Time");
		IDimension dimOT = cube.getDataPool().getDimension("OrderType");
		
		DimensionNavigationProvider otNavigationProvider = new DimensionNavigationProvider(model, dimOT);
		otNavigationProvider.setClickable(true);
		otNavigationProvider.setIndention(1);
//		otNavigationProvider.setHideEmptyElements(true);
//		otNavigationProvider.setShowRoot(true);
		
		model.addRowNavigationProvider(otNavigationProvider);
		
		DimensionNavigationProvider geoNavigationProvider = new DimensionNavigationProvider(model, dimGEO);
		geoNavigationProvider.setHideEmptyElements(true);
		
		geoNavigationProvider.setShowRoot(true);
		geoNavigationProvider.setClickable(true);
		geoNavigationProvider.setIndention(1);
		model.addRowNavigationProvider(geoNavigationProvider);

		SectionLineNavigationProvider slnp = new SectionLineNavigationProvider();
		slnp.setTitle("Totals will be here!");
		model.addRowNavigationProvider(slnp);

		TotalNavigationProvider tnp1 = new TotalNavigationProvider();
		tnp1.setIndention(1);
		tnp1.setTitle("Sum Total");
		model.addRowNavigationProvider(tnp1);
		
		model.addRowNavigationProvider(new EmptyLineNavigationProvider());

		TotalNavigationProvider tnp = new TotalNavigationProvider();
		tnp.setIndention(0);
		tnp.setTitle("Grand Total");
		model.addRowNavigationProvider(tnp);
		
		DimensionNavigationProvider timeNavigationProvider = new DimensionNavigationProvider(model, dimTime);
//		timeNavigationProvider.setHideEmptyElements(true);
		timeNavigationProvider.setShowRoot(true);
		timeNavigationProvider.setClickable(true);
//		navigationProvider.setIndention(1);
//		model.addRowNavigationProvider(navigationProvider);

		timeNavigationProvider.setCssCellClass("TimeClassCSS");
		
		model.addColumnNavigationProvider(timeNavigationProvider);
		
		// create filter
		final CubeFilter filter = new CubeFilter(page, "filter", model);
		
		filter.setSelectMeasure(true);
		DimensionElementSelector dimSelector = filter.addDimension(dimOT);
		dimSelector.setShowFilterField(true);
		otNavigationProvider.setFilter(filter);
		
		
		dimSelector = filter.addDimension(dimGEO);
		dimSelector.setDimensionElement(dimGEO.getDimensionElement("Americas"));
		geoNavigationProvider.setFilter(filter);
		
		dimSelector.setSelectLeafsOnly(true);
		dimSelector = filter.addDimension(dimTime);
		dimSelector.setMultiSelection(true);
		dimSelector.setShowFilterField(true);
		timeNavigationProvider.setFilter(filter);
		
		
		List<FilterGroupProfile> profiles;
		try {
			profiles = FilterGroupProfile.deserialize("[{\"name\":\"Test Profile 1\",\"profile\":\"{'root.filter':{'Time':['2009/Q1'],'GEO':['Americas'],'OrderType':['COO']}}\"},{\"name\":\"asd3\",\"profile\":\"{'root.filter':{'Time':['2009/Q3'],'GEO':['Americas'],'OrderType':['COO']}}\"},{\"name\":\"asd2\",\"profile\":\"{'root.filter':{'Time':['2009/Q2'],'GEO':['Americas'],'OrderType':['COO']}}\"}]");
		} catch (JSONException e1) {
			e1.printStackTrace();
			profiles = new ArrayList<FilterGroupProfile>();
		}
		
		final FilterGroup filterGroup = new FilterGroup();
		
		filterGroup.setFilterProfies(profiles);
		
		filterGroup.addFilter(filter);
		filterGroup.createSaveButton(toolBar.addRightGroup(), "Save", null, "Save Filter");
		filterGroup.createLoadButton(toolBar.addRightGroup(), "Load Profile", null);
		// create writer
		new CubeWriter(page, "writer", model);
		
		Button applyButton = toolBar.addGroup().addButton();
		applyButton.setTitle("Apply Filters");
		applyButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void objectSelected(SelectionEvent arg0) {
				filterGroup.applyAllFilters();
				try {
					System.out.println(FilterGroupProfile.serialize(filterGroup.getFilterProfiles()));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		return page;
	}

	/**
	 * @param selectionKey
	 * @param selectionArguments
	 */
	protected void onCellSelection(Key selectionKey, String[] selectionArguments) {
		
		System.out.println(selectionKey+" "+Arrays.asList(selectionArguments));
		
	}

	private ICube createCube() {

		IDataPoolManager dpm = DataPoolManagerFactory.createDataPoolManager(new FileDataPoolStorageProvider(new File(".")));
		IDataPool pool = dpm.createDataPool("demo");
		IDimension dimGEO = pool.createDimension("GEO");
		IDimensionElement de = dimGEO.createDimensionElement("EMEA");
		de.createDimensionElement("Germany");
		de.createDimensionElement("UK");
		de.createDimensionElement("France");
		dimGEO.createDimensionElement("Americas");
		dimGEO.createDimensionElement("APAC");
		
		IDimension dimTime = pool.createDimension("Time");
		IDimensionElement deY = dimTime.createDimensionElement("2009");
		de = deY.createDimensionElement("Q1");
		de.createDimensionElement("May");
		de.createDimensionElement("Jun");
		de.createDimensionElement("Jul");
		de = deY.createDimensionElement("Q2");
		de.createDimensionElement("Aug");
		de.createDimensionElement("Sep");
		de.createDimensionElement("Oct");
		de = deY.createDimensionElement("Q3");
		de.createDimensionElement("Nov");
		de.createDimensionElement("Dec");
		de.createDimensionElement("Jan");
		de = deY.createDimensionElement("Q4");
		de.createDimensionElement("Feb");
		de.createDimensionElement("Mar");
		de.createDimensionElement("Apr");
		
		deY = dimTime.createDimensionElement("2010");
		de = deY.createDimensionElement("Q1");
		de.createDimensionElement("May");
		de.createDimensionElement("Jun");
		de.createDimensionElement("Jul");
		de = deY.createDimensionElement("Q2");
		de.createDimensionElement("Aug");
		de.createDimensionElement("Sep");
		de.createDimensionElement("Oct");
		de = deY.createDimensionElement("Q3");
		de.createDimensionElement("Nov");
		de.createDimensionElement("Dec");
		de.createDimensionElement("Jan");
		de = deY.createDimensionElement("Q4");
		de.createDimensionElement("Feb");
		de.createDimensionElement("Mar");
		de.createDimensionElement("Apr");
	
		IDimension dimOT = pool.createDimension("OrderType");
		dimOT.createDimensionElement("AOO");
		dimOT.createDimensionElement("COO");
		
		IMeasure meActual = pool.createMeasure("Actual");
		IMeasure mePlan = pool.createMeasure("Plan");
		IMeasure mePA = pool.createMeasure("Plan-Actual");
		mePA.setFunction(new DifferenceFunction(mePlan, meActual, true));
		mePA.setValueFormatProvider(new PercentageValueFormatProvider());
		
		ICube cube = pool.createCube("demo",
				new IDimension[] { dimGEO, dimTime, dimOT },
				new IMeasure[] { meActual, mePlan, mePA });
		
		
		// write some data
		cube.setCellValue(cube.createKey("[*]"), meActual, 0);
		cube.setCellValue(cube.createKey("[*]"), mePlan, 0);
		cube.setCellValue(cube.createKey("[*][2009/Q1/May][AOO]"), meActual, 1);
		cube.setCellValue(cube.createKey("[*][2009/Q1/Jun][AOO]"), meActual, 10);
		cube.setCellValue(cube.createKey("[*][2009/Q1/Jul][AOO]"), meActual, 100);
//		cube.setCellValue(cube.createKey("[*]"), meActual, 1000);
		
		return cube;
	}

}
