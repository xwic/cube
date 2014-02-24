package de.xwic.cube.webui.samples.demo1;

import de.jwic.base.Application;
import de.jwic.base.Control;
import de.jwic.base.IControlContainer;
import de.jwic.base.Page;
import de.jwic.controls.Button;
import de.jwic.controls.ToolBar;
import de.jwic.events.SelectionEvent;
import de.jwic.events.SelectionListener;
import de.xwic.cube.*;
import de.xwic.cube.formatter.PercentageValueFormatProvider;
import de.xwic.cube.functions.DifferenceFunction;
import de.xwic.cube.storage.impl.FileDataPoolStorageProvider;
import de.xwic.cube.webui.controls.DimensionElementSelector;
import de.xwic.cube.webui.controls.filter.FilterGroup;
import de.xwic.cube.webui.controls.filter.FilterGroupProfile;
import de.xwic.cube.webui.viewer.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Sample Application.
 * @author lippisch
 */
public class DemoApplication1 extends Application {

	
	private CubeViewerModel model;

	@Override
	public Control createRootControl(IControlContainer container) {
		
		Page page = new Page(container, "root");
		ToolBar toolBar = new ToolBar(page,"toolbar");
		
		page.setTitle("xwic cube demo");
		page.setTemplateName(getClass().getName());
		ICube cube = createCube();
		
		
		CubeViewer viewer = new CubeViewer(page, "viewer");
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
		
		DimensionNavigationProvider navigationProvider = new DimensionNavigationProvider(model, dimOT);
		navigationProvider.setClickable(true);
		model.addColumnNavigationProvider(navigationProvider);
		
		navigationProvider = new DimensionNavigationProvider(model, dimGEO);
		navigationProvider.setHideEmptyElements(true);
		navigationProvider.setShowRoot(true);
		navigationProvider.setClickable(true);
		
		model.addRowNavigationProvider(navigationProvider);
		
		navigationProvider = new DimensionNavigationProvider(model, dimTime);
		navigationProvider.setHideEmptyElements(false);
		navigationProvider.setShowRoot(true);
		navigationProvider.setClickable(true);
//		navigationProvider.setIndention(1);
		model.addRowNavigationProvider(navigationProvider);

		model.addColumnNavigationProvider(navigationProvider);
		
		// create filter
		final CubeFilter filter = new CubeFilter(page, "filter", model);
		filter.setSelectMeasure(true);
		filter.addDimension(dimOT);
		DimensionElementSelector dimSelector = filter.addDimension(dimGEO);
		dimSelector.setDimensionElement(dimGEO.getDimensionElement("Americas"));
		dimSelector.setSelectLeafsOnly(true);
		filter.addDimension(dimTime).setMultiSelection(true);
		
		FilterGroupProfile profile = new FilterGroupProfile("Test Profile 1", "{'root.filter':{'Time':['2009/Q1'],'GEO':['Americas'],'OrderType':['COO']}}");
		List<FilterGroupProfile> profiles = new ArrayList<FilterGroupProfile>();
		profiles.add(profile);
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
				System.out.println(filterGroup.save());
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
