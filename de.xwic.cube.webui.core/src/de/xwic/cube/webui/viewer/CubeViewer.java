/**
 * 
 */
package de.xwic.cube.webui.viewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import de.jwic.base.Control;
import de.jwic.base.IControlContainer;
import de.jwic.base.IncludeJsOption;
import de.jwic.base.JavaScriptSupport;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.webui.util.Table;
import de.xwic.cube.webui.util.TableCell;

/**
 * @author Florian Lippisch
 */
/**
 * @author msoft_000
 *
 */
@JavaScriptSupport
public class CubeViewer extends Control {

	
	public enum Align { BEGIN, END };
	public enum ColumnExpand{LEFT, RIGHT};
	public enum RowExpand{UP, DOWN};
	
	private static final long serialVersionUID = 2L;
	private final CubeViewerModel model;
	
	private Align columnTotalAlign = Align.BEGIN;
	private Align rowTotalAlign = Align.END;
	private int leftNavMinWidth = 250;
	private int columnWidth = 0; // default
	private boolean emptyCellsClickable = false;
	private ColumnExpand columnExpand = ColumnExpand.RIGHT;
	private RowExpand rowExpand = RowExpand.DOWN;
	private boolean fixedHeaders = false;
	
	private String cssTableStyle = "width:100%";
	
	private String cssTableClass = "";
	
	private int frozenColumnFixWidth = 0;
	
	/**
	 * @param container
	 * @param name
	 */
	public CubeViewer(IControlContainer container, String name) {
		super(container, name);
		
		model = new CubeViewerModel(getSessionContext().getLocale());
		model.addCubeViewerModelListener(new CubeViewerModelAdapter() {
			@Override
			public void filterUpdated(CubeViewerModelEvent event) {
				onFilterUpdated(event);				
			}
			@Override
			public void cubeUpdated(CubeViewerModelEvent event) {
				onCubeUpdated(event);
			}
			@Override
			public void nodeCollapse(CubeViewerModelEvent event) {
				requireRedraw();
			}
			@Override
			public void nodeExpand(CubeViewerModelEvent event) {
				requireRedraw();
			}
		});
	}

	/**
	 * @param event
	 */
	protected void onCubeUpdated(CubeViewerModelEvent event) {
		requireRedraw();
	}
	
	/**
	 * @param event
	 */
	protected void onFilterUpdated(CubeViewerModelEvent event) {
		requireRedraw();
	}

	/**
	 * @return the model
	 */
	public CubeViewerModel getModel() {
		return model;
	}

	/**
	 * Handle click action.
	 * @param parameter
	 */
	public void actionClick(String parameter) {
		
		StringTokenizer stk = new StringTokenizer(parameter, ";");
		int len = stk.countTokens();
		String dimKey;
		if (len > 0) {
			dimKey = stk.nextToken();
			String[] args = new String[len - 1];
			int i = 0;
			while (stk.hasMoreTokens()) {
				args[i++] = stk.nextToken();
			}
			model.notifyCellSelection(dimKey, args);
		} else {
			log.warn("Invalid parameter for cell selection - parameter must not be empty.");
		}
	}
	
