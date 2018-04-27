/**
 * Kellerkinder Framework Alerts
 * 
 * Copyright 2018  <@Seil0>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.kellerkinder.Alerts;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class JFX2BtnCancelAlert {
	
	private String headingText;
	private String bodyText;
	private String btnStyle;
	private String btn1Text;
	private String btn2Text;
	private String cancelText;
	private EventHandler<ActionEvent> btn1Action;
	private EventHandler<ActionEvent> btn2Action;
	private Stage stage;
	private JFXAlert<Void> alert;

	/**
	 * Creates a new JFoenix Alert with 2 buttons and one cancel button
	 * @param titleText		Title text of the alert
	 * @param headerText	Heading text of the alert
	 * @param contentText	Content text of the alert
	 * @param btnStyle		Style of the okay button
	 * @param btn1Text		btn1 text
	 * @param btn2Text		btn2 text
	 * @param cancelText	cancel button text
	 * @param stage			stage to which the dialog belongs
	 */
	public JFX2BtnCancelAlert(String headingText, String bodyText, String btnStyle, String btn1Text, String btn2Text,
			String cancelText, Stage stage) {
		setHeadingText(headingText);
		setBodyText(bodyText);
		setBtnStyle(btnStyle);
		setBtn1Text(btn1Text);
		setBtn2Text(btn2Text);
		setCancelText(cancelText);
		setStage(stage);
	}

	public JFX2BtnCancelAlert() {
		// Auto-generated constructor stub
	}

	public void showAndWait() {
		alert = new JFXAlert<>(stage);

		JFXButton btnOne = new JFXButton();

		btnOne.setText(btn1Text);
		btnOne.setOnAction(btn1Action);
		btnOne.setButtonType(com.jfoenix.controls.JFXButton.ButtonType.RAISED);
		btnOne.setPrefHeight(32);
		btnOne.setStyle(btnStyle);

		JFXButton btnTwo = new JFXButton();
		btnTwo.setText(btn2Text);
		btnTwo.setOnAction(btn2Action);
		btnTwo.setButtonType(com.jfoenix.controls.JFXButton.ButtonType.RAISED);
		btnTwo.setPrefHeight(32);
		btnTwo.setStyle(btnStyle);

		JFXButton cancelBtn = new JFXButton();
		cancelBtn.setText(cancelText);
		cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				alert.close();
				System.exit(1);
			}
		});
		cancelBtn.setButtonType(com.jfoenix.controls.JFXButton.ButtonType.RAISED);
		cancelBtn.setPrefHeight(32);
		cancelBtn.setStyle(btnStyle);

		JFXDialogLayout content = new JFXDialogLayout();
		content.setActions(btnOne, btnTwo, cancelBtn);
		content.setHeading(new Text(headingText));
		content.setBody(new Text(bodyText));
		alert.setContent(content);
		alert.showAndWait();
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

	public String getBtnStyle() {
		return btnStyle;
	}

	public void setBtnStyle(String btnStyle) {
		this.btnStyle = btnStyle;
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

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public JFXAlert<Void> getAlert() {
		return alert;
	}

	public void setAlert(JFXAlert<Void> alert) {
		this.alert = alert;
	}
}
