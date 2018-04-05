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

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kellerkinder.Alerts.JFXInfoAlert;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;

import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.SortType;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import kellerkinder.HomeFlix.controller.DBController;
import kellerkinder.HomeFlix.controller.OMDbAPIController;
import kellerkinder.HomeFlix.controller.UpdateController;
import kellerkinder.HomeFlix.datatypes.SourceDataType;
import kellerkinder.HomeFlix.player.Player;
import kellerkinder.HomeFlix.datatypes.FilmTabelDataType;

public class MainWindowController {	
	
	@FXML
	private AnchorPane mainAnchorPane;
	
	@FXML
	private ScrollPane settingsScrollPane;
	
	@FXML
	private ScrollPane textScrollPane;
	
	@FXML
	private HBox topHBox;
	
	@FXML
	private VBox sideMenuVBox;
	
	@FXML
	private TreeTableView<FilmTabelDataType> filmsTreeTable;
	
	@FXML
	private TableView<SourceDataType> sourcesTable;

	@FXML
	private TextFlow textFlow;

	@FXML
	private JFXButton playbtn;
	
	@FXML
	private JFXButton openfolderbtn;
	
	@FXML
	private JFXButton returnBtn;
	
	@FXML
	private JFXButton forwardBtn;
	
    @FXML
    private JFXButton aboutBtn;
    
    @FXML
    private JFXButton settingsBtn;
    
    @FXML
    private JFXButton debugBtn;
    
    @FXML
    public JFXButton updateBtn;
    
    @FXML
    private JFXButton addDirectoryBtn;
    
    @FXML
    private JFXButton addStreamSourceBtn;
    
    @FXML
    private JFXHamburger menuHam;
    
    @FXML
    private JFXToggleButton autoUpdateToggleBtn;
    
    @FXML
    private JFXTextField searchTextField;
    
    @FXML
    public JFXColorPicker colorPicker;
    
	@FXML
	public ChoiceBox<String> languageChoisBox = new ChoiceBox<>();

	@FXML
	public ChoiceBox<String> branchChoisBox = new ChoiceBox<>();

	@FXML
	public JFXSlider fontsizeSlider;
	
    @FXML
    private Label homeflixSettingsLbl;
	
    @FXML
    private Label mainColorLbl;
	
	@FXML
	private Label fontsizeLbl;
	
    @FXML
    private Label languageLbl;
	
	@FXML
	private Label updateLbl;
	
	@FXML
	private Label branchLbl;
	
	@FXML
	private Label sourcesLbl;

	@FXML
	private Label versionLbl;

    @FXML
	private ImageView posterImageView;
    private ImageView imv1;
    
	@FXML
	private TreeItem<FilmTabelDataType> filmRoot = new TreeItem<>(new FilmTabelDataType("", "", "", "", false, false, imv1));
	@FXML
	private TreeTableColumn<FilmTabelDataType, String> columnStreamUrl = new TreeTableColumn<>("File Name");
	@FXML
	private TreeTableColumn<FilmTabelDataType, String> columnTitle = new TreeTableColumn<>("Title");
	@FXML
	private TreeTableColumn<FilmTabelDataType, String> columnSeason = new TreeTableColumn<>("Season");
	@FXML
	private TreeTableColumn<FilmTabelDataType, String> columnEpisode = new TreeTableColumn<>("Episode");
	@FXML
	private TreeTableColumn<FilmTabelDataType, ImageView> columnFavorite = new TreeTableColumn<>("Favorite");
    
    @FXML
    private TreeItem<SourceDataType> sourceRoot =new TreeItem<>(new SourceDataType("", ""));
    @FXML
    private TableColumn<SourceDataType, String> sourceColumn;
    @FXML
    private TableColumn<SourceDataType, String> modeColumn;
	
	private boolean menuTrue = false;
	private boolean settingsTrue = false;
	private boolean autoUpdate = false;
	private boolean useBeta = false;
    private static final Logger LOGGER = LogManager.getLogger(MainWindowController.class.getName());
	private int hashA = -647380320;
	
	private String version = "0.6.99";
	private String buildNumber = "147";
	private String versionName = "glowing vampire";
	private String dialogBtnStyle;
	private String color;
	private String title;
	private String streamUrl;
	private String ratingSortType;
	private String local;
	private String omdbAPIKey;
	