	/**
	 * @return
	 */
	public Table renderTable() {
		Table tbl = new Table();
		// render header...
		
		if (model.getColumnProvider().size() == 0 && model.getRowProvider().size() == 0) {
			tbl.initSize(2, 1);
			tbl.getCell(0, 0).setContent("Total");
			tbl.getCell(1, 0).setContent(formatValue(model.getCube().getCellValue(model.getTotalKey(), model.getMeasure())));
			// early EXIT
			return tbl;
		} 

		// evaluate horizontal header size (columns)
		int colHeight = 0;
		int colCount = 0;
		for (INavigationProvider navProvider : model.getColumnProvider()) {
			NavigationSize size = navProvider.getNavigationSize();
			int depth = size.depth+ navProvider.getIndention();
			if (depth > colHeight) {
				colHeight = depth;
			}
			colCount += size.cells;
		}
		
		// evaluate vertical header size (rows)
		int rowDepth = 1;
		int rowCount = 0;
		for (INavigationProvider navProvider : model.getRowProvider()) {
			NavigationSize size = navProvider.getNavigationSize();
			rowCount += size.cells;
		}
		tbl.initHead(colHeight, rowDepth+colCount);
		tbl.initSize(rowCount, rowDepth + colCount);
		
		if (columnWidth > 0) {
			for (int col = rowDepth; col < (rowDepth + colCount); col ++) {
				tbl.setColumnWidth(col, columnWidth);
			}
		}
		
		// render Header
		int startCol = rowDepth;

		List<INavigationProvider> columnProvider = model.getColumnProvider();
		if(columnExpand == ColumnExpand.RIGHT){
			columnProvider = new ArrayList<INavigationProvider>(model.getColumnProvider());
			Collections.reverse(columnProvider);
		}
		for (INavigationProvider navProvider : columnProvider) {
			NavigationSize size = navProvider.getNavigationSize();
			size.cells += startCol;
			renderNavigation(tbl, 0, startCol, navProvider, size, true,"",navProvider.getIndention(), navProvider.getCssCellClass());
			startCol += (size.cells - startCol);
		}		
		
		// render rows
		int startRow = colHeight;
		List<INavigationProvider> rowProvider = model.getRowProvider();
		if(rowExpand == RowExpand.DOWN){
			rowProvider = new ArrayList<INavigationProvider>(rowProvider);
			Collections.reverse(rowProvider);
		}
		for (INavigationProvider navProvider : rowProvider) {
			NavigationSize size = navProvider.getNavigationSize();
			size.depth = rowDepth;
			if (size.cells > 0) {
				renderNavigation(tbl, startRow, 0, navProvider, size, false,"",navProvider.getIndention(), navProvider.getCssCellClass());
			}
			startRow += size.cells;
		}		
		
		// render content
		for (int row = 0; row < rowCount; row++) {
			int rowIdx = row + colHeight;
			ContentInfo ciRow = (ContentInfo) tbl.getRowData(rowIdx);
			
			for (int col = 0; col < colCount; col++) {
				int colIdx = col + rowDepth;
				ContentInfo ciCol = (ContentInfo) tbl.getColumnData(colIdx);
				TableCell cell = tbl.getCell(rowIdx, colIdx);
				cell.setCssCellClass(ciCol.getCssCellClass());
				boolean empty = true;
				if (ciCol != null && ciRow != null) {
					ICubeDataProvider dataProvider = ciRow.getCubeDataProvider().getPriority() > ciCol.getCubeDataProvider().getPriority() ?
							ciRow.getCubeDataProvider() :
							ciCol.getCubeDataProvider();
					
					String content = dataProvider.getCellData(model, ciRow, ciCol);
					empty = content == null || content.length() == 0;
					cell.setContent(content);
					if (ciRow.isClickable() && ciCol.isClickable() && (!empty || emptyCellsClickable)) {
						cell.setAction("click");
						cell.setActionParam(buildActionParameter(ciRow, ciCol));
					}
					if(cell.getLevel() == 0)
						cell.setLevel(ciCol.getLevel());
					
				} else {
					cell.setContent("");
				}
				
				
			}
		}
		return tbl;
	}

	/**
	 * @param ciRow
	 * @param ciCol
	 * @return
	 */
	private String buildActionParameter(ContentInfo ciRow, ContentInfo ciCol) {

		// build dimension data
		StringBuilder sb = new StringBuilder();
		for (IDimensionElement de : ciRow.getElements()) {
			sb.append(de.getID());
		}
		for (IDimensionElement de : ciCol.getElements()) {
			sb.append(de.getID());
		}

		if (ciRow.getExtraClickInfo() != null) {
			sb.append(";")
			  .append(ciRow.getExtraClickInfo());
		}
		if (ciCol.getExtraClickInfo() != null) {
			sb.append(";")
			  .append(ciCol.getExtraClickInfo());
		}
		
		return sb.toString();
	}

