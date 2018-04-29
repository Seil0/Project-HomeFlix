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
import java.io.Writer;
import java.math.BigInteger;
import java.net.URLConnection;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.SortType;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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
	private AnchorPane tableModeAnchorPane;
	
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
	private JFXButton updateBtn;
	@FXML
	private JFXButton addDirectoryBtn;
	@FXML
	private JFXButton addStreamSourceBtn;
	
	@FXML
	private JFXHamburger menuHam;

	@FXML
	private JFXToggleButton autoUpdateToggleBtn;
	@FXML
	private JFXToggleButton autoplayToggleBtn;

	@FXML
	private JFXTextField searchTextField;

	@FXML
	private JFXColorPicker colorPicker;

	@FXML
	private ChoiceBox<String> languageChoisBox = new ChoiceBox<>();
	@FXML
	private ChoiceBox<String> branchChoisBox = new ChoiceBox<>();

	@FXML
	private JFXSlider fontsizeSlider;

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
	private TreeItem<SourceDataType> sourceRoot = new TreeItem<>(new SourceDataType("", ""));
	@FXML
	private TableColumn<SourceDataType, String> sourceColumn;
	@FXML
	private TableColumn<SourceDataType, String> modeColumn;
	
	private Main main;
	private MainWindowController mainWindowController;
	private UpdateController updateController;
	private OMDbAPIController omdbAPIController;
	private DBController dbController;
	private static final Logger LOGGER = LogManager.getLogger(MainWindowController.class.getName());
	
	private boolean menuTrue = false;
	private boolean settingsTrue = false;
	private boolean autoUpdate = false;
	private boolean useBeta = false;
	private boolean autoplay = false;

	private final String version = "0.7.0";
	private final String buildNumber = "155";
	private final String versionName = "toothless dragon";
	private String btnStyle;
	private String color;
	private String local;
	private String omdbAPIKey;
	
	private double fontSize;
	private final int hashA = -647380320;
	private int last;
	private int indexTable;
	private int indexList;
	private int next;
	private ResourceBundle bundle;
	private FilmTabelDataType currentTableFilm = new FilmTabelDataType("", "", "", "", false, false, null);
	
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
	private MenuItem dislike = new MenuItem("dislike"); // TODO one option (like or dislike)
	private ContextMenu menu = new ContextMenu(like, dislike);
	private Properties props = new Properties();
	
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
	
	// call all initialize methods
	void init() {
		LOGGER.info("Initializing Project-HomeFlix build " + buildNumber);
		loadSettings();
		checkAutoUpdate();
		initTabel();
		initUI();
		initActions();
	}
	
	// Initialize UI elements
	private void initUI() {
		versionLbl.setText("Version: " + version + " (Build: " + buildNumber + ")");
		fontsizeSlider.setValue(getFontSize());
		colorPicker.setValue(Color.valueOf(getColor()));

		updateBtn.setFont(Font.font("System", 12));
		autoUpdateToggleBtn.setSelected(isAutoUpdate());
		autoplayToggleBtn.setSelected(isAutoplay());
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
		filmsTreeTable.getColumns().get(0).setVisible(false); // hide columnStreamUrl (important)

		// context menu for treeTableViewfilm
		filmsTreeTable.setContextMenu(menu);

		// sourcesTreeTable
		sourceColumn.setCellValueFactory(cellData -> cellData.getValue().pathProperty());
		modeColumn.setCellValueFactory(cellData -> cellData.getValue().modeProperty());
		sourcesTable.setItems(sourcesList);
	}

	// Initializing the actions
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
				filterData.clear();
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
				setFontSize(fontsizeSlider.getValue());
				if (!getCurrentTitle().isEmpty()) {
					dbController.readCache(getCurrentStreamUrl());
				}
				// ta1.setFont(Font.font("System", size));
				saveSettings();
			}
		});
        
		like.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dbController.like(getCurrentStreamUrl());
				dbController.refresh(getCurrentStreamUrl(), indexList);
				refreshTableElement();
			}
		});
        
		dislike.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dbController.dislike(getCurrentStreamUrl());
				dbController.refresh(getCurrentStreamUrl(), indexList);
				refreshTableElement();
			}
		});
        
		/**
		 * FIXME fix bug when sort by ASCENDING, wrong order
		 */
		columnFavorite.sortTypeProperty().addListener(new ChangeListener<SortType>() {
			@Override
			public void changed(ObservableValue<? extends SortType> paramObservableValue, SortType paramT1, SortType paramT2) {
				filmRoot.getChildren().clear();
				filterData.clear();
				
				if (paramT2.equals(SortType.DESCENDING)) {
					for (FilmTabelDataType film : filmsList) {
						if (film.getFavorite()) {
							filterData.add(0, film);
						} else {
							filterData.add(film);
						}
					}
				} else {
//					System.out.println("ascending");
					for (FilmTabelDataType film : filmsList) {
						if (!film.getFavorite()) {
							filterData.add(0, film);
						} else {
							filterData.add(film);
						}
					}
				}
				
				addDataUI(filterData);
			}
		});
        
		// Change-listener for treeTableViewfilm
		filmsTreeTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<?> observable, Object oldVal, Object newVal) {
				if (filmsTreeTable.getSelectionModel().getSelectedItem() == null) {
					return;
				}
				
				currentTableFilm = filmsTreeTable.getSelectionModel().getSelectedItem().getValue(); // set the current film object
				indexTable = filmsTreeTable.getSelectionModel().getSelectedIndex(); // get selected items table index
				for (FilmTabelDataType film : filmsList) {
					if (film.equals(currentTableFilm)) {
						indexList = filmsList.indexOf(film); // get selected items list index
					}
				}
				
				last = indexTable - 1;
				next = indexTable + 1;
				
				if (currentTableFilm.getCached() || dbController.searchCacheByURL(getCurrentStreamUrl())) {
					LOGGER.info("loading from cache: " + getCurrentTitle());
					dbController.readCache(getCurrentStreamUrl());
				} else {			
					omdbAPIController = new OMDbAPIController(mainWindowController, dbController, main);
					Thread omdbAPIThread = new Thread(omdbAPIController);
					omdbAPIThread.setName("OMDbAPI");
					omdbAPIThread.start();	
				}
			}
		});
	}
	
	@FXML
	private void playbtnclicked() {
		if (currentTableFilm.getStreamUrl().contains("_rootNode")) {
			LOGGER.info("rootNode found, getting last watched episode");
			currentTableFilm = dbController.getLastWatchedEpisode(currentTableFilm.getTitle());
		}
		
		if (isSupportedFormat(currentTableFilm)) {
			new Player(mainWindowController);
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
					JFXInfoAlert vlcInfoAlert = new JFXInfoAlert("Info", getBundle().getString("vlcNotInstalled"), btnStyle, main.getPrimaryStage());
					vlcInfoAlert.showAndWait();
				} else {
					try {
						new ProcessBuilder("vlc", getCurrentStreamUrl()).start();
					} catch (IOException e) {
						LOGGER.warn("An error has occurred while opening the file!", e);
					}
				}
				
			} else if (System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac OS X")) {
				try {
					Desktop.getDesktop().open(new File(getCurrentStreamUrl()));
				} catch (IOException e) {
					LOGGER.warn("An error has occurred while opening the file!", e);
				}
			} else {
				LOGGER.error(System.getProperty("os.name") + ", OS is not supported, please contact a developer! ");
			}
		}
	}
	
	@FXML
	private void openfolderbtnclicked() {
		String dest = new File(getCurrentStreamUrl()).getParentFile().getAbsolutePath();
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
				+ versionName + "\" \n" + getBundle().getString("infoText");
		JFXInfoAlert infoAlert = new JFXInfoAlert("Project HomeFlix", bodyText, btnStyle, main.getPrimaryStage());
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
	private void colorPickerAction() {
		setColor(colorPicker.getValue().toString().substring(2, 10));
		saveSettings();
		applyColor();
	}
	
	@FXML
	private void updateBtnAction() {
		updateController = new UpdateController(this, buildNumber, useBeta);
		Thread updateThread = new Thread(updateController);
		updateThread.setName("Updater");
		updateThread.start();	
	}
	
	@FXML
	private void autoUpdateToggleBtnAction(){
		autoUpdate = isAutoUpdate() ? false : true;
		saveSettings();
	}
	
	@FXML
	private void autoplayToggleBtnAction(){
		autoplay = isAutoplay() ? false : true;
		saveSettings();
	}
	
	// refresh the selected child of the root node
	private void refreshTableElement() {
		filmRoot.getChildren().get(indexTable).setValue(filmsList.get(indexList));
	}
	
	/**
	 * add data from films-list to films-table
	 */
	public void addDataUI(ObservableList<FilmTabelDataType> elementsList) {

		for (FilmTabelDataType element : elementsList) {
			
			// only if the entry contains a season and a episode it's a valid series
			if (!element.getSeason().isEmpty() && !element.getEpisode().isEmpty()) {

				// check if there is a series node to add the item		
				for (int i = 0; i < filmRoot.getChildren().size(); i++) {
					if (filmRoot.getChildren().get(i).getValue().getTitle().equals(element.getTitle())) {
						// if a root node exists, add element as child
						TreeItem<FilmTabelDataType> episodeNode = new TreeItem<>(new FilmTabelDataType(
								element.getStreamUrl(), element.getTitle(), element.getSeason(), element.getEpisode(),
								element.getFavorite(), element.getCached(), element.getImage()));
						filmRoot.getChildren().get(i).getChildren().add(episodeNode);
					} else if (filmRoot.getChildren().get(i).nextSibling() == null) {
						// if no root node exists, create one and add element as child
						TreeItem<FilmTabelDataType> seriesRootNode = new TreeItem<>(new FilmTabelDataType(
								element.getTitle() + "_rootNode", element.getTitle(), "", "", element.getFavorite(),
								false, element.getImage()));
						filmRoot.getChildren().add(seriesRootNode);
					}
				}
			} else {
				// if season and episode are empty, we can assume the object is a film
				filmRoot.getChildren().add(new TreeItem<FilmTabelDataType>(element));
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
	
	/**
	 * set the color of the GUI-Elements
	 * if usedColor is less than checkColor set text fill white, else black
	 */
	private void applyColor() {
		String menuBtnStyle;
		String btnStyleBlack = "-fx-button-type: RAISED; -fx-background-color: #" + getColor() + "; -fx-text-fill: BLACK;";
		String btnStyleWhite = "-fx-button-type: RAISED; -fx-background-color: #" + getColor() + "; -fx-text-fill: WHITE;";
		BigInteger usedColor = new BigInteger(getColor(), 16);
		BigInteger checkColor = new BigInteger("78909cff", 16);

		if (usedColor.compareTo(checkColor) == -1) {
			btnStyle = btnStyleWhite;
			menuBtnStyle = "-fx-text-fill: WHITE;";
			
			playbtn.setGraphic(play_arrow_white);
			returnBtn.setGraphic(skip_previous_white);
			forwardBtn.setGraphic(skip_next_white);
			
			menuHam.getStyleClass().clear();
			menuHam.getStyleClass().add("jfx-hamburgerW");
		} else {
			btnStyle = btnStyleBlack;
			menuBtnStyle = "-fx-text-fill: BLACK;";
			
			playbtn.setGraphic(play_arrow_black);
			returnBtn.setGraphic(skip_previous_black);
			forwardBtn.setGraphic(skip_next_black);
			
			menuHam.getStyleClass().clear();
			menuHam.getStyleClass().add("jfx-hamburgerB");
		}
		
		// boxes and TextFields
		sideMenuVBox.setStyle("-fx-background-color: #" + getColor() + ";");
		topHBox.setStyle("-fx-background-color: #" + getColor() + ";");
		searchTextField.setFocusColor(Color.valueOf(getColor()));
		
		// normal buttons
		addDirectoryBtn.setStyle(btnStyle);
		addStreamSourceBtn.setStyle(btnStyle);
		updateBtn.setStyle(btnStyle);
		playbtn.setStyle(btnStyle);
		openfolderbtn.setStyle(btnStyle);
		returnBtn.setStyle(btnStyle);
		forwardBtn.setStyle(btnStyle);
		
		// menu buttons
		settingsBtn.setStyle(menuBtnStyle);
		aboutBtn.setStyle(menuBtnStyle);
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
		autoplayToggleBtn.setText(getBundle().getString("autoplay"));
		branchLbl.setText(getBundle().getString("branchLbl"));
		columnStreamUrl.setText(getBundle().getString("columnStreamUrl"));
		columnTitle.setText(getBundle().getString("columnName"));
		columnSeason.setText(getBundle().getString("columnSeason"));
		columnEpisode.setText(getBundle().getString("columnEpisode"));
		columnFavorite.setText(getBundle().getString("columnFavorite"));
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
			props.setProperty("autoplay", String.valueOf(isAutoplay()));
			props.setProperty("size", getFontSize().toString());
			props.setProperty("local", getLocal());
			props.setProperty("ratingSortType", columnFavorite.getSortType().toString());

			OutputStream outputStream = new FileOutputStream(main.getConfigFile()); // new output-stream
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
			InputStream inputStream = new FileInputStream(main.getConfigFile());
			props.loadFromXML(inputStream); // new input-stream from .xml

			try {
				setColor(props.getProperty("color"));
			} catch (Exception e) {
				LOGGER.error("cloud not load color", e);
				setColor("00a8cc");
			}

			try {
				setFontSize(Double.parseDouble(props.getProperty("size")));
			} catch (Exception e) {
				LOGGER.error("cloud not load fontsize", e);
				setFontSize(17.0);
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
				setAutoplay(Boolean.parseBoolean(props.getProperty("autoplay")));
			} catch (Exception e) {
				LOGGER.error("cloud not load autoplay", e);
				setAutoplay(false);
			}

			try {
				setLocal(props.getProperty("local"));
			} catch (Exception e) {
				LOGGER.error("cloud not load local", e);
				setLocal(System.getProperty("user.language") + "_" + System.getProperty("user.country"));
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
	
	/**
	 * check if a film is supported by the HomeFlixPlayer or not
	 * this is the case if the mime type is mp4
	 * @param entry the film you want to check
	 * @return true if so, false if not
	 */
	private boolean isSupportedFormat(FilmTabelDataType film) {
		 String mimeType = URLConnection.guessContentTypeFromName(film.getStreamUrl());
		 return mimeType != null && (mimeType.contains("mp4") || mimeType.contains("vp6"));
	}


	// getter and setter
	public DBController getDbController() {
		return dbController;
	}

	public String getColor() {
		return color;
	}
	
	public void setColor(String input) {
		this.color = input;
	}
	
	public FilmTabelDataType getCurrentTableFilm() {
		return currentTableFilm;
	}

	public String getCurrentTitle() {
		return currentTableFilm.getTitle();
	}

	public String getCurrentStreamUrl() {
		return currentTableFilm.getStreamUrl();
	}

	public Double getFontSize() {
		return fontSize;
	}
	
	public void setFontSize(Double input) {
		this.fontSize = input;
	}

	public int getIndexTable() {
		return indexTable;
	}
	
	public int getIndexList() {
		return indexList;
	}

	public boolean isAutoUpdate() {
		return autoUpdate;
	}
	
	public void setAutoUpdate(boolean input) {
		this.autoUpdate = input;
	}

	public boolean isUseBeta() {
		return useBeta;
	}

	public void setUseBeta(boolean useBeta) {
		this.useBeta = useBeta;
	}
	
	public boolean isAutoplay() {
		return autoplay;
	}

	public void setAutoplay(boolean autoplay) {
		this.autoplay = autoplay;
	}

	public String getLocal() {
		return local;
	}
	
	public void setLocal(String input) {
		this.local = input;
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
