/**
 * Project-HomeFlix
 *
 * Copyright 2016-2018  <@Seil0>
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
package kellerkinder.HomeFlix.application;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kellerkinder.Alerts.JFX2BtnCancelAlert;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

	private Stage primaryStage;
	private Scene scene;
	private AnchorPane pane;
	private MainWindowController mainWindowController;
	private static String userHome = System.getProperty("user.home");
	private static String userName = System.getProperty("user.name");
	private static String osName = System.getProperty("os.name");
	private static String osArch = System.getProperty("os.arch");
	private static String osVers = System.getProperty("os.version");
	private static String javaVers = System.getProperty("java.version");
	private static String javaVend = System.getProperty("java.vendor");
	private static String local = System.getProperty("user.language") + "_" + System.getProperty("user.country");
	private String dirWin = userHome + "/Documents/HomeFlix"; // Windows: C:/Users/"User"/Documents/HomeFlix
	private String dirLinux = userHome + "/HomeFlix"; // Linux: /home/"User"/HomeFlix
	private File directory;
	private File configFile;
	private File posterCache;
	private ResourceBundle bundle;
	private static Logger LOGGER;

	@Override
	public void start(Stage primaryStage) throws IOException {
		LOGGER.info("OS: " + osName + " " + osVers + " " + osArch);
		LOGGER.info("Java: " + javaVend + " " + javaVers);
		LOGGER.info("User: " + userName + " " + userHome);

		this.primaryStage = primaryStage;
		mainWindow();
	}

	/**
	 * initialize the mainWindowController, GUI and load the saved settings or call addFirstSource
	 * initialize the primaryStage and set the file/directory paths
	 */
	private void mainWindow(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ClassLoader.getSystemResource("fxml/MainWindow.fxml"));
			pane = (AnchorPane) loader.load();
			primaryStage.setMinHeight(600.00);
			primaryStage.setMinWidth(1000.00);
			primaryStage.setResizable(false);
			primaryStage.setTitle("Project HomeFlix");
			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/icons/Homeflix_Icon_64x64.png"))); //adds application icon	
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					System.exit(1);
				}
			});
			
			mainWindowController = loader.getController();	//Link of FXMLController and controller class
			mainWindowController.setMain(this);	//call setMain

			
			// get OS and the specific paths
			if (osName.contains("Windows")) {
				directory = new File(dirWin);
				configFile = new File(dirWin + "/config.xml");
				posterCache = new File(dirWin + "/posterCache");
			} else {
				directory = new File(dirLinux);
				configFile = new File(dirLinux + "/config.xml");
				posterCache = new File(dirLinux + "/posterCache");
			}
			
			// generate window
			scene = new Scene(pane); // create new scene, append pane to scene
			scene.getStylesheets().add(getClass().getResource("/css/MainWindow.css").toExternalForm());
			primaryStage.setScene(scene); // append scene to stage
			primaryStage.show(); // show stage
			
			// startup checks
			if (!configFile.exists()) {
				directory.mkdir();		
				addFirstSource();
				mainWindowController.setColor("ee3523");
				mainWindowController.setFontSize(17.0);
				mainWindowController.setAutoUpdate(false);
				mainWindowController.setLocal(local);
				mainWindowController.saveSettings();
			}

			if (!posterCache.exists()) {
				posterCache.mkdir();
			}
			
			// init here as it loads the games to the mwc and the gui, therefore the window must exist
			mainWindowController.init();
			mainWindowController.getDbController().init();
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * we need to get the path for the first source from the user and add it to 
	 * sources.json, if the user ends the file-/directory-chooser the program will exit
	 */
	private void addFirstSource() {
		switch (local) {
		case "en_US":
			bundle = ResourceBundle.getBundle("locals.HomeFlix-Local", Locale.US); // us_english
			break;
		case "de_DE":
			bundle = ResourceBundle.getBundle("locals.HomeFlix-Local", Locale.GERMAN); // German
			break;
		default:
			bundle = ResourceBundle.getBundle("locals.HomeFlix-Local", Locale.US); // default local
			break;
		}
		
		JFX2BtnCancelAlert selectFirstSource = new JFX2BtnCancelAlert(bundle.getString("addSourceHeader"),
				bundle.getString("addSourceBody"),
				"-fx-button-type: RAISED; -fx-background-color: #ee3523; -fx-text-fill: BLACK;",
				bundle.getString("addDirectory"), bundle.getString("addStreamSource"),
				bundle.getString("cancelBtnText"), primaryStage);

		// directory action
		EventHandler<ActionEvent> btn1Action = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				directoryChooser.setTitle(bundle.getString("addDirectory"));
				File selectedFolder = directoryChooser.showDialog(primaryStage);
				if (selectedFolder != null && selectedFolder.exists()) {
					mainWindowController.addSource(selectedFolder.getPath(), "local");
					selectFirstSource.getAlert().close();
				} else {
					LOGGER.error("The selected folder dosen't exist!");
					System.exit(1);
				}
			}
		};

		// streaming action
		EventHandler<ActionEvent> btn2Action = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("addStreamSource");
				File selectedFile = fileChooser.showOpenDialog(getPrimaryStage());
				if (selectedFile != null && selectedFile.exists()) {
					mainWindowController.addSource(selectedFile.getPath(), "stream");
					selectFirstSource.getAlert().close();
				} else {
					LOGGER.error("The selected file dosen't exist!");
					System.exit(1);
				}
			}
		};
		selectFirstSource.setBtn1Action(btn1Action);
		selectFirstSource.setBtn2Action(btn2Action);
		selectFirstSource.showAndWait();
	}

	/**
	 * set the log file location and initialize the logger
	 * launch the GUI
	 * @param args arguments given at the start
	 */
	public static void main(String[] args) {
		if (System.getProperty("os.name").equals("Windows")) {
			System.setProperty("logFilename", userHome + "/Documents/HomeFlix/app.log");
			File logFile = new File(userHome + "/Documents/HomeFlix/app.log");
			logFile.delete();
		} else {
			System.setProperty("logFilename", userHome + "/HomeFlix/app.log");
			File logFile = new File(userHome + "/HomeFlix/app.log");
			logFile.delete();
		}
		LOGGER = LogManager.getLogger(Main.class.getName());
		launch(args);
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public AnchorPane getPane() {
		return pane;
	}

	public File getDirectory() {
		return directory;
	}

	public File getConfigFile() {
		return configFile;
	}

	public File getPosterCache() {
		return posterCache;
	}
}