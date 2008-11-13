/**
 * 
 */
package de.xwic.cube.webui.viewer;

import java.util.ArrayList;
import java.util.List;

import de.xwic.cube.IMeasure;

/**
 * @author Florian Lippisch
 */
public class TotalNavigationProvider implements INavigationProvider {

	private List<INavigationElement> elements;
	private String title = "Total";
	private IMeasure fixedMeasure = null;
	public boolean clickable = false;
	private int indention = 0;

	private class TotalNavigationElement implements INavigationElement {

		/* (non-Javadoc)
		 * @see de.xwic.cube.webui.viewer.INavigationElement#getContentInfo()
		 */
		public ContentInfo getContentInfo() {
			DefaultDimensionDataProvider dataProvider = new DefaultDimensionDataProvider();
			dataProvider.setFixedMeasure(fixedMeasure);
			ContentInfo ci = new ContentInfo (dataProvider);
			ci.setClickable(clickable );
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
			return title;
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

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the fixedMeasure
	 */
	public IMeasure getFixedMeasure() {
		return fixedMeasure;
	}

	/**
	 * @param fixedMeasure the fixedMeasure to set
	 */
	public void setFixedMeasure(IMeasure fixedMeasure) {
		this.fixedMeasure = fixedMeasure;
	}

	/**
	 * @return the clickable
	 */
	public boolean isClickable() {
		return clickable;
	}

	/**
	 * @param clickable the clickable to set
	 */
	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}

	/**
	 * @return the indention
	 */
	public int getIndention() {
		return indention;
	}

	/**
	 * @param indention the indention to set
	 */
	public void setIndention(int indention) {
		this.indention = indention;
	}

}