	// text strings
	private String errorPlay;
	private String errorLoad;
	private String errorSave;
	private String infoText;
	private String vlcNotInstalled;
	
	public double size;
	private int last;
	private int indexTable;
	private int indexList;
	private int next;
	private ResourceBundle bundle;
	private FilmTabelDataType currentFilm;
	
	private ObservableList<String> languages = FXCollections.observableArrayList("English (en_US)", "Deutsch (de_DE)");
	private ObservableList<String> branches = FXCollections.observableArrayList("stable", "beta");
	private ObservableList<FilmTabelDataType> filterData = FXCollections.observableArrayList();
	private ObservableList<FilmTabelDataType> filmsList = FXCollections.observableArrayList();
	private ObservableList<SourceDataType> sourcesList = FXCollections.observableArrayList();
	private ImageView skip_previous_white = new ImageView(new Image("icons/ic_skip_previous_white_18dp_1x.png"));
	private ImageView skip_previous_black = new ImageView(new Image("icons/ic_skip_previous_black_18dp_1x.png"));
	private ImageView skip_next_white = new ImageView(new Image("icons/ic_skip_next_white_18dp_1x.png"));
	private ImageView skip_next_black = new ImageView(new Image("icons/ic_skip_next_black_18dp_1x.png"));
	private ImageView play_arrow_white = new ImageView(new Image("icons/ic_play_arrow_white_18dp_1x.png"));
	private ImageView play_arrow_black = new ImageView(new Image("icons/ic_play_arrow_black_18dp_1x.png"));
    private MenuItem like = new MenuItem("like");
    private MenuItem dislike = new MenuItem("dislike");	//TODO one option (like or dislike)
	private ContextMenu menu = new ContextMenu(like, dislike);
	private Properties props = new Properties();
	
	private Main main;
	private MainWindowController mainWindowController;
	private UpdateController updateController;
	private OMDbAPIController omdbAPIController;
	private DBController dbController;
	
	/**
	 * "Main" Method called in Main.java main() when starting
	 * Initialize other objects: Updater, dbController and ApiQuery
	 */
	void setMain(Main main) {
		this.main = main;
		mainWindowController = this;
		dbController = new DBController(this.main, this);	
		omdbAPIController = new OMDbAPIController(this, dbController, this.main);
	}
	
	// call all init methods
	void init() {
		loadSettings();
		checkAutoUpdate();
		initTabel();
		initActions();
		initUI();
	}
	
	// Initialize the tables (treeTableViewfilm and sourcesTable)
	private void initTabel() {

		// film Table
		columnStreamUrl.setMaxWidth(0);
		columnTitle.setMaxWidth(190);
		columnFavorite.setMaxWidth(80);
		columnSeason.setMaxWidth(73);
		columnEpisode.setMaxWidth(77);
		columnFavorite.setStyle("-fx-alignment: CENTER;");

		filmsTreeTable.setRoot(filmRoot);
		filmsTreeTable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
		filmsTreeTable.setShowRoot(false);

		// write content into cell
		columnStreamUrl.setCellValueFactory(cellData -> cellData.getValue().getValue().streamUrlProperty());
		columnTitle.setCellValueFactory(cellData -> cellData.getValue().getValue().titleProperty());
		columnSeason.setCellValueFactory(cellData -> cellData.getValue().getValue().seasonProperty());
		columnEpisode.setCellValueFactory(cellData -> cellData.getValue().getValue().episodeProperty());
		columnFavorite.setCellValueFactory(cellData -> cellData.getValue().getValue().imageProperty());

		// add columns to treeTableViewfilm
		filmsTreeTable.getColumns().add(columnStreamUrl);
		filmsTreeTable.getColumns().add(columnTitle);
		filmsTreeTable.getColumns().add(columnFavorite);
		filmsTreeTable.getColumns().add(columnSeason);
		filmsTreeTable.getColumns().add(columnEpisode);
		filmsTreeTable.getColumns().get(0).setVisible(false); //hide columnStreamUrl (important)
	    
	    // context menu for treeTableViewfilm  
		filmsTreeTable.setContextMenu(menu);
	    
	    // sourcesTreeTable
	    sourceColumn.setCellValueFactory(cellData -> cellData.getValue().pathProperty());
	    modeColumn.setCellValueFactory(cellData -> cellData.getValue().modeProperty());
	    sourcesTable.setItems(sourcesList);
	}
	
