/**
 * 
 */
package de.xwic.cube.webui.controls;

import de.jwic.base.ControlContainer;
import de.jwic.base.IControlContainer;
import de.jwic.controls.ListBox;
import de.jwic.data.ISelectElement;
import de.jwic.events.ElementSelectedListener;
import de.xwic.cube.IDimension;
import de.xwic.cube.IDimensionElement;

/**
 * Uses a classic combo box to select a dimension element.
 * @author Florian Lippisch
 */
public class ClassicDimensionSelectorControl extends ControlContainer {

	private final IDimension dimension;
	private ListBox lbDim;

	/**
	 * @param container
	 * @param name
	 */
	public ClassicDimensionSelectorControl(IControlContainer container, String name, IDimension dimension) {
		super(container, name);
		this.dimension = dimension;
		
		lbDim = new ListBox(this,"listBox");
		lbDim.setConfirmMsg("");
		lbDim.setCssClass("xcube-cb");
		lbDim.setChangeNotification(true);
		
		ISelectElement addElement = lbDim.addElement("- All -", dimension.getID());
		
		addEntries(lbDim, 0, dimension);
		
		lbDim.setSelectedKey(dimension.getID());

	}
	
	/**
	 * @param lbc
	 * @param i
	 * @param dimension
	 */
	private void addEntries(ListBox lbc, int depth, IDimensionElement parent) {
	
		StringBuilder indent = new StringBuilder();
		for (int i = 0; i < depth; i++) {
			indent.append(" - ");
		}
		/*if (depth > 0) {
			indent.append("- ");
		}*/
		for (IDimensionElement elm : parent.getDimensionElements()) {
			
			String title = elm.getTitle() != null && elm.getTitle().length() > 0 ? elm.getTitle() : elm.getKey();
			lbc.addElement(indent + title, elm.getID());
			if (!elm.isLeaf()) {
				addEntries(lbc, depth + 1, elm);
			}
			
		}
		
	}

	/**
	 * @param listener
	 * @see de.jwic.controls.ListControl#addElementSelectedListener(de.jwic.events.ElementSelectedListener)
	 */
	public void addElementSelectedListener(ElementSelectedListener listener) {
		lbDim.addElementSelectedListener(listener);
	}

	/**
	 * @param listener
	 * @see de.jwic.controls.ListControl#removeElementSelectedListener(de.jwic.events.ElementSelectedListener)
	 */
	public void removeElementSelectedListener(ElementSelectedListener listener) {
		lbDim.removeElementSelectedListener(listener);
	}

	/**
	 * @return the dimension
	 */
	public IDimension getDimension() {
		return dimension;
	}

	/**
	 * @return
	 */
	public String getSelectedId() {
		return lbDim.getSelectedKey();
	}
}
