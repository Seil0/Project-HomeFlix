package com.cemu_UI.uiElements;

import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class JFXDirStrmCancelDialog {
	private String headingText;
	private String bodyText;
	private String dialogBtnStyle;
	private String btn1Text;
	private String btn2Text;
	private String cancelText;
	private int dialogWidth;
	private int dialogHeight;
	private EventHandler<ActionEvent> btn1Action;
	private EventHandler<ActionEvent> btn2Action;
	private Pane pane;

	/**
	 * Creates a new JFoenix Dialog to show some information with okay and cancel
	 * option
	 * 
	 * @param headingText 		Heading Text, just the heading
	 * @param bodyText			body Text, all other text belongs here
	 * @param dialogBtnStyle	Style of the okay button
	 * @param dialogWidth		dialog width
	 * @param dialogHeight		dialog height
	 * @param btn1Action		action which is performed if btn1 is clicked
	 * @param btn2Action		action which is performed if btn2 is clicked
	 * @param cancelAction		action which is performed if the cancel button is clicked
	 * @param pane				pane to which the dialog belongs
	 */
	public JFXDirStrmCancelDialog(String headingText, String bodyText, String dialogBtnStyle, int dialogWidth,
			int dialogHeight, EventHandler<ActionEvent> btn1Action, EventHandler<ActionEvent> btn2Action,
			Pane pane, ResourceBundle bundle) {
		setHeadingText(headingText);
		setBodyText(bodyText);
		setDialogBtnStyle(dialogBtnStyle);
		setDialogWidth(dialogWidth);
		setDialogHeight(dialogHeight);
		setBtn1Action(btn1Action);
		setBtn2Action(btn2Action);
		setPane(pane);

		btn1Text = bundle.getString("addDirectory");
		btn2Text = bundle.getString("addStreamSource");
		cancelText = bundle.getString("cancelBtnText");
	}

	public JFXDirStrmCancelDialog() {
		// Auto-generated constructor stub
	}

	public void show() {
		JFXDialogLayout content = new JFXDialogLayout();
		content.setHeading(new Text(headingText));
		content.setBody(new Text(bodyText));
		StackPane stackPane = new StackPane();
		stackPane.autosize();
		JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.LEFT, true);
		
		JFXButton btn1 = new JFXButton(btn1Text);
		btn1.addEventHandler(ActionEvent.ACTION, (e) -> {
			dialog.close();
		});
		btn1.addEventHandler(ActionEvent.ACTION, btn1Action);
		btn1.setButtonType(com.jfoenix.controls.JFXButton.ButtonType.RAISED);
		btn1.setPrefHeight(32);
		btn1.setStyle(dialogBtnStyle);
		
		JFXButton btn2 = new JFXButton(btn2Text);
		btn2.addEventHandler(ActionEvent.ACTION, (e) -> {
			dialog.close();
		});
		btn2.addEventHandler(ActionEvent.ACTION, btn2Action);
		btn2.setButtonType(com.jfoenix.controls.JFXButton.ButtonType.RAISED);
		btn2.setPrefHeight(32);
		btn2.setStyle(dialogBtnStyle);
		
		JFXButton cancelBtn = new JFXButton(cancelText);
		cancelBtn.addEventHandler(ActionEvent.ACTION, (e) -> {
			dialog.close();
		});
		cancelBtn.setButtonType(com.jfoenix.controls.JFXButton.ButtonType.RAISED);
		cancelBtn.setPrefHeight(32);
		cancelBtn.setStyle(dialogBtnStyle);
		
		content.setActions(cancelBtn, btn1, btn2);
		content.setPrefSize(dialogWidth, dialogHeight);
		pane.getChildren().add(stackPane);
		AnchorPane.setTopAnchor(stackPane, (pane.getHeight() - content.getPrefHeight()) / 2);
		AnchorPane.setLeftAnchor(stackPane, (pane.getWidth() - content.getPrefWidth()) / 2);
		dialog.show();
	}

	public String getHeadingText() {
		return headingText;
	}

	public void setHeadingText(String headingText) {
		this.headingText = headingText;
	}

	public String getBodyText() {
		return bodyText;
	}

	public void setBodyText(String bodyText) {
		this.bodyText = bodyText;
	}

	public String getDialogBtnStyle() {
		return dialogBtnStyle;
	}

	public void setDialogBtnStyle(String dialogBtnStyle) {
		this.dialogBtnStyle = dialogBtnStyle;
	}

	public String getBtn1Text() {
		return btn1Text;
	}

	public void setBtn1Text(String btn1Text) {
		this.btn1Text = btn1Text;
	}

	public String getBtn2Text() {
		return btn2Text;
	}

	public void setBtn2Text(String btn2Text) {
		this.btn2Text = btn2Text;
	}

	public String getCancelText() {
		return cancelText;
	}

	public void setCancelText(String cancelText) {
		this.cancelText = cancelText;
	}

	public int getDialogWidth() {
		return dialogWidth;
	}

	public void setDialogWidth(int dialogWidth) {
		this.dialogWidth = dialogWidth;
	}

	public int getDialogHeight() {
		return dialogHeight;
	}

	public void setDialogHeight(int dialogHeight) {
		this.dialogHeight = dialogHeight;
	}

	public EventHandler<ActionEvent> getBtn1Action() {
		return btn1Action;
	}

	public void setBtn1Action(EventHandler<ActionEvent> btn1Action) {
		this.btn1Action = btn1Action;
	}

	public EventHandler<ActionEvent> getBtn2Action() {
		return btn2Action;
	}

	public void setBtn2Action(EventHandler<ActionEvent> btn2Action) {
		this.btn2Action = btn2Action;
	}

	public Pane getPane() {
		return pane;
	}

	public void setPane(Pane pane) {
		this.pane = pane;
	}

}
