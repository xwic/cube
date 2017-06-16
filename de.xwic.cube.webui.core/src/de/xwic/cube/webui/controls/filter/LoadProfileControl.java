/**
 * 
 */
package de.xwic.cube.webui.controls.filter;

import de.jwic.base.ControlContainer;
import de.jwic.base.IControlContainer;
import de.jwic.controls.Button;
import de.jwic.controls.ListBox;
import de.jwic.events.ElementSelectedEvent;
import de.jwic.events.ElementSelectedListener;

/**
 * @author bogdan
 * 
 */
class LoadProfileControl extends ControlContainer {

	private final Button loadButton;
	private final ListBox profileListBox;

	/**
	 * @param container
	 */
	public LoadProfileControl(IControlContainer container) {
		super(container, "loadContent");
		this.loadButton = new Button(this, "loadButton");
		this.profileListBox = new ListBox(this, "profileList");
		this.profileListBox.setConfirmMsg("");

	}

	/**
	 * @return
	 */
	public Button getLoadButton() {
		return loadButton;
	}

	/**
	 * @return
	 */
	public ListBox getProfileListBox() {
		return profileListBox;
	}
}