	//Initializing the actions
	private void initActions() {

		HamburgerBackArrowBasicTransition burgerTask = new HamburgerBackArrowBasicTransition(menuHam);
		menuHam.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
			if (menuTrue == false) {
				sideMenuSlideIn();
				burgerTask.setRate(1.0);
				burgerTask.play();
				menuTrue = true;
			} else {
				sideMenuSlideOut();
				burgerTask.setRate(-1.0);
				burgerTask.play();
				menuTrue = false;
			}
			if (settingsTrue == true) {
				settingsScrollPane.setVisible(false);
				saveSettings();
				settingsTrue = false;
			}
		});

		searchTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				ObservableList<FilmTabelDataType> helpData;
				filterData.removeAll(filterData);
				filmRoot.getChildren().removeAll(filmRoot.getChildren());

				helpData = filmsList;


				for (int i = 0; i < helpData.size(); i++) {
					if (helpData.get(i).getTitle().toLowerCase().contains(searchTextField.getText().toLowerCase())) {
						filterData.add(helpData.get(i)); // add data from newDaten to filteredData where title contains search input
					}
				}

				for (int i = 0; i < filterData.size(); i++) {
					filmRoot.getChildren().add(new TreeItem<FilmTabelDataType>(filterData.get(i))); // add filtered data to root node after search
				}
				if (searchTextField.getText().hashCode() == hashA) {
					setColor("000000");
					applyColor();
				}
			}
		});
        
		languageChoisBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number value, Number new_value) {
				String local = languageChoisBox.getItems().get((int) new_value).toString();
				local = local.substring(local.length() - 6, local.length() - 1); // reading only en_US from English (en_US)
				setLocal(local);
				setLocalUI();
				saveSettings();
			}
		});

		branchChoisBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number value, Number new_value) {
				if (branchChoisBox.getItems().get((int) new_value).toString() == "beta") {
					setUseBeta(true);
				} else {
					setUseBeta(false);
				}
				saveSettings();
			}
		});
        
		fontsizeSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				setSize(fontsizeSlider.getValue());
				if (title != null) {
					dbController.readCache(streamUrl);
				}
				// ta1.setFont(Font.font("System", size));
				saveSettings();
			}
		});
        
		like.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dbController.like(streamUrl);
				dbController.refresh(streamUrl, indexList);
				refreshTable();
			}
		});
        
		dislike.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dbController.dislike(streamUrl);
				dbController.refresh(streamUrl, indexList);
				refreshTable();
			}
		});
        
		/**
		 * FIXME fix bug when sort by ASCENDING, wrong order
		 * FIXME when sorting, series are expanded
		 */
		columnFavorite.sortTypeProperty().addListener(new ChangeListener<SortType>() {
			@Override
			public void changed(ObservableValue<? extends SortType> paramObservableValue, SortType paramT1, SortType paramT2) {
				LOGGER.info("NAME Clicked -- sortType = " + paramT1 + ", SortType=" + paramT2);
				ArrayList<Integer> fav_true = new ArrayList<Integer>();
				ArrayList<Integer> fav_false = new ArrayList<Integer>();
				ObservableList<FilmTabelDataType> helpData;
				filterData.removeAll(filterData);
//				treeTableViewfilm.getSelectionModel().clearSelection(selected);
				filmRoot.getChildren().removeAll(filmRoot.getChildren());

				helpData = filmsList;

				for (int i = 0; i < helpData.size(); i++) {
					if (helpData.get(i).getFavorite() == true) {
						fav_true.add(i);
					} else {
						fav_false.add(i);
					}
				}
				if (paramT2.toString().equals("DESCENDING")) {
					LOGGER.info("Absteigend"); // Debug, delete?
					for (int i = 0; i < fav_true.size(); i++) {
						filterData.add(helpData.get(fav_true.get(i)));
					}
					for (int i = 0; i < fav_false.size(); i++) {
						filterData.add(helpData.get(fav_false.get(i)));
					}
				} else {
					for (int i = 0; i < fav_false.size(); i++) {
						filterData.add(helpData.get(fav_false.get(i)));
					}
					for (int i = 0; i < fav_true.size(); i++) {
						filterData.add(helpData.get(fav_true.get(i)));
					}
				}

				LOGGER.info(filterData.size()); // Debug, delete?
				for (int i = 0; i < filterData.size(); i++) {
//					LOGGER.info(filterData.get(i).getTitle()+"; "+filterData.get(i).getRating()); // Debugging
					// add filtered data to root node after search
					filmRoot.getChildren().add(new TreeItem<FilmTabelDataType>(filterData.get(i))); 				
				}
			}
		});
        
		// Change-listener for treeTableViewfilm
		filmsTreeTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<?> observable, Object oldVal, Object newVal) {
				indexTable = filmsTreeTable.getSelectionModel().getSelectedIndex(); // get selected item
				last = indexTable - 1;
				next = indexTable + 1;
				title = columnTitle.getCellData(indexTable); // get name of selected item
				streamUrl = columnStreamUrl.getCellData(indexTable); // get file path of selected item
				
				for (FilmTabelDataType helpData : filmsList) {
					if (helpData.getStreamUrl().equals(streamUrl)) {
						indexList = filmsList.indexOf(helpData);
					}
				}
				
				currentFilm = filmsList.get(indexList);
				
				if (filmsList.get(indexList).getCached()) {
					LOGGER.info("loading from cache: " + title);
					dbController.readCache(streamUrl);
				} else {
					omdbAPIController = new OMDbAPIController(mainWindowController, dbController, main);
					Thread omdbAPIThread = new Thread(omdbAPIController);
					omdbAPIThread.setName("OMDbAPI");
					omdbAPIThread.start();	
				}
			}
		});
	}
	
	// initialize UI elements
	private void initUI() {
		debugBtn.setDisable(true); // debugging button for tests
		debugBtn.setVisible(false);

		versionLbl.setText("Version: " + version + " (Build: " + buildNumber + ")");
		fontsizeSlider.setValue(getSize());
		colorPicker.setValue(Color.valueOf(getColor()));

		updateBtn.setFont(Font.font("System", 12));
		autoUpdateToggleBtn.setSelected(isAutoUpdate());
		languageChoisBox.setItems(languages);
		branchChoisBox.setItems(branches);
		
		if (isUseBeta()) {
			branchChoisBox.getSelectionModel().select(1);
		} else {
			branchChoisBox.getSelectionModel().select(0);
		}
		
		setLocalUI();
		applyColor();
	}
	
	@FXML
	private void playbtnclicked() {	
		if (isSupportedFormat(currentFilm)) {
			new Player(currentFilm, dbController);
		} else {
			LOGGER.error("using fallback player!");
			
			if (System.getProperty("os.name").contains("Linux")) {
				String line;
				String output = "";
				Process p;
				try {
					p = Runtime.getRuntime().exec("which vlc");
					BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
					while ((line = input.readLine()) != null) {
						output = line;
					}
					LOGGER.info(output);
					input.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if (output.contains("which: no vlc") || output == "") {
					JFXInfoAlert vlcInfoAlert = new JFXInfoAlert("Info", vlcNotInstalled, dialogBtnStyle, main.getPrimaryStage());
					vlcInfoAlert.showAndWait();
				} else {
					try {
						new ProcessBuilder("vlc", streamUrl).start();
					} catch (IOException e1) {
						showErrorMsg(errorPlay, e1);
					}
				}
				
			} else if (System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac OS X")) {
				try {
					Desktop.getDesktop().open(new File(streamUrl));
				} catch (IOException e1) {
					showErrorMsg(errorPlay, e1);
				}
			} else {
				LOGGER.error(System.getProperty("os.name") + ", OS is not supported, please contact a developer! ");
			}
		}
	}
	
	/** TODO improve function
	 * check if a film is supported by the HomeFlixPlayer or not
	 * @param entry the film you want to check
	 * @return true if so, false if not
	 */
	private boolean isSupportedFormat(FilmTabelDataType film) {
		 String mimeType = URLConnection.guessContentTypeFromName(film.getStreamUrl());    
		 return mimeType != null && mimeType.contains("mp4");
	}
	
	@FXML
	private void openfolderbtnclicked() {
		String dest = new File(streamUrl).getParentFile().getAbsolutePath();
		if (!System.getProperty("os.name").contains("Linux")) {
			try {
				Desktop.getDesktop().open(new File(dest));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void returnBtnclicked(){
		filmsTreeTable.getSelectionModel().select(last);
	}
	
	@FXML
	private void forwardBtnclicked(){
		filmsTreeTable.getSelectionModel().select(next);
	}
	
	@FXML
	private void aboutBtnAction() {
		String bodyText = "cemu_UI by @Seil0 \nVersion: " + version + " (Build: " + buildNumber + ")  \""
				+ versionName + "\" \n" + infoText;
		JFXInfoAlert infoAlert = new JFXInfoAlert("Project HomeFlix", bodyText, dialogBtnStyle, main.getPrimaryStage());
		infoAlert.showAndWait();
	}
	
	@FXML
	private void settingsBtnclicked(){
		if(settingsTrue == false){
			settingsScrollPane.setVisible(true);	
			settingsTrue = true;
		}else{
			settingsScrollPane.setVisible(false);
			saveSettings();
			settingsTrue = false;
		}
	}
	
	@FXML
	private void debugBtnclicked(){
		//for testing
	}
	
	@FXML
	private void addDirectoryBtnAction(){
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle(bundle.getString("addDirectory"));
		File selectedFolder = directoryChooser.showDialog(main.getPrimaryStage());
		if (selectedFolder != null && selectedFolder.exists()) {
			mainWindowController.addSource(selectedFolder.getPath(), "local");
		} else {
			LOGGER.error("The selected folder dosen't exist!");
		}
	}
	
	@FXML
	private void addStreamSourceBtnAction(){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("addStreamSource");
		File selectedFile = fileChooser.showOpenDialog(main.getPrimaryStage());
		if (selectedFile != null && selectedFile.exists()) {
			addSource(selectedFile.getPath(), "stream");
			dbController.refreshDataBase();
		} else {
			LOGGER.error("The selected file dosen't exist!");
		}
	}
	
	@FXML
	private void colorPickerAction(){
		editColor(colorPicker.getValue().toString());
		applyColor();
	}
	
	@FXML
	private void updateBtnAction(){
		updateController = new UpdateController(this, buildNumber, useBeta);
		Thread updateThread = new Thread(updateController);
		updateThread.setName("Updater");
		updateThread.start();	
	}
	
	@FXML
	private void autoUpdateToggleBtnAction(){
		if (autoUpdate) {
			setAutoUpdate(false);
		} else {
			setAutoUpdate(true);
		}
		saveSettings();
	}
	
	// refresh the selected child of the root node
	private void refreshTable() {
		filmRoot.getChildren().get(indexTable).setValue(filmsList.get(indexList));
	}
	
	/**
	 * add data from films-list to films-table
	 */
	public void addDataUI() {

		for (FilmTabelDataType element : filmsList) {
			
			// only if the entry contains a season and a episode it's a valid series
			if (!element.getSeason().isEmpty() && !element.getEpisode().isEmpty()) {
//				System.out.println("Found Series: " + element.getTitle());
				// check if there is a series node to add the item
				for (int i = 0; i < filmRoot.getChildren().size(); i++) {
					if (filmRoot.getChildren().get(i).getValue().getTitle().equals(element.getTitle())) {
						// if a root node exists, add element as child
//						System.out.println("Found a root node to add child");
//						System.out.println("Adding: " + element.getStreamUrl());
						TreeItem<FilmTabelDataType> episodeNode = new TreeItem<>(new FilmTabelDataType(element.getStreamUrl(),
								element.getTitle(), element.getSeason(), element.getEpisode(), element.getFavorite(),
								element.getCached(), element.getImage()));
						filmRoot.getChildren().get(i).getChildren().add(episodeNode);
					} else if (i == filmRoot.getChildren().size() - 1) {
						// if no root node exists, create one and add element as child
//						System.out.println("Create a root node to add child");
//						System.out.println("Adding: " + element.getStreamUrl());
						// TODO get the last watched episode, the first one with currentTime != 0
						TreeItem<FilmTabelDataType> seriesRootNode = new TreeItem<>(new FilmTabelDataType(element.getStreamUrl(),
								element.getTitle(), "", "", element.getFavorite(), element.getCached(), element.getImage()));
						filmRoot.getChildren().add(seriesRootNode);
					}
				}
			} else {
				filmRoot.getChildren().add(new TreeItem<FilmTabelDataType>(element)); // add data to root-node
			}
		}
	}
	
	// add a source to the sources table on the settings pane
	public void addSourceToTable(String path, String mode) {
		sourcesList.add(new SourceDataType(path, mode));
		sourceRoot.getChildren().add(new TreeItem<SourceDataType>(sourcesList.get(sourcesList.size() - 1))); // adds data to root-node
	}
	
	// add a source to the newsources list
	public void addSource(String path, String mode) {
		JsonObject source = null;
		JsonArray newsources = null;

		try {
			// read old array
			File oldSources = new File(main.getDirectory() + "/sources.json");
			if (oldSources.exists()) {
				newsources = Json.parse(new FileReader(main.getDirectory() + "/sources.json")).asArray();
			} else {
				newsources = Json.array();
			}

			// add new source
			source = Json.object().add("path", path).add("mode", mode);
			newsources.add(source);
			Writer writer = new FileWriter(main.getDirectory() + "/sources.json");
			newsources.writeTo(writer);
			writer.close();
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}
	
	//set color of UI-Elements
	/**
	 * set the color of the GUI-Elements
	 * if usedColor is less than checkColor set text fill white, else black
	 */
	private void applyColor() {
		String style = "-fx-background-color: #" + getColor() + ";";
		String btnStyleBlack = "-fx-button-type: RAISED; -fx-background-color: #" + getColor() + "; -fx-text-fill: BLACK;";
		String btnStyleWhite = "-fx-button-type: RAISED; -fx-background-color: #" + getColor() + "; -fx-text-fill: WHITE;";
		BigInteger usedColor = new BigInteger(getColor(), 16);
		BigInteger checkColor = new BigInteger("78909cff", 16);

		sideMenuVBox.setStyle(style);
		topHBox.setStyle(style);
		searchTextField.setFocusColor(Color.valueOf(getColor()));

		if (usedColor.compareTo(checkColor) == -1) {
			dialogBtnStyle = btnStyleWhite;
			settingsBtn.setStyle("-fx-text-fill: WHITE;");
			aboutBtn.setStyle("-fx-text-fill: WHITE;");
			debugBtn.setStyle("-fx-text-fill: WHITE;");
			addDirectoryBtn.setStyle(btnStyleWhite);
			addStreamSourceBtn.setStyle(btnStyleWhite);
			updateBtn.setStyle(btnStyleWhite);
			playbtn.setStyle(btnStyleWhite);
			openfolderbtn.setStyle(btnStyleWhite);
			returnBtn.setStyle(btnStyleWhite);
			forwardBtn.setStyle(btnStyleWhite);
			playbtn.setGraphic(play_arrow_white);
			returnBtn.setGraphic(skip_previous_white);
			forwardBtn.setGraphic(skip_next_white);
			menuHam.getStyleClass().add("jfx-hamburgerW");
		} else {
			dialogBtnStyle = btnStyleBlack;
			settingsBtn.setStyle("-fx-text-fill: BLACK;");
			aboutBtn.setStyle("-fx-text-fill: BLACK;");
			debugBtn.setStyle("-fx-text-fill: BLACK;");
			addDirectoryBtn.setStyle(btnStyleBlack);
			addStreamSourceBtn.setStyle(btnStyleBlack);
			updateBtn.setStyle(btnStyleBlack);
			playbtn.setStyle(btnStyleBlack);
			openfolderbtn.setStyle(btnStyleBlack);
			returnBtn.setStyle(btnStyleBlack);
			forwardBtn.setStyle(btnStyleBlack);
			playbtn.setGraphic(play_arrow_black);
			returnBtn.setGraphic(skip_previous_black);
			forwardBtn.setGraphic(skip_next_black);
			menuHam.getStyleClass().add("jfx-hamburgerB");
		}
	}
	
	// slide in in 400ms
	private void sideMenuSlideIn() {
		sideMenuVBox.setVisible(true);
		TranslateTransition translateTransition = new TranslateTransition(Duration.millis(400), sideMenuVBox);
		translateTransition.setFromX(-150);
		translateTransition.setToX(0);
		translateTransition.play();
	}
	
	// slide out in 400ms
	private void sideMenuSlideOut() {
		TranslateTransition translateTransition = new TranslateTransition(Duration.millis(400), sideMenuVBox);
		translateTransition.setFromX(0);
		translateTransition.setToX(-150);
		translateTransition.play();
	}
	
	/**
	 * set the local based on the languageChoisBox selection
	 */
	void setLocalUI() {
		switch (getLocal()) {
		case "en_US":
			setBundle(ResourceBundle.getBundle("locals.HomeFlix-Local", Locale.US)); // us_English
			languageChoisBox.getSelectionModel().select(0);
			break;
		case "de_DE":
			setBundle(ResourceBundle.getBundle("locals.HomeFlix-Local", Locale.GERMAN)); // German
			languageChoisBox.getSelectionModel().select(1);
			break;
		default:
			setBundle(ResourceBundle.getBundle("locals.HomeFlix-Local", Locale.US)); // default local
			languageChoisBox.getSelectionModel().select(0);
			break;
		}
		aboutBtn.setText(getBundle().getString("info"));
		settingsBtn.setText(getBundle().getString("settings"));
		searchTextField.setPromptText(getBundle().getString("tfSearch"));
		openfolderbtn.setText(getBundle().getString("openFolder"));
		updateBtn.setText(getBundle().getString("checkUpdates"));
		addDirectoryBtn.setText(getBundle().getString("addDirectory"));
		addStreamSourceBtn.setText(getBundle().getString("addStreamSource"));
		homeflixSettingsLbl.setText(getBundle().getString("homeflixSettingsLbl"));
		mainColorLbl.setText(getBundle().getString("mainColorLbl"));
		fontsizeLbl.setText(getBundle().getString("fontsizeLbl"));
		languageLbl.setText(getBundle().getString("languageLbl"));
		autoUpdateToggleBtn.setText(getBundle().getString("autoUpdate"));
		branchLbl.setText(getBundle().getString("branchLbl"));
		columnStreamUrl.setText(getBundle().getString("columnStreamUrl"));
		columnTitle.setText(getBundle().getString("columnName"));
		columnSeason.setText(getBundle().getString("columnSeason"));
		columnEpisode.setText(getBundle().getString("columnEpisode"));
		columnFavorite.setText(getBundle().getString("columnFavorite"));
		errorPlay = getBundle().getString("errorPlay");
		errorLoad = getBundle().getString("errorLoad");
		errorSave = getBundle().getString("errorSave");
		infoText = getBundle().getString("infoText");
		vlcNotInstalled = getBundle().getString("vlcNotInstalled");
	}
	
	// TODO rework after #19 has landed
	public void showErrorMsg(String msg, Exception exception) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("");
		alert.setContentText(msg);
		alert.initOwner(main.getPrimaryStage());

		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		String exceptionText = sw.toString();

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);
		alert.showAndWait();
		LOGGER.error("An error occurred", exception);
	}
	
	/**
	 * save the configuration to the config.xml file
	 */
	public void saveSettings() {
		LOGGER.info("saving settings ...");
		try {
			props.setProperty("color", getColor());
			props.setProperty("autoUpdate", String.valueOf(isAutoUpdate()));
			props.setProperty("useBeta", String.valueOf(isUseBeta()));
			props.setProperty("size", getSize().toString());
			props.setProperty("local", getLocal());
			props.setProperty("ratingSortType", columnFavorite.getSortType().toString());

			OutputStream outputStream = new FileOutputStream(main.getConfigFile()); // new output-stream
			props.storeToXML(outputStream, "Project HomeFlix settings"); // write new .xml
			outputStream.close();
		} catch (IOException e) {
			LOGGER.error(errorLoad, e);
		}
	}
	
	/**
	 * load the configuration from the config.xml file
	 * and try to load the API keys from apiKeys.json
	 */
	public void loadSettings() {
		LOGGER.info("loading settings ...");
		
		try {
			InputStream inputStream = new FileInputStream(main.getConfigFile());
			props.loadFromXML(inputStream); // new input-stream from .xml

			try {
				setColor(props.getProperty("color"));
			} catch (Exception e) {
				LOGGER.error("cloud not load color", e);
				setColor("");
			}

			try {
				setSize(Double.parseDouble(props.getProperty("size")));
			} catch (Exception e) {
				LOGGER.error("cloud not load fontsize", e);
				setSize(17.0);
			}

			try {
				setAutoUpdate(Boolean.parseBoolean(props.getProperty("autoUpdate")));
			} catch (Exception e) {
				LOGGER.error("cloud not load autoUpdate", e);
				setAutoUpdate(false);
			}
			
			try {
				setUseBeta(Boolean.parseBoolean(props.getProperty("useBeta")));
			} catch (Exception e) {
				LOGGER.error("cloud not load autoUpdate", e);
				setUseBeta(false);
			}

			try {
				setLocal(props.getProperty("local"));
			} catch (Exception e) {
				LOGGER.error("cloud not load local", e);
				setLocal(System.getProperty("user.language") + "_" + System.getProperty("user.country"));
			}

			try {
				setRatingSortType(props.getProperty("ratingSortType"));
			} catch (Exception e) {
				LOGGER.error("cloud not load autoUpdate", e);
				setRatingSortType("");
			}

			inputStream.close();
		} catch (IOException e) {
			LOGGER.error(errorSave, e);
		}
		
		// try loading the omdbAPI key
		try {
			InputStream in = getClass().getClassLoader().getResourceAsStream("apiKeys.json");
			if (in != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				JsonObject apiKeys = Json.parse(reader).asObject();
				omdbAPIKey = apiKeys.getString("omdbAPIKey", "");
				reader.close();
				in.close();
			} else {
				LOGGER.warn("Cloud not load apiKeys.json. No such file");
			}
		} catch (Exception e) {
			LOGGER.error("Cloud not load the omdbAPI key. Please contact the developer!", e);
		}
	}
	
	// if AutoUpdate, then check for updates
	private void checkAutoUpdate() {

		if (isAutoUpdate()) {
			try {
				LOGGER.info("AutoUpdate: looking for updates on startup ...");
				updateController = new UpdateController(this, buildNumber, useBeta);
				Thread updateThread = new Thread(updateController);
				updateThread.setName("Updater");
				updateThread.start();
				updateThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	// cuts 0x of the Color-pickers return value
	private void editColor(String input) {
		StringBuilder sb = new StringBuilder(input);
		sb.delete(0, 2);
		this.color = sb.toString();
		saveSettings();
	}

	// getter and setter
	public DBController getDbController() {
		return dbController;
	}

	public void setColor(String input) {
		this.color = input;
	}

	public String getColor() {
		return color;
	}

	public String getTitle() {
		return title;
	}

	public String getStreamUrl() {
		return streamUrl;
	}

	public void setSize(Double input) {
		this.size = input;
	}

	public Double getSize() {
		return size;
	}

	public int getIndexTable() {
		return indexTable;
	}
	
	public int getIndexList() {
		return indexList;
	}

	public void setAutoUpdate(boolean input) {
		this.autoUpdate = input;
	}

	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	public boolean isUseBeta() {
		return useBeta;
	}

	public void setUseBeta(boolean useBeta) {
		this.useBeta = useBeta;
	}

	public void setLocal(String input) {
		this.local = input;
	}

	public String getLocal() {
		return local;
	}

	public String getOmdbAPIKey() {
		return omdbAPIKey;
	}

	public ObservableList<FilmTabelDataType> getFilmsList() {
		return filmsList;
	}

	public ObservableList<SourceDataType> getSourcesList() {
		return sourcesList;
	}

	public String getRatingSortType() {
		return ratingSortType;
	}

	public void setRatingSortType(String ratingSortType) {
		this.ratingSortType = ratingSortType;
	}

	public ResourceBundle getBundle() {
		return bundle;
	}

	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	public TreeTableView<FilmTabelDataType> getFilmsTreeTable() {
		return filmsTreeTable;
	}

	public TextFlow getTextFlow() {
		return textFlow;
	}

	public ImageView getPosterImageView() {
		return posterImageView;
	}

	public JFXButton getUpdateBtn() {
		return updateBtn;
	}

	public TreeItem<FilmTabelDataType> getFilmRoot() {
		return filmRoot;
	}

	public TreeItem<SourceDataType> getSourceRoot() {
		return sourceRoot;
	}
}
