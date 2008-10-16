/**
 * 
 */
package de.xwic.cube.webui.viewer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Florian Lippisch
 */
public class TotalNavigationProvider implements INavigationProvider {

	private List<INavigationElement> elements;

	private class TotalNavigationElement implements INavigationElement {

		/* (non-Javadoc)
		 * @see de.xwic.cube.webui.viewer.INavigationElement#getContentInfo()
		 */
		public ContentInfo getContentInfo() {
			ContentInfo ci = new ContentInfo (new DefaultDimensionDataProvider());
			return ci;
		}

		/* (non-Javadoc)
		 * @see de.xwic.cube.webui.viewer.INavigationElement#getElementId()
		 */
		public String getElementId() {
			return "Total";
		}

		/* (non-Javadoc)
		 * @see de.xwic.cube.webui.viewer.INavigationElement#getSpan()
		 */
		public int getSpan() {
			return 1;
		}

		/* (non-Javadoc)
		 * @see de.xwic.cube.webui.viewer.INavigationElement#getTitle()
		 */
		public String getTitle() {
			return "Total";
		}

		/* (non-Javadoc)
		 * @see de.xwic.cube.webui.viewer.INavigationElement#hideTotal()
		 */
		public boolean hideTotal() {
			return false;
		}

		/* (non-Javadoc)
		 * @see de.xwic.cube.webui.viewer.INavigationElement#isExpandable()
		 */
		public boolean isExpandable() {
			return false;
		}

		/* (non-Javadoc)
		 * @see de.xwic.cube.webui.viewer.INavigationElementProvider#getNavigationElements()
		 */
		public List<INavigationElement> getNavigationElements() {
			return null;
		}
		
	}
	
	/**
	 * Constructor.
	 */
	public TotalNavigationProvider () {
		elements = new ArrayList<INavigationElement>();
		elements.add(new TotalNavigationElement());
	}
	
	/* (non-Javadoc)
	 * @see de.xwic.cube.webui.viewer.INavigationProvider#getNavigationSize()
	 */
	public NavigationSize getNavigationSize() {
		return new NavigationSize(1, 1);
	}

	/* (non-Javadoc)
	 * @see de.xwic.cube.webui.viewer.INavigationElementProvider#getNavigationElements()
	 */
	public List<INavigationElement> getNavigationElements() {
		return elements;
	}

}
