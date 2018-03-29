package org.kellerkinder.Alerts;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

public class JFXInfoAlert {
	
	private String titleText;
	private String headerText;
	private String contentText;
	private String btnStyle;
	private Stage stage;
	
	public JFXInfoAlert() {
		// Auto-generated constructor stub
	}
	
	public JFXInfoAlert(String titleText, String headerText, String contentText, String btnStyle, Stage stage) {
		setTitleText(titleText);
		setHeaderText(headerText);
		setContentText(contentText);
		setBtnStyle(btnStyle);
		setStage(stage);
	}
	
	public void showAndWait( ) {
		JFXAlert<Void> alert = new JFXAlert<>(stage);
		alert.setTitle(titleText);
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		
		JFXButton button = new JFXButton("Okay");
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				alert.close();
			}
		});
		button.setButtonType(com.jfoenix.controls.JFXButton.ButtonType.RAISED);
		button.setPrefHeight(32);
		button.setStyle(btnStyle);
		
		JFXDialogLayout content = new JFXDialogLayout();
		content.setActions(button);
		alert.setContent(content);
		alert.showAndWait();
	}

	public String getTitleText() {
		return titleText;
	}

	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}

	public String getHeaderText() {
		return headerText;
	}

	public void setHeaderText(String headerText) {
		this.headerText = headerText;
	}

	public String getContentText() {
		return contentText;
	}

	public void setContentText(String contentText) {
		this.contentText = contentText;
	}

	public String getBtnStyle() {
		return btnStyle;
	}

	public void setBtnStyle(String btnStyle) {
		this.btnStyle = btnStyle;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
}
