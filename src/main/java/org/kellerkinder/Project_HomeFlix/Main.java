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

package org.kellerkinder.Project_HomeFlix;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	String currentWorkingDirectory;
	private String COLOR = "ee3523";
	private String FONT_FAMILY = "System";
	private String mode = "local";	//local or streaming TODO
	private String local = System.getProperty("user.language")+"_"+System.getProperty("user.country");
	private boolean AUTO_UPDATE = false;
	private double FONT_SIZE = 17;
	private ResourceBundle bundle;
	private MainWindowController mainWindowController;
	private File directory;
	private File settingsFile;
	private File posterCache;
	private String dirWin = System.getProperty("user.home") + "/Documents/HomeFlix";	//Windows: C:/Users/"User"/Documents/HomeFlix
	private String dirLinux = System.getProperty("user.home") + "/HomeFlix";	//Linux: /home/"User"/HomeFlix
	private static Logger LOGGER;
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		currentWorkingDirectory = new java.io.File( "." ).getCanonicalPath();
		this.primaryStage = primaryStage;	
		mainWindow();
	}
	
	private void mainWindow(){

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ClassLoader.getSystemResource("fxml/MainWindow.fxml"));
			AnchorPane pane = (AnchorPane) loader.load();
			primaryStage.setMinHeight(600.00);
			primaryStage.setMinWidth(900.00);
			primaryStage.setResizable(false);
			primaryStage.setTitle("Project HomeFlix");
			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/icons/Homeflix_Icon_64x64.png"))); //adds application icon
	
			mainWindowController = loader.getController();	//Link of FXMLController and controller class
			mainWindowController.setAutoUpdate(AUTO_UPDATE);	//set auto-update
			mainWindowController.setCurrentWorkingDirectory(currentWorkingDirectory);
			mainWindowController.setMain(this);	//call setMain
			
			/**Linux else Windows, check if directory & config exist
			 * Windows: config file: 	C:/Users/"User"/Documents/HomeFlix/config.xml
			 * 			directory:		C:/Users/"User"/Documents/HomeFlix
			 * Linux: 	config file: 	/home/"User"/HomeFlix/config.xml
			 * 			directory: 		/home/"User"/HomeFlix
			 */
			if(System.getProperty("os.name").equals("Linux")) {
				directory = new File(dirLinux);
				settingsFile = new File(dirLinux + "/config.xml");
			} else {
				directory = new File(dirWin);
				settingsFile = new File(dirWin + "/config.xml");
			}
			
			posterCache = new File(directory+"/posterCache");
			
			if(!settingsFile.exists()){
				directory.mkdir();
				mainWindowController.setPath(firstStart());
				mainWindowController.setStreamingPath(directory.getAbsolutePath());
				mainWindowController.setColor(COLOR);
				mainWindowController.setSize(FONT_SIZE);
				mainWindowController.setAutoUpdate(AUTO_UPDATE);
				mainWindowController.setLocal(local);
				mainWindowController.setMode(mode);
				mainWindowController.saveSettings();
				try {
					Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar");	//start again (preventing Bugs)
					System.exit(0);	//finishes it self
				} catch (Exception e) {
					LOGGER.error("error while restarting HomeFlix", e);
				}
			}
			
			if(!posterCache.exists()) {
				posterCache.mkdir();
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
			scene.getStylesheets().add(getClass().getResource("/css/MainWindow.css").toExternalForm());
			primaryStage.setScene(scene);	//append scene to stage
			primaryStage.show();	//show stage
		} catch (IOException e) {
			LOGGER.error("", e);
		}
	}
	
	//Method for first Start
	private String firstStart(){
		MainWindowController.firststart = true;
		switch(System.getProperty("user.language")+"_"+System.getProperty("user.country")){
		case "en_US":	bundle = ResourceBundle.getBundle("locals.HomeFlix-Local", Locale.US);	//us_english
				break;
     	case "de_DE":	bundle = ResourceBundle.getBundle("locals.HomeFlix-Local", Locale.GERMAN);	//German
     			break;
     	default:		bundle = ResourceBundle.getBundle("locals.HomeFlix-Local", Locale.US);	//default local
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
		if(System.getProperty("os.name").equals("Linux")){
			System.setProperty("logFilename", System.getProperty("user.home") + "/HomeFlix/app.log");
			File logFile = new File(System.getProperty("user.home") + "/HomeFlix/app.log");
			logFile.delete();
		}else{
			System.setProperty("logFilename", System.getProperty("user.home") + "/Documents/HomeFlix/app.log");
			File logFile = new File(System.getProperty("user.home") + "/Documents/HomeFlix/app.log");
			logFile.delete();
		}
		LOGGER = LogManager.getLogger(Main.class.getName());
		launch(args);
	}

	public String getFONT_FAMILY() {
		return FONT_FAMILY;
	}

	public void setFONT_FAMILY(String FONT_FAMILY) {
		this.FONT_FAMILY = FONT_FAMILY;
	}

	public File getPosterCache() {
		return posterCache;
	}

	public void setPosterCache(File posterCache) {
		this.posterCache = posterCache;
	}
}