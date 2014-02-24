package de.xwic.cube.webui.controls.filter;

import de.jwic.base.ControlContainer;
import de.jwic.base.IControlContainer;
import de.jwic.controls.Button;
import de.jwic.controls.InputBox;
import de.jwic.controls.Label;

/**
 * @author bogdan
 *
 */
class PopUpContent extends ControlContainer {

	private final Button ok;
	private final Button cancel;
	private final Label infoText;
	private final InputBox filterName; 
	
	/**
	 * @param container
	 */
	public PopUpContent(IControlContainer container) {
		super(container,"content");
		this.setTemplateName(PopUpContent.class.getName());
		this.ok = new Button(this,"okButton");
		this.ok.setTitle("Ok");
		this.cancel  = new Button(this,"cancelButton");
		this.cancel.setTitle("Cancel");
		this.infoText = new Label(this,"infoText");
		this.filterName = new InputBox(this,"filterName");
		
	}
	
	/**
	 * @return
	 */
	public Button getOk() {
		return ok;
	}
	/**
	 * @return
	 */
	public Button getCancel() {
		return cancel;
	}
	/**
	 * @return
	 */
	public Label getInfoText() {
		return infoText;
	}
	
	/**
	 * @return
	 */
	public InputBox getFilterName() {
		return filterName;
	}
}
