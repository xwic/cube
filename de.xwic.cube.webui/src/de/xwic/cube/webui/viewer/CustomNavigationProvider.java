/**
 * 
 */
package de.xwic.cube.webui.viewer;

import java.util.ArrayList;
import java.util.List;

import de.xwic.cube.webui.viewer.INavigationElement;
import de.xwic.cube.webui.viewer.INavigationProvider;
import de.xwic.cube.webui.viewer.NavigationSize;

/**
 * Contains custom navigation elements.
 * @author lippisch
 */
public class CustomNavigationProvider implements INavigationProvider {

	private List<INavigationElement> elements = new ArrayList<INavigationElement>();
	
	/**
	 * @param elements
	 */
	public CustomNavigationProvider(List<INavigationElement> elements) {
		super();
		this.elements = elements;
	}

	/**
	 * Construct a new CustomNavigationProvider.
	 * @param element
	 */
	public CustomNavigationProvider(INavigationElement element) {
		elements.add(element);
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.webui.viewer.INavigationProvider#getNavigationSize()
	 */
	public NavigationSize getNavigationSize() {
		return new NavigationSize(1, elements.size());
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.webui.viewer.INavigationElementProvider#getNavigationElements()
	 */
	public List<INavigationElement> getNavigationElements() {
		return elements;
	}

}
