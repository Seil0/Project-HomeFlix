/**
 * Project-HomeFlix
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cemu_UI.uiElements.JFXInfoDialog;
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
import kellerkinder.HomeFlix.controller.UpdateController;
import kellerkinder.HomeFlix.controller.apiQuery;
import kellerkinder.HomeFlix.datatypes.SourceDataType;
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
	private TreeTableView<FilmTabelDataType> treeTableViewfilm;
	
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
    private JFXButton switchBtn;
    
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
	private ImageView image1;
    
    private ImageView imv1;
    
	@FXML
	private TreeItem<FilmTabelDataType> filmRoot = new TreeItem<>(new FilmTabelDataType(1, 1, 5.0, "filme", "1", imv1, false));
	@FXML
	TreeTableColumn<FilmTabelDataType, ImageView> columnRating = new TreeTableColumn<>("Rating");
	@FXML
	TreeTableColumn<FilmTabelDataType, String> columnTitle = new TreeTableColumn<>("Title");
	@FXML
	TreeTableColumn<FilmTabelDataType, String> columnStreamUrl = new TreeTableColumn<>("File Name");
	@FXML
	TreeTableColumn<FilmTabelDataType, Integer> columnSeason = new TreeTableColumn<>("Season");
	@FXML
	TreeTableColumn<FilmTabelDataType, Integer> columnEpisode = new TreeTableColumn<>("Episode");
    
    @FXML
    private TreeItem<SourceDataType> streamingRoot =new TreeItem<>(new SourceDataType("", ""));
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
	
	private String version = "0.5.2";
	private String buildNumber = "131";
	private String versionName = "solidify cow";
	private String dialogBtnStyle;
	
	// text strings
	private String errorPlay;
	private String errorOpenStream;
	private String errorMode;
	private String errorLoad;
	private String errorSave;
	private String infoText;
	private String vlcNotInstalled;
	private String streamingPath;
	private String color;
	private String title;
	private String streamUrl;
	private String mode;
	private String ratingSortType;
	private String local;
	
	public double size;
	private int last;
	private int selected;
	private int next;
	private ResourceBundle bundle;

	private ObservableList<FilmTabelDataType> filterData = FXCollections.observableArrayList();
	private ObservableList<String> languages = FXCollections.observableArrayList("English (en_US)", "Deutsch (de_DE)");
	private ObservableList<String> branches = FXCollections.observableArrayList("stable", "beta");
	private ObservableList<FilmTabelDataType> localFilms = FXCollections.observableArrayList();
	private ObservableList<FilmTabelDataType> streamingFilms = FXCollections.observableArrayList();
	private ObservableList<SourceDataType> sourcesList = FXCollections.observableArrayList();
	private ImageView skip_previous_white = new ImageView(new Image("icons/ic_skip_previous_white_18dp_1x.png"));
	private ImageView skip_previous_black = new ImageView(new Image("icons/ic_skip_previous_black_18dp_1x.png"));
	private ImageView skip_next_white = new ImageView(new Image("icons/ic_skip_next_white_18dp_1x.png"));
	private ImageView skip_next_black = new ImageView(new Image("icons/ic_skip_next_black_18dp_1x.png"));
	private ImageView play_arrow_white = new ImageView(new Image("icons/ic_play_arrow_white_18dp_1x.png"));
	private ImageView play_arrow_black = new ImageView(new Image("icons/ic_play_arrow_black_18dp_1x.png"));
	private DirectoryChooser directoryChooser = new DirectoryChooser();
    private MenuItem like = new MenuItem("like");
    private MenuItem dislike = new MenuItem("dislike");	//TODO one option (like or dislike)
	private ContextMenu menu = new ContextMenu(like, dislike);
	private Properties props = new Properties();
	
	private Main main;
	private UpdateController updateController;
	private apiQuery ApiQuery;
	DBController dbController;
	
	/**
	 * "Main" Method called in Main.java main() when starting
	 * Initialize other objects: Updater, dbController and ApiQuery
	 */
	void setMain(Main main) {
		this.main = main;
		dbController = new DBController(this.main, this);	
		ApiQuery = new apiQuery(this, dbController, this.main);
	}
	
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
		columnRating.setStyle("-fx-alignment: CENTER;");

		treeTableViewfilm.setRoot(filmRoot);
		treeTableViewfilm.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
		treeTableViewfilm.setShowRoot(false);

		// write content into cell
		columnTitle.setCellValueFactory(cellData -> cellData.getValue().getValue().titleProperty());
		columnRating.setCellValueFactory(cellData -> cellData.getValue().getValue().imageProperty());
		columnStreamUrl.setCellValueFactory(cellData -> cellData.getValue().getValue().streamUrlProperty());
		columnSeason.setCellValueFactory(cellData -> cellData.getValue().getValue().seasonProperty().asObject());
		columnEpisode.setCellValueFactory(cellData -> cellData.getValue().getValue().episodeProperty().asObject());

		// add columns to treeTableViewfilm
        treeTableViewfilm.getColumns().add(columnTitle);
        treeTableViewfilm.getColumns().add(columnRating);
        treeTableViewfilm.getColumns().add(columnStreamUrl);
        treeTableViewfilm.getColumns().add(columnSeason);
        treeTableViewfilm.getColumns().add(columnEpisode);
        treeTableViewfilm.getColumns().get(2).setVisible(false); //hide columnStreamUrl (column with file URL, important for opening a file/stream)
	    
	    // context menu for treeTableViewfilm  
	    treeTableViewfilm.setContextMenu(menu);
	    
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

				if (mode.equals("local")) {
					helpData = localFilms;
				} else {
					helpData = streamingFilms;
				}

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
				dbController.refresh(streamUrl, selected);
				refreshTable();
			}
		});
        
		dislike.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dbController.dislike(streamUrl);
				dbController.refresh(streamUrl, selected);
				refreshTable();
			}
		});
        
		/**
		 * FIXME fix bug when sort by ASCENDING, wrong order
		 */
		columnRating.sortTypeProperty().addListener(new ChangeListener<SortType>() {
			@Override
			public void changed(ObservableValue<? extends SortType> paramObservableValue, SortType paramT1, SortType paramT2) {
				LOGGER.info("NAME Clicked -- sortType = " + paramT1 + ", SortType=" + paramT2);
				ArrayList<Integer> fav_true = new ArrayList<Integer>();
				ArrayList<Integer> fav_false = new ArrayList<Integer>();
				ObservableList<FilmTabelDataType> helpData;
				filterData.removeAll(filterData);
//				treeTableViewfilm.getSelectionModel().clearSelection(selected);
				filmRoot.getChildren().removeAll(filmRoot.getChildren());

				if (mode.equals("local")) {
					helpData = localFilms;
				} else {
					helpData = streamingFilms;
				}

				for (int i = 0; i < helpData.size(); i++) {
					if (helpData.get(i).getRating() == 1.0) {
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
		treeTableViewfilm.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<?> observable, Object oldVal, Object newVal) {
				// last = selected; //for auto-play
				selected = treeTableViewfilm.getSelectionModel().getSelectedIndex(); // get selected item
				last = selected - 1;
				next = selected + 1;
				title = columnTitle.getCellData(selected); // get name of selected item
				streamUrl = columnStreamUrl.getCellData(selected); // get file path of selected item

				if (mode.equals("local")) {
					if (localFilms.get(selected).getCached() == true) {
						LOGGER.info("loading from cache: " + title);
						dbController.readCache(streamUrl);
					} else {
						ApiQuery.startQuery(title, streamUrl); // start api query
					}
				} else {
					if (streamingFilms.get(selected).getCached() == true) {
						LOGGER.info("loading from cache: " + title);
						dbController.readCache(streamUrl);
					} else {
						ApiQuery.startQuery(title, streamUrl); // start api query
					}
				}
			}
		});
	}
	
	// initialize UI elements
	private void initUI() {
		LOGGER.info("Mode: " + mode); // TODO debugging
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
	private void playbtnclicked(){	
		if (mode.equals("streaming")) {
			if (Desktop.isDesktopSupported()) {
				new Thread(() -> {
					try {
				        Desktop.getDesktop().browse(new URI(streamUrl));	//open the streaming URL in browser
				    } catch (IOException | URISyntaxException e) {
				    	e.printStackTrace();
				        showErrorMsg(errorOpenStream, (IOException) e);
				    }
				}).start();	
			} else {
				LOGGER.info("Desktop not supported");
			}
		}else if (mode.equals("local")) {
			if(System.getProperty("os.name").contains("Linux")){
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
				if(output.contains("which: no vlc")||output == ""){
					JFXInfoDialog vlcInfoDialog = new JFXInfoDialog("Info", vlcNotInstalled, dialogBtnStyle, 350, 200, main.getPane());
					vlcInfoDialog.show();
				}else{
					try {
						Runtime.getRuntime().exec(new String[] { "vlc", streamUrl}); // TODO switch to ProcessBuilder
					} catch (IOException e) {
						showErrorMsg(errorPlay,e);
					}
				}
			}else if(System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac OS X")){
					try {
						Desktop.getDesktop().open(new File(streamUrl));
					} catch (IOException e) {
						showErrorMsg(errorPlay,e);
					}
			} else {
				LOGGER.error(System.getProperty("os.name") + ", OS is not supported, please contact a developer! ");
			}	
		} else {
			IOException e = new IOException("error");
			showErrorMsg(errorMode, e);
		}
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
		treeTableViewfilm.getSelectionModel().select(last);
	}
	
	@FXML
	private void forwardBtnclicked(){
		treeTableViewfilm.getSelectionModel().select(next);
	}
	
	@FXML
	private void aboutBtnAction() {
		String bodyText = "cemu_UI by @Seil0 \nVersion: " + version + " (Build: " + buildNumber + ")  \""
				+ versionName + "\" \n" + infoText;
		JFXInfoDialog aboutDialog = new JFXInfoDialog("Project HomeFlix", bodyText, dialogBtnStyle, 350, 200, main.getPane());
		aboutDialog.show();
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
	private void switchBtnclicked(){
		if(mode.equals("local")){	//switch to streaming mode
			setMode("streaming");
			switchBtn.setText("local");
		}else if(mode.equals("streaming")){	//switch to local mode
			setMode("local");
			switchBtn.setText("streaming");
		}
		saveSettings();
		filmRoot.getChildren().removeAll(filmRoot.getChildren());
		addDataUI();
		settingsScrollPane.setVisible(false);
		sideMenuSlideOut();		//disables side-menu
		menuTrue = false;
		settingsTrue = false;
	}
	
	@FXML
	private void debugBtnclicked(){
		//for testing
	}
	
	@FXML
	private void addDirectoryBtnAction(){
		File selectedFolder = directoryChooser.showDialog(null);
		if (selectedFolder != null && selectedFolder.exists()) {
			addSource(selectedFolder.getPath(), "local");
			dbController.refreshDataBase();
		} else {
			LOGGER.error("The selected folder dosen't exist!");
		}
	}
	
	@FXML
	private void addStreamSourceBtnAction(){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
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
		if (mode.equals("local")) {
			filmRoot.getChildren().get(selected).setValue(localFilms.get(selected));
		} else {
			filmRoot.getChildren().get(selected).setValue(streamingFilms.get(selected));
		}
	}
	
	// TODO rework
	public void addDataUI() {

		if (mode.equals("local")) {
			for (int i = 0; i < localFilms.size(); i++) {
				filmRoot.getChildren().add(new TreeItem<FilmTabelDataType>(localFilms.get(i))); // add data to root-node
			}
			columnRating.setMaxWidth(85);
			columnTitle.setMaxWidth(290);
			treeTableViewfilm.getColumns().get(3).setVisible(false);
			treeTableViewfilm.getColumns().get(4).setVisible(false);
		} else {
			for (int i = 0; i < streamingFilms.size(); i++) {
				filmRoot.getChildren().add(new TreeItem<FilmTabelDataType>(streamingFilms.get(i))); // add data to root-node
			}
			columnTitle.setMaxWidth(215);
			columnRating.setMaxWidth(60);
			columnSeason.setMaxWidth(55);
			columnEpisode.setMaxWidth(64);
			treeTableViewfilm.getColumns().get(3).setVisible(true);
			treeTableViewfilm.getColumns().get(4).setVisible(true);
		}
	}
	
	// add a source to the sources table on the settings pane
	public void addSourceToTable(String path, String mode) {
		sourcesList.add(new SourceDataType(path, mode));
		streamingRoot.getChildren().add(new TreeItem<SourceDataType>(sourcesList.get(sourcesList.size() - 1))); // adds data to root-node
	}
	
	// add a source to the newsources list
	public void addSource(String path, String mode) {
		JsonObject source = null;
		JsonArray newsources = null;

		try {
			// read old array
			newsources = Json.parse(new FileReader(main.getDirectory() + "/sources.json")).asArray();

			// add new source
			Writer writer = new FileWriter(main.getDirectory() + "/sources.json");
			source = Json.object().add("path", path).add("mode", mode);
			newsources.add(source);
			newsources.writeTo(writer);
			writer.close();
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}
	
	//set color of UI-Elements
	private void applyColor() {
		String style = "-fx-background-color: #" + getColor() + ";";
		String btnStyleBlack = "-fx-button-type: RAISED; -fx-background-color: #" + getColor() + "; -fx-text-fill: BLACK;";
		String btnStyleWhite = "-fx-button-type: RAISED; -fx-background-color: #" + getColor() + "; -fx-text-fill: WHITE;";
		BigInteger icolor = new BigInteger(getColor(), 16);
		BigInteger ccolor = new BigInteger("78909cff", 16);

		sideMenuVBox.setStyle(style);
		topHBox.setStyle(style);
		searchTextField.setFocusColor(Color.valueOf(getColor()));

		if (icolor.compareTo(ccolor) == -1) {
			dialogBtnStyle = btnStyleWhite;
			settingsBtn.setStyle("-fx-text-fill: WHITE;");
			switchBtn.setStyle("-fx-text-fill: WHITE;");
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
			switchBtn.setStyle("-fx-text-fill: BLACK;");
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

		if (mode.equals("local")) {
			switchBtn.setText("streaming");
		} else if (mode.equals("streaming")) {
			switchBtn.setText("local");
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
		columnTitle.setText(getBundle().getString("columnName"));
		columnRating.setText(getBundle().getString("columnRating"));
		columnStreamUrl.setText(getBundle().getString("columnStreamUrl"));
		columnSeason.setText(getBundle().getString("columnSeason"));
		errorPlay = getBundle().getString("errorPlay");
		errorOpenStream = getBundle().getString("errorOpenStream");
		errorMode = getBundle().getString("errorMode");
		errorLoad = getBundle().getString("errorLoad");
		errorSave = getBundle().getString("errorSave");
		infoText = getBundle().getString("infoText");
		vlcNotInstalled = getBundle().getString("vlcNotInstalled");
	}
	
	// TODO rework to material design
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
	
	// save settings
	public void saveSettings() {
		LOGGER.info("saving settings ...");
		try {
			props.setProperty("color", getColor());
			props.setProperty("autoUpdate", String.valueOf(isAutoUpdate()));
			props.setProperty("useBeta", String.valueOf(isUseBeta()));
			props.setProperty("size", getSize().toString());
			props.setProperty("local", getLocal());
			props.setProperty("streamingPath", getStreamingPath());
			props.setProperty("mode", getMode());
			props.setProperty("ratingSortType", columnRating.getSortType().toString());

			OutputStream outputStream = new FileOutputStream(main.getConfigFile()); // new output-stream
			props.storeToXML(outputStream, "Project HomeFlix settings"); // writes new .xml
			outputStream.close();
		} catch (IOException e) {
			LOGGER.error(errorLoad, e);
		}
	}
	
	// load settings
	public void loadSettings() {
		LOGGER.info("loading settings ...");
		
		try {
			InputStream inputStream = new FileInputStream(main.getConfigFile());
			props.loadFromXML(inputStream); // new input-stream from .xml

			try {
				setStreamingPath(props.getProperty("streamingPath"));
			} catch (Exception e) {
				LOGGER.error("cloud not load streamingPath", e);
				setStreamingPath("");
			}

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

			try {
				switch (props.getProperty("mode")) {
				case "local":
					setMode("local");
					break;
				case "streaming":
					setMode("streaming");
					break;
				default:
					setMode("local");
					break;
				}
			} catch (Exception e) {
				setMode("local");
				LOGGER.error("cloud not load mode", e);
			}

			inputStream.close();
		} catch (IOException e) {
			LOGGER.error(errorSave, e);
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
	public void setColor(String input) {
		this.color = input;
	}

	public String getColor() {
		return color;
	}

	public void setStreamingPath(String input) {
		this.streamingPath = input;
	}

	public String getStreamingPath() {
		return streamingPath;
	}

	public void setSize(Double input) {
		this.size = input;
	}

	public Double getSize() {
		return size;
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

	public void setMode(String input) {
		this.mode = input;
	}

	public String getMode() {
		return mode;
	}

	public ObservableList<FilmTabelDataType> getLocalFilms() {
		return localFilms;
	}

	public void setLocalFilms(ObservableList<FilmTabelDataType> localFilms) {
		this.localFilms = localFilms;
	}

	public ObservableList<FilmTabelDataType> getStreamingFilms() {
		return streamingFilms;
	}

	public void setStreamingFilms(ObservableList<FilmTabelDataType> streamingFilms) {
		this.streamingFilms = streamingFilms;
	}

	public ObservableList<SourceDataType> getSourcesList() {
		return sourcesList;
	}

	public void setSourcesList(ObservableList<SourceDataType> sourcesList) {
		this.sourcesList = sourcesList;
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

	public TextFlow getTextFlow() {
		return textFlow;
	}

	public ImageView getImage1() {
		return image1;
	}

	public JFXButton getUpdateBtn() {
		return updateBtn;
	}

	public TreeItem<FilmTabelDataType> getFilmRoot() {
		return filmRoot;
	}

	public TreeItem<SourceDataType> getStreamingRoot() {
		return streamingRoot;
	}
}
