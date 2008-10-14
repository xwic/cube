/**
 * 
 */
package de.xwic.cube.webui.viewer;

import java.io.PrintWriter;

import de.jwic.base.Control;
import de.jwic.base.IControlContainer;
import de.jwic.base.RenderContext;
import de.jwic.renderer.self.ISelfRenderingControl;
import de.jwic.renderer.self.SelfRenderer;
import de.xwic.cube.IDimension;
import de.xwic.cube.IDimensionElement;
import de.xwic.cube.Key;
import de.xwic.cube.webui.util.TableRenderer;

/**
 * @author Florian Lippisch
 */
public class CubeViewer extends Control implements ISelfRenderingControl {

	private static final long serialVersionUID = 2L;
	private CubeViewerModel model;
	
	/**
	 * @param container
	 * @param name
	 */
	public CubeViewer(IControlContainer container, String name) {
		super(container, name);

		setRendererId(SelfRenderer.RENDERER_ID);
		
		model = new CubeViewerModel(getSessionContext().getLocale());
		
	}

	/**
	 * @return the model
	 */
	public CubeViewerModel getModel() {
		return model;
	}

	/* (non-Javadoc)
	 * @see de.jwic.renderer.self.ISelfRenderingControl#render(de.jwic.base.RenderContext)
	 */
	public void render(RenderContext renderContext) {
		
		if (model.getCube() == null) {
			// render empty block
			renderWarning(renderContext, "No Cube Defined");
			return;
		}
		if (model.getMeasure() == null) {
			// render empty block
			renderWarning(renderContext, "No Measure Defined");
			return;
		}

		PrintWriter out = renderContext.getWriter();

		TableRenderer tbl = new TableRenderer();

		// render header...
		/*
		if (model.getVerticals().size() > 0 || model.getHorizontals().size() > 0) {
			tbl.append("");
		}
		for (IDimension dimCol : model.getHorizontals()) {
			for (IDimensionElement elmCol : dimCol.getDimensionElements()) {
				tbl.append(elmCol.getKey());
			}
		}
		tbl.append("Total");
		tbl.newRow();
		
		// render rows.
		Key cursor = model.createCursor();
		for (IDimension dimVert : model.getVerticals()) {
			int idx = model.getDimensionIndex(dimVert);
			for (IDimensionElement elm : dimVert.getDimensionElements()) {
				tbl.append(elm.getKey());
				cursor.setDimensionElement(idx, elm);
				
				// iterate over cols
				for (IDimension dimCol : model.getHorizontals()) {
					int idxCol = model.getDimensionIndex(dimCol);
					for (IDimensionElement elmCol : dimCol.getDimensionElements()) {
						cursor.setDimensionElement(idxCol, elmCol);
						tbl.append(formatValue(model.getCube().getCellValue(cursor, model.getMeasure())));
					}
					cursor.setDimensionElement(idxCol, dimCol);
				}
				tbl.append(formatValue(model.getCube().getCellValue(cursor, model.getMeasure())));
				
				tbl.newRow();
				
			}
			cursor.setDimensionElement(idx, dimVert);
		}
		tbl.append("Total");
		
		// iterate over cols
		for (IDimension dimCol : model.getHorizontals()) {
			int idxCol = model.getDimensionIndex(dimCol);
			for (IDimensionElement elmCol : dimCol.getDimensionElements()) {
				cursor.setDimensionElement(idxCol, elmCol);
				tbl.append(formatValue(model.getCube().getCellValue(cursor, model.getMeasure())));
			}
			cursor.setDimensionElement(idxCol, dimCol);
		}
		tbl.append(formatValue(model.getCube().getCellValue(cursor, model.getMeasure())));
		
		
		tbl.render(out);
		*/
		out.println("<span class=\"info\">cube: " + model.getCube().getKey() + ", measure: " + model.getMeasure().getKey() + "</span>");
		
		
	}

	/**
	 * @param value
	 * @return
	 */
	private String formatValue(Double value) {
		if (value != null) {
			return model.getNumberFormat().format(value);
		}
		return "<i>na</i>";
	}

	/**
	 * @param renderContext
	 * @param string
	 */
	private void renderWarning(RenderContext renderContext, String msg) {
		renderContext.getWriter().println(msg);
	}

}