	/**
	 * @param tbl 
	 * @param i
	 * @param startCol
	 * @param parentElement
	 * @param size
	 * @param b
	 */
	private NavigationSize renderNavigation(Table tbl, int startRow, int startCol, INavigationElementProvider parentElement, NavigationSize totalSize, boolean horizontal,String group, int level, String cssCellClass) {
		
		NavigationSize size = new NavigationSize();
		int row = startRow;
		int col = startCol;
		
		int totalItems = 0;
		
		List<INavigationElement> navigationElements = parentElement.getNavigationElements();
		if(horizontal){
			if(columnExpand == ColumnExpand.RIGHT ){
				navigationElements = new ArrayList<INavigationElement>(parentElement.getNavigationElements());
				Collections.reverse(navigationElements);
			}
		}else{
			if(rowExpand == RowExpand.DOWN){
				navigationElements = new ArrayList<INavigationElement>(parentElement.getNavigationElements());
				Collections.reverse(navigationElements);
			}
		}
		for (INavigationElement elm : navigationElements) {
			
			boolean expanded = model.isExpanded(elm.getElementId());
			int titleRow =  row;
			int titleCol = col;
			
			int items = 1;
			ContentInfo contentInfo = elm.getContentInfo();
			contentInfo.setLevel(level);
			contentInfo.setCssCellClass(cssCellClass);
			if (expanded) {
				Align align = horizontal ? columnTotalAlign : rowTotalAlign;
				int startIndent = !elm.hideTotal() && (align == Align.BEGIN) ? 1 : 0;
				NavigationSize subSize;
				if (horizontal) {
					subSize = renderNavigation(tbl, row + 1, col + startIndent, elm, totalSize, horizontal, group+" "+elm.getTitle(),level+1, cssCellClass);
				} else {
					subSize = renderNavigation(tbl, row + startIndent, col, elm, totalSize, horizontal, group+" "+elm.getTitle(),level+1, cssCellClass);
				}
				if (size.depth < subSize.depth) {
					size.depth = subSize.depth;
				}
				items = subSize.cells;
				
				if (!elm.hideTotal()) {
					items++;
					// render "sub-total" text
					int stRow = row + (horizontal ? subSize.depth : (align == Align.BEGIN ? 0 : subSize.cells));
					int stCol = col + (horizontal ? (align == Align.BEGIN ? 0 : subSize.cells) : subSize.depth);
					if (horizontal) {
						tbl.setColumnData(stCol, contentInfo);
						titleCol = stCol;
					} else {
						tbl.setRowData(stRow, contentInfo);
						titleRow = stRow;
					}
				}
			} else {
				if (horizontal) {
					tbl.setColumnData(col, contentInfo);
				} else {
					tbl.setRowData(row, contentInfo);
				}
			}
			// render navigation element cell
			totalItems += items;
			if (horizontal) {
				tbl.setMaxColLevel(Math.max(tbl.getMaxColLevel(), horizontal ? startRow : startCol));
				col += items;
			} else {
				tbl.setMaxRowLevel(Math.max(tbl.getMaxRowLevel(), level));
				row += items;
			}

			TableCell cell = tbl.getCell(titleRow, titleCol);
			cell.setContent(elm.getTitle());
			cell.setExpandable(elm.isExpandable());
			cell.setTitle(true);
			cell.setElementId(elm.getElementId());
			cell.setGroup(group);
			cell.setExpanded(expanded);
			cell.setCssCellClass(cssCellClass);
			
			if(!horizontal) {
				cell.getParent().setLevel(level);
				if (INavigationElementProvider.NavigationProviderTypes.TOTAL.equals(parentElement.getNavigationProviderType()) ||
						INavigationElementProvider.NavigationProviderTypes.EMPTY.equals(parentElement.getNavigationProviderType())	||
						INavigationElementProvider.NavigationProviderTypes.SECTION.equals(parentElement.getNavigationProviderType()) ||
						INavigationElementProvider.NavigationProviderTypes.TOTAL_TOP.equals(parentElement.getNavigationProviderType())) {
					cell.getParent().setLevel(((TotalNavigationProvider) parentElement).getIndention());
					cell.getParent().setTableRowType(parentElement.getNavigationProviderType().name());
				}
			} else{
				cell.setLevel(level);
				for(int i=0;i<tbl.getHeadRows().size() - row;i++){
					tbl.getCell(i+row, titleCol).setLevel(level);
				}
				if(level > 0 && expanded){
					for(int i=titleCol ;i < tbl.getColCount();i++){
						tbl.getCell(row, i).setLevel(level);
					}
					
				}
			}
			
		}
		
		size.cells = totalItems;
		size.depth++; // add "myself";
		return size;
	}

	/**
	 * Handles expand action.
	 * @param elementId
	 */
	public void actionExpand(String elementId) {
		model.expand(elementId);
		requireRedraw();
	}
	
	/**
	 * Handles Collapse action.
	 * @param elementId
	 */
	public void actionCollapse(String elementId) {
		model.collapse(elementId);
		requireRedraw();
	}
	
	public void actionToggleExpand(String elementId){
		if(model.isExpanded(elementId)){
			model.collapse(elementId);
		}else{
			model.expand(elementId);
		}
		this.requireRedraw();
	}
	
	/**
	 * @param value
	 * @return
	 */
	private String formatValue(Double value) {
		if (value != null) {
			return model.getValueFormat().format(value);
		}
		return "<i>na</i>";
	}

	/**
	 * @return the columnTotalAlign
	 */
	public Align getColumnTotalAlign() {
		return columnTotalAlign;
	}

	/**
	 * @param columnTotalAlign the columnTotalAlign to set
	 */
	public void setColumnTotalAlign(Align columnTotalAlign) {
		this.columnTotalAlign = columnTotalAlign;
	}

	/**
	 * @return the rowTotalAlign
	 */
	public Align getRowTotalAlign() {
		return rowTotalAlign;
	}

	/**
	 * @param rowTotalAlign the rowTotalAlign to set
	 */
	public void setRowTotalAlign(Align rowTotalAlign) {
		this.rowTotalAlign = rowTotalAlign;
	}

	/**
	 * @return the leftNavMinWidth
	 */
	public int getLeftNavMinWidth() {
		return leftNavMinWidth;
	}

	/**
	 * @param leftNavMinWidth the leftNavMinWidth to set
	 */
	public void setLeftNavMinWidth(int leftNavMinWidth) {
		this.leftNavMinWidth = leftNavMinWidth;
	}

	/**
	 * @return the columnWidth
	 */
	public int getColumnWidth() {
		return columnWidth;
	}

	/**
	 * @param columnWidth the columnWidth to set
	 */
	public void setColumnWidth(int columnWidth) {
		this.columnWidth = columnWidth;
	}

	/**
	 * @return the emptyCellsClickable
	 */
	public boolean isEmptyCellsClickable() {
		return emptyCellsClickable;
	}

	/**
	 * @param emptyCellsClickable the emptyCellsClickable to set
	 */
	public void setEmptyCellsClickable(boolean emptyCellsClickable) {
		this.emptyCellsClickable = emptyCellsClickable;
	}
	
	
	
	public Table getTable(){
		
		try{
			Table renderTable = renderTable();
			renderTable.setExpandDirections(this.rowExpand == RowExpand.DOWN, this.columnExpand == ColumnExpand.LEFT);
			return renderTable;
		}catch(Throwable t){
			log.error(t.getMessage(),t);
			return null;
		}
	}

	
	
	/**
	 * @param fixedColumn
	 */
	public void setFixedHeaders(boolean fixedColumn) {
		this.fixedHeaders = fixedColumn;
		this.requireRedraw();
	}
	/**
	 * @return
	 */
	@IncludeJsOption
	public boolean isFixedHeaders() {
		return fixedHeaders;
	}

	/**
	 * @return
	 */
	public ColumnExpand getColumnExpand() {
		return columnExpand;
	}

	/**
	 * @param columnExpand
	 */
	public void setColumnExpand(ColumnExpand columnExpand) {
		this.columnExpand = columnExpand;
		this.requireRedraw();
	}

	/**
	 * @return
	 */
	public RowExpand getRowExpand() {
		return rowExpand;
	}

	/**
	 * @param rowExpand
	 */
	public void setRowExpand(RowExpand rowExpand) {
		this.rowExpand = rowExpand;
		this.requireRedraw();
	}

	/**
	 * @return
	 */
	public String getCssTableStyle() {
		return cssTableStyle;
	}

	/**
	 * @param cssTableStyle
	 */
	public void setCssTableStyle(String cssTableStyle) {
		this.cssTableStyle = cssTableStyle;
		this.requireRedraw();
	}

	/**
	 * @return
	 */
	public String getCssTableClass() {
		return cssTableClass;
	}

	/**
	 * @param cssTableClass
	 */
	public void setCssTableClass(String cssTableClass) {
		this.cssTableClass = cssTableClass;
	}

	public int getFrozenColumnFixWidth() {
		return frozenColumnFixWidth;
	}

	public void setFrozenColumnFixWidth(int frozenColumnFixWidth) {
		this.frozenColumnFixWidth = frozenColumnFixWidth;
	}
	
	
}
