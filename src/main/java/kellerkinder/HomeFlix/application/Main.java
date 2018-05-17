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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kellerkinder.Alerts.JFX2BtnCancelAlert;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

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
	private static String dirHomeFlix;
	private static File directory;
	private static File configFile;
	private static File posterCache;
	private ResourceBundle bundle;
	private static Logger LOGGER;
	private Properties props = new Properties();

	@Override
	public void start(Stage primaryStage) throws IOException {
		LOGGER.info("OS: " + osName + " " + osVers + " " + osArch);
		LOGGER.info("Java: " + javaVend + " " + javaVers);
		LOGGER.info("User: " + userName + " " + userHome);

		this.primaryStage = primaryStage;
		mainWindowController = new MainWindowController(this);
		
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
			loader.setController(mainWindowController);
			pane = (AnchorPane) loader.load();
			primaryStage.setMinHeight(600.00);
			primaryStage.setMinWidth(1000.00);
			primaryStage.setResizable(false);
			primaryStage.setTitle("Project HomeFlix");
			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/icons/Homeflix_Icon_64x64.png"))); //adds application icon	
			primaryStage.setOnCloseRequest(event -> System.exit(1));

			// generate window
			scene = new Scene(pane); // create new scene, append pane to scene
			scene.getStylesheets().add(getClass().getResource("/css/MainWindow.css").toExternalForm());
			primaryStage.setScene(scene); // append scene to stage
			primaryStage.show(); // show stage
			
			// startup checks TODO move to mwc
			if (!configFile.exists()) {
				directory.mkdir();

				addFirstSource();
				mainWindowController.setColor("ee3523");
				mainWindowController.setFontSize(17.0);
				mainWindowController.setAutoUpdate(false);
				mainWindowController.setLocal(local);
				saveSettings();
			}

			if (!posterCache.exists()) {
				posterCache.mkdir();
			}
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * set the log file location and initialize the logger launch the GUI
	 * @param args arguments given at the start
	 */
	public static void main(String[] args) {

		if (osName.contains("Windows")) {
			dirHomeFlix = userHome + "/Documents/HomeFlix";
		} else {
			dirHomeFlix = userHome + "/HomeFlix";
		}
		
		// set the concrete files
		directory = new File(dirHomeFlix);
		configFile = new File(dirHomeFlix + "/config.xml");
		posterCache = new File(dirHomeFlix + "/posterCache");

		System.setProperty("logFilename", dirHomeFlix + "/app.log");
		File logFile = new File(dirHomeFlix + "/app.log");
		logFile.delete();
		LOGGER = LogManager.getLogger(Main.class.getName());
		launch(args);
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
			bundle = ResourceBundle.getBundle("locals.HomeFlix-Local", Locale.GERMAN); // de_german
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
	 * save the configuration to the config.xml file
	 */
	public void saveSettings() {
		LOGGER.info("saving settings ...");
		try {
			props.setProperty("color", mainWindowController.getColor());
			props.setProperty("autoUpdate", String.valueOf(mainWindowController.isAutoUpdate()));
			props.setProperty("useBeta", String.valueOf(mainWindowController.isUseBeta()));
			props.setProperty("autoplay", String.valueOf(mainWindowController.isAutoplay()));
			props.setProperty("size", mainWindowController.getFontSize().toString());
			props.setProperty("local", mainWindowController.getLocal());

			OutputStream outputStream = new FileOutputStream(getConfigFile()); // new output-stream
			props.storeToXML(outputStream, "Project HomeFlix settings"); // write new .xml
			outputStream.close();
		} catch (IOException e) {
			LOGGER.error("An error occurred while saving the settings!", e);
		}
	}
	
	/**
	 * load the configuration from the config.xml file
	 * and try to load the API keys from apiKeys.json
	 */
	public void loadSettings() {
		LOGGER.info("loading settings ...");
		
		try {
			InputStream inputStream = new FileInputStream(getConfigFile());
			props.loadFromXML(inputStream); // new input-stream from .xml

			try {
				mainWindowController.setColor(props.getProperty("color"));
			} catch (Exception e) {
				LOGGER.error("cloud not load color", e);
				mainWindowController.setColor("00a8cc");
			}

			try {
				mainWindowController.setFontSize(Double.parseDouble(props.getProperty("size")));
			} catch (Exception e) {
				LOGGER.error("cloud not load fontsize", e);
				mainWindowController.setFontSize(17.0);
			}

			try {
				mainWindowController.setAutoUpdate(Boolean.parseBoolean(props.getProperty("autoUpdate")));
			} catch (Exception e) {
				LOGGER.error("cloud not load autoUpdate", e);
				mainWindowController.setAutoUpdate(false);
			}
			
			try {
				mainWindowController.setUseBeta(Boolean.parseBoolean(props.getProperty("useBeta")));
			} catch (Exception e) {
				LOGGER.error("cloud not load autoUpdate", e);
				mainWindowController.setUseBeta(false);
			}
			
			try {
				mainWindowController.setAutoplay(Boolean.parseBoolean(props.getProperty("autoplay")));
			} catch (Exception e) {
				LOGGER.error("cloud not load autoplay", e);
				mainWindowController.setAutoplay(false);
			}

			try {
				mainWindowController.setLocal(props.getProperty("local"));
			} catch (Exception e) {
				LOGGER.error("cloud not load local", e);
				mainWindowController.setLocal(System.getProperty("user.language") + "_" + System.getProperty("user.country"));
			}

			inputStream.close();
		} catch (IOException e) {
			LOGGER.error("An error occurred while loading the settings!", e);
		}
		
		// try loading the omdbAPI key
		try {
			InputStream in = getClass().getClassLoader().getResourceAsStream("apiKeys.json");
			if (in != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				JsonObject apiKeys = Json.parse(reader).asObject();
				mainWindowController.setOmdbAPIKey(apiKeys.getString("omdbAPIKey", ""));
				reader.close();
				in.close();
			} else {
				LOGGER.warn("Cloud not load apiKeys.json. No such file");
			}
		} catch (Exception e) {
			LOGGER.error("Cloud not load the omdbAPI key. Please contact the developer!", e);
		}
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