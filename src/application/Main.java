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
	private String color = "ee3523";
	private String autoUpdate = "0";
	private double size = 12;
	private int local = 0;
	Properties props = new Properties();
	private MainWindowController mainWindowController;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		mainWindow();
	}
	
	public void mainWindow(){
		File file = new File("config.xml");
		try {
		FXMLLoader loader = new FXMLLoader(Main.class.getResource("MainWindow.fxml"));
		AnchorPane pane = loader.load();
		primaryStage.setMinHeight(600.00);
		primaryStage.setMinWidth(900.00);
		primaryStage.setResizable(false);
		primaryStage.setTitle("Project HomeFlix");
		primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/recources/Homeflix_Icon_64x64.png"))); //fügt Anwendungsicon hinzu

		
		mainWindowController = loader.getController();	//verknüpfung von FXMLController und Controller Klasse
		mainWindowController.setAutoUpdate(autoUpdate);	//setzt autoupdate
		mainWindowController.setMain(this);	//aufruf setMain
		
		//prüft ob config.xml vorhanden, wenn nicht hole Pfad und schreibe Daten in Controller
		if (file.exists() != true) {
			mainWindowController.setPath(firstStart());
			mainWindowController.setColor(color);
			mainWindowController.setSize(size);
			mainWindowController.setAutoUpdate(autoUpdate);
			mainWindowController.setLoaclUI(local);
			mainWindowController.saveSettings();
			Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar");	//starte neu um Bugs zu verhindern
			System.exit(0);	//beendet sich selbst
		}else{
			loadSettings();
		}

		mainWindowController.applyColor();	//setzt die Theme Farbe
		mainWindowController.cbLocal.getSelectionModel().select(mainWindowController.getLocal()); //setzt local
		mainWindowController.mainColor.setValue(Color.valueOf(mainWindowController.getColor()));
		mainWindowController.loadData();	//läd die Daten im Controller
		
		Scene scene = new Scene(pane);	//neue Scen um inhalt der stage anzuzeigen
		
		primaryStage.setScene(scene);
		primaryStage.show();	//zeige scene
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//methode für den erstmaligen Start
	private String firstStart(){
		Alert alert = new Alert(AlertType.CONFIRMATION);	//neuer alert mit filechooser
		alert.setTitle("Project HomeFlix");
		alert.setHeaderText("Es ist kein Stammverzeichniss für Filme angegeben!");
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
		File configFile = new File("config.xml");
		try {
			InputStream inputStream = new FileInputStream(configFile);
			props.loadFromXML(inputStream);
			path = props.getProperty("path");
			color = props.getProperty("color");
			autoUpdate = props.getProperty("autoUpdate");
			size = Double.parseDouble(props.getProperty("size"));
			local = Integer.parseInt(props.getProperty("local"));
			
			inputStream.close();
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}