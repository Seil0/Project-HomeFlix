/**
 * Project HomeFlix
 * 
 * Copyright 2016-2017  <admin@kellerkinder>
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
 * 
 */
package application;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Main extends Application {
	
	Stage primaryStage;
	private String path;
	private String streamingPathWin = System.getProperty("user.home") + "\\Documents\\HomeFlix";
	private String streamingPathLinux = System.getProperty("user.home") + "/HomeFlix";
	private String color = "ee3523";
	private String autoUpdate = "0";
	private String mode = "local";	//local or streaming
	private String local = System.getProperty("user.language")+"_"+System.getProperty("user.country");
	private double size = 17;
	private ResourceBundle bundle;
	private MainWindowController mainWindowController;
	private File dirWin = new File(System.getProperty("user.home") + "/Documents/HomeFlix");	//Windows: C:/Users/"User"/Documents/HomeFlix
	private File dirLinux = new File(System.getProperty("user.home") + "/HomeFlix");	//Linux: /home/"User"/HomeFlix
	private File fileWin = new File(dirWin + "/config.xml");	//Windows: C:/Users/"User"/Documents/HomeFlix/config.xml
	private File fileLinux = new File(dirLinux + "/config.xml");	//Linux: /home/"User"/HomeFlix/config.xml
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		mainWindow();
	}
	
	private void mainWindow(){
	
		try {
		FXMLLoader loader = new FXMLLoader(Main.class.getResource("MainWindow.fxml"));
		AnchorPane pane = loader.load();
		primaryStage.setMinHeight(600.00);
		primaryStage.setMinWidth(900.00);
		primaryStage.setResizable(false);
		primaryStage.setTitle("Project HomeFlix");
		primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/recources/Homeflix_Icon_64x64.png"))); //adds application icon

		mainWindowController = loader.getController();	//Link of FXMLController and controller class
		mainWindowController.setAutoUpdate(autoUpdate);	//set auto-update
		mainWindowController.setMain(this);	//call setMain
		
		//Linux					if directory exists -> check config.xml
		if(System.getProperty("os.name").equals("Linux")){
			if(dirLinux.exists() != true){
				dirLinux.mkdir();
			}else if(fileLinux.exists() != true){
				mainWindowController.setPath(firstStart());
				mainWindowController.setStreamingPath(streamingPathLinux);
				mainWindowController.setColor(color);
				mainWindowController.setSize(size);
				mainWindowController.setAutoUpdate(autoUpdate);
				mainWindowController.setLocal(local);
				mainWindowController.setMode(mode);
				mainWindowController.saveSettings();
				Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar");	//start again (preventing Bugs)
				System.exit(0);	//finishes itself
			}
		//windows
		}else{
			if(dirWin.exists() != true){
				dirWin.mkdir();
			}else if(fileWin.exists() != true){
				mainWindowController.setPath(firstStart());
				mainWindowController.setStreamingPath(streamingPathWin);
				mainWindowController.setColor(color);
				mainWindowController.setSize(size);
				mainWindowController.setAutoUpdate(autoUpdate);
				mainWindowController.setLocal(local);
				mainWindowController.setMode(mode);
				mainWindowController.saveSettings();
				Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar");	//start again (preventing Bugs)
				System.exit(0);	//finishes itself
			}	
		}
		
		mainWindowController.loadSettings();
		mainWindowController.loadStreamingSettings();
		mainWindowController.initUI();
		mainWindowController.initActions();
		mainWindowController.initTabel();
		mainWindowController.setLocalUI();
		mainWindowController.applyColor();	//set theme color
		
		mainWindowController.dbController.main(); //initialize database controller
		mainWindowController.dbController.createDatabase(); //creating the database
		mainWindowController.dbController.loadData(); 	//loading data from database to mainWindowController 
		mainWindowController.addDataUI();
		
		Scene scene = new Scene(pane);	//create new scene, append pane to scene
		
		primaryStage.setScene(scene);	//append scene to stage
		primaryStage.show();	//show stage
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Method for first Start
	private String firstStart(){
		MainWindowController.firststart = true;
		switch(System.getProperty("user.language")+"_"+System.getProperty("user.country")){
		case "en_US":	bundle = ResourceBundle.getBundle("recources.HomeFlix-Local", Locale.US);	//us_english
				break;
     	case "de_DE":	bundle = ResourceBundle.getBundle("recources.HomeFlix-Local", Locale.GERMAN);	//German
     			break;
     	default:		bundle = ResourceBundle.getBundle("recources.HomeFlix-Local", Locale.US);	//default local
     			break;
		 }
		
		Alert alert = new Alert(AlertType.CONFIRMATION);	//new alert with file-chooser
		alert.setTitle("Project HomeFlix");
		alert.setHeaderText(bundle.getString("firstStartHeader"));
		alert.setContentText(bundle.getString("firstStartContent"));

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = 
                directoryChooser.showDialog(primaryStage);
                path = selectedDirectory.getAbsolutePath();
            
		} else {
		    path = "";
		}
		return path;
	}

	public static void main(String[] args) {
		launch(args);
	}
}