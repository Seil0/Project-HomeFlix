/**
 * Project HomeFlix
 * 
 * Copyright 2016  <admin@kellerkinder>
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
/**
 * TODO OSX and	Linux directory and file (Linux: 99% not working!)
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Main extends Application {
	
	public Stage primaryStage;
	private String path;
	private InputStream inputStream;
	private String streamingPathWin = System.getProperty("user.home") + "\\Documents\\HomeFlix";
	private String streamingPathLinux = System.getProperty("user.home") + "/HomeFlix";
	private String color = "ee3523";
	private String autoUpdate = "0";
	private String mode = "local";	//local or streaming
	private double size = 12;
	private int local = 0;
	private File dirWin = new File(System.getProperty("user.home") + "/Documents/HomeFlix");	//Windows: C:/Users/"User"/Documents/HomeFlix	OSX: not tested yet	Linux: not tested yet(shalt not work!)
	private File dirLinux = new File(System.getProperty("user.home") + "/HomeFlix");
	private File fileWin = new File(dirWin + "/config.xml");	//Windows: C:/Users/"User"/Documents/HomeFlix/config.xml	OSX: not tested yet	Linux: not tested yet(shalt not work!)
	private File fileLinux = new File(dirLinux + "/config.xml");
	Properties props = new Properties();
	private MainWindowController mainWindowController;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		mainWindow();
	}
	
	public void mainWindow(){
	
		try {
		FXMLLoader loader = new FXMLLoader(Main.class.getResource("MainWindow.fxml"));
		AnchorPane pane = loader.load();
		primaryStage.setMinHeight(600.00);
		primaryStage.setMinWidth(900.00);
		primaryStage.setResizable(false);
		primaryStage.setTitle("Project HomeFlix");
		primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/recources/Homeflix_Icon_64x64.png"))); //adds application icon

		mainWindowController = loader.getController();	//Link of FXMLController and controller class
		mainWindowController.setAutoUpdate(autoUpdate);	//set autoupdate
		mainWindowController.setMain(this);	//call setMain
		
		//dir exists -> check config.xml
		if(System.getProperty("os.name").equals("Linux")){
			if(dirLinux.exists() == true){
				if (fileLinux.exists() != true) {
					mainWindowController.setPath(firstStart());
					if(System.getProperty("os.name").equals("Linux")){
						mainWindowController.setStreamingPath(streamingPathLinux);
					}else{
						mainWindowController.setStreamingPath(streamingPathWin);
					}
					mainWindowController.setColor(color);
					mainWindowController.setSize(size);
					mainWindowController.setAutoUpdate(autoUpdate);
					mainWindowController.setLoaclUI(local);
					mainWindowController.setMode(mode);
					mainWindowController.saveSettings();
					Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar");	//start again (preventing Bugs)
					System.exit(0);	//finishes itself
				}else{
					loadSettings();
				}	
			}else{
			dirLinux.mkdir();
			mainWindowController.setPath(firstStart());
			mainWindowController.setStreamingPath(streamingPathLinux);
			mainWindowController.setColor(color);
			mainWindowController.setSize(size);
			mainWindowController.setAutoUpdate(autoUpdate);
			mainWindowController.setLoaclUI(local);
			mainWindowController.setMode(mode);
			mainWindowController.saveSettings();
			Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar");	//start again (preventing Bugs)
			System.exit(0);	//finishes itself
			}
		}else{
			if(dirWin.exists() == true){
				if (fileWin.exists() != true) {
					mainWindowController.setPath(firstStart());
					mainWindowController.setStreamingPath(streamingPathWin);
					mainWindowController.setColor(color);
					mainWindowController.setSize(size);
					mainWindowController.setAutoUpdate(autoUpdate);
					mainWindowController.setLoaclUI(local);
					mainWindowController.setMode(mode);
					mainWindowController.saveSettings();
					Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar");	//start again (preventing Bugs)
					System.exit(0);	//finishes itself
				}else{
					loadSettings();
				}	
			}else{
			dirWin.mkdir();
			mainWindowController.setPath(firstStart());
			mainWindowController.setStreamingPath(streamingPathWin);
			mainWindowController.setColor(color);
			mainWindowController.setSize(size);
			mainWindowController.setAutoUpdate(autoUpdate);
			mainWindowController.setLoaclUI(local);
			mainWindowController.setMode(mode);
			mainWindowController.saveSettings();
			Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar");	//start again (preventing Bugs)
			System.exit(0);	//finishes itself
			}
		}
		
		mainWindowController.loadStreamingSettings();
		mainWindowController.applyColor();	//set theme color
		mainWindowController.cbLocal.getSelectionModel().select(mainWindowController.getLocal()); //set local
		mainWindowController.mainColor.setValue(Color.valueOf(mainWindowController.getColor()));
		
		mainWindowController.dbController.main(); //initialize database controller
		mainWindowController.dbController.createDatabase(); //creating the database
		mainWindowController.dbController.loadData(); 	//loading data from database to mainWindowController 
		
//		mainWindowController.loadData();	//l�d die Daten im Controller
		mainWindowController.addDataUI();
		
		Scene scene = new Scene(pane);	//create new scene, append pane to scene
		
		primaryStage.setScene(scene);	//append scene to stage
		primaryStage.show();	//show stage
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//methode f�r den erstmaligen Start
	private String firstStart(){
		Alert alert = new Alert(AlertType.CONFIRMATION);	//new alert with filechooser
		alert.setTitle("Project HomeFlix");
		alert.setHeaderText("Es ist kein Stammverzeichniss f�r Filme angegeben!");	//TODO translate
		alert.setContentText("Stammverzeichniss angeben?");

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
	
	//lädt die einstellungen aus der XML
	public void loadSettings(){
		try {
			if(System.getProperty("os.name").equals("Linux")){
				inputStream = new FileInputStream(fileLinux);
			}else{
				inputStream = new FileInputStream(fileWin);
			}
			props.loadFromXML(inputStream);
			path = props.getProperty("path");	//setzt Propselement in Pfad
			if(System.getProperty("os.name").equals("Linux")){
				streamingPathLinux = props.getProperty("streamingPath");
			}else{
				streamingPathWin = props.getProperty("streamingPath");
			}
			color = props.getProperty("color");
			size = Double.parseDouble(props.getProperty("size"));
			autoUpdate = props.getProperty("autoUpdate");
			local = Integer.parseInt(props.getProperty("local"));
			mode = props.getProperty("mode");
			inputStream.close();
		} catch (IOException e) {
			System.out.println("An error has occurred!");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}