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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
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
import javafx.util.Duration;
import kellerkinder.HomeFlix.controller.DBController;
import kellerkinder.HomeFlix.controller.UpdateController;
import kellerkinder.HomeFlix.controller.apiQuery;
import kellerkinder.HomeFlix.datatypes.tableData;

public class MainWindowController {	
	@FXML
	private AnchorPane anpane;
	@FXML
	private AnchorPane settingsAnchor;
	@FXML 
	private AnchorPane streamingSettingsAnchor;
	@FXML
	private HBox topHBox;
	@FXML
	private VBox sideMenuVBox;
	@FXML
	private TreeTableView<tableData> treeTableViewfilm;
	@FXML
	private TableView<tableData> tableViewStreamingdata;
	@FXML
	private TextFlow textFlow;
	@FXML
	ScrollPane scrollPane;
	@FXML
	private JFXButton playbtn;
	@FXML
	private JFXButton openfolderbtn;
	@FXML
	private JFXButton returnBtn;
	@FXML
	private JFXButton forwardBtn;
    @FXML
    private JFXButton infoBtn;
    @FXML
    private JFXButton settingsBtn;
    @FXML
    private JFXButton streamingSettingsBtn;
    @FXML
    private JFXButton switchBtn;
    @FXML
    private JFXButton debugBtn;
    @FXML
    public JFXButton updateBtn;
    @FXML
    private JFXButton directoryBtn;
    @FXML
    private JFXButton streamingDirectoryBtn;
    @FXML
    private JFXHamburger menuHam;
    @FXML
    private JFXToggleButton autoUpdateToggleBtn;
    @FXML
    public JFXTextField tfPath;
    @FXML
    public JFXTextField tfStreamingPath;
    @FXML
    private JFXTextField tfsearch;
    @FXML
    public JFXColorPicker mainColor;
    @FXML
    public	ChoiceBox<String> cbLocal = new ChoiceBox<>();
    @FXML
    public JFXSlider sliderFontSize;
    @FXML
    private Label versionLabel;
    @FXML
    private Label fontsizeLabel;
    @FXML
    private Label autoUpdateLabel;
    @FXML
    private Label settingsHead1Label;
    @FXML
    private Label mainColorLabel;
    @FXML
    private Label localLabel;
    @FXML
	private ImageView image1;
    
    private ImageView imv1;
    
	@FXML
	TreeItem<tableData> root = new TreeItem<>(new tableData(1, 1, 1, 5.0, "1", "filme", "1", imv1, false));
	@FXML
	TreeTableColumn<tableData, ImageView> columnRating = new TreeTableColumn<>("Rating");
	@FXML
	TreeTableColumn<tableData, String> columnTitel = new TreeTableColumn<>("Titel");
	@FXML
	TreeTableColumn<tableData, String> columnStreamUrl = new TreeTableColumn<>("File Name");
	@FXML
	TreeTableColumn<tableData, String> columnResolution = new TreeTableColumn<>("Resolution");
	@FXML
	TreeTableColumn<tableData, Integer> columnYear = new TreeTableColumn<>("Year");
	@FXML
	TreeTableColumn<tableData, Integer> columnSeason = new TreeTableColumn<>("Season");
	@FXML
	TreeTableColumn<tableData, Integer> columnEpisode = new TreeTableColumn<>("Episode");
    
    @FXML
    private TreeItem<tableData> streamingRoot =new TreeItem<>(new tableData(1 ,1 ,1 ,1.0 ,"1" ,"filme" ,"1", imv1, false));
    @FXML
    private TableColumn<tableData, String> dataNameColumn = new TableColumn<>("Datei Name");
    @FXML
    private TableColumn<tableData, String> dataNameEndColumn = new TableColumn<>("Datei Name mit Endung");
	
	private boolean menuTrue = false;
	private boolean settingsTrue = false;
	private boolean streamingSettingsTrue = false;
	private boolean autoUpdate = false;
	private boolean useBeta = false;
    private static final Logger LOGGER = LogManager.getLogger(MainWindowController.class.getName());
	private int hashA = -647380320;
	
	private String version = "0.5.2";
	private String buildNumber = "131";
	private String versionName = "solidify cow";
	
	private String errorPlay;
	private String errorOpenStream;
	private String errorMode;
	private String errorLoad;
	private String errorSave;
	private String infoText;
	private String vlcNotInstalled;
	private String path;
	private String streamingPath;
	private String color;
	private String name;
	private String datPath;
	private String mode;
	private String ratingSortType;
	private String local;
	
	public double size;
	private int last;
	private int selected;
	private int next;
	private File selectedFolder;
	private File selectedStreamingFolder;
	private ResourceBundle bundle;

	private ObservableList<tableData> filterData = FXCollections.observableArrayList();
	private ObservableList<String> locals = FXCollections.observableArrayList("English (en_US)", "Deutsch (de_DE)");
	private ObservableList<tableData> localFilms = FXCollections.observableArrayList();
	private ObservableList<tableData> streamingFilms = FXCollections.observableArrayList();
	private ObservableList<tableData> streamingData = FXCollections.observableArrayList();
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
	Properties props = new Properties();
	
	private Main main;
	private UpdateController updateController;
	private apiQuery ApiQuery;
	DBController dbController;
	
	/**"Main" Method called in Main.java main() when starting
	 * Initialize other objects: Updater, dbController and ApiQuery
	 */
	void setMain(Main main) {
		this.main = main;
		dbController = new DBController(this.main, this);	
		ApiQuery = new apiQuery(this, dbController, this.main);
	}
	
	void init() {
		loadSettings();
		loadStreamingSettings();
		checkAutoUpdate();
		initTabel();
		initActions();
		initUI();	
	}
	
	//Initialize the tables (treeTableViewfilm and tableViewStreamingdata)
	private void initTabel() {

		//film Table 
	    columnRating.setMaxWidth(80);
	    columnTitel.setMaxWidth(260);
	    columnStreamUrl.setMaxWidth(0);
	    dataNameColumn.setPrefWidth(150);
	    dataNameEndColumn.setPrefWidth(220);
	    columnRating.setStyle("-fx-alignment: CENTER;");
		
        treeTableViewfilm.setRoot(root);
        treeTableViewfilm.setColumnResizePolicy( TreeTableView.CONSTRAINED_RESIZE_POLICY );
        treeTableViewfilm.setShowRoot(false);
        
        //write content into cell
        columnTitel.setCellValueFactory(cellData -> cellData.getValue().getValue().titleProperty());
        columnRating.setCellValueFactory(cellData -> cellData.getValue().getValue().imageProperty());
        columnStreamUrl.setCellValueFactory(cellData -> cellData.getValue().getValue().streamUrlProperty());
        columnResolution.setCellValueFactory(cellData -> cellData.getValue().getValue().resolutionProperty());
        columnYear.setCellValueFactory(cellData -> cellData.getValue().getValue().yearProperty().asObject());
        columnSeason.setCellValueFactory(cellData -> cellData.getValue().getValue().seasonProperty().asObject());
        columnEpisode.setCellValueFactory(cellData -> cellData.getValue().getValue().episodeProperty().asObject());

        //add columns to treeTableViewfilm
        treeTableViewfilm.getColumns().add(columnTitel);
        treeTableViewfilm.getColumns().add(columnRating);
        treeTableViewfilm.getColumns().add(columnStreamUrl);
        treeTableViewfilm.getColumns().add(columnResolution);
        treeTableViewfilm.getColumns().add(columnYear);
        treeTableViewfilm.getColumns().add(columnSeason);
        treeTableViewfilm.getColumns().add(columnEpisode);
        treeTableViewfilm.getColumns().get(2).setVisible(false); //hide columnStreamUrl (column with file URL, important for opening a file/stream)
	
	    //Change-listener for treeTableViewfilm
	    treeTableViewfilm.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {	
			@Override
			public void changed(ObservableValue<?> observable, Object oldVal, Object newVal){
				// last = selected; //for auto-play
				selected = treeTableViewfilm.getSelectionModel().getSelectedIndex(); //get selected item
				last = selected - 1;
				next = selected + 1;
				name = columnTitel.getCellData(selected); //get name of selected item
				datPath = columnStreamUrl.getCellData(selected); //get file path of selected item
				
				if(mode.equals("local")){
					if(localFilms.get(selected).getCached()==true){
						LOGGER.info("loading from cache: "+name);
						dbController.readCache(datPath);
					}else{
						ApiQuery.startQuery(name,datPath); // start api query
					}
				}else{
					LOGGER.info(streamingFilms.size());
					if(streamingFilms.get(selected).getCached()==true){
						LOGGER.info("loading from cache: "+name);
						dbController.readCache(datPath);
					}else{
						ApiQuery.startQuery(name,datPath); // start api query
					}
				}
			}
		});
	    
	    //context menu for treeTableViewfilm  
	    treeTableViewfilm.setContextMenu(menu);

	    //Streaming-Settings Table
	    dataNameColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
	    dataNameEndColumn.setCellValueFactory(cellData -> cellData.getValue().streamUrlProperty());
		
	    tableViewStreamingdata.getColumns().add(dataNameColumn);
	    tableViewStreamingdata.getColumns().add(dataNameEndColumn);
	    tableViewStreamingdata.setItems(streamingData); 
	}
	
	//Initializing the actions
	private void initActions(){
		
		HamburgerBackArrowBasicTransition burgerTask = new HamburgerBackArrowBasicTransition(menuHam);
		menuHam.addEventHandler(MouseEvent.MOUSE_PRESSED, (e)->{
	    	if(menuTrue == false){
				sideMenuSlideIn();
				burgerTask.setRate(1.0);
				burgerTask.play();
				menuTrue = true;
			}else{
				sideMenuSlideOut();
				burgerTask.setRate(-1.0);
				burgerTask.play();
				menuTrue = false;
			}
			if(settingsTrue == true){
				settingsAnchor.setVisible(false);
				setPath(tfPath.getText());
				saveSettings();
				settingsTrue = false;
			}
			if(streamingSettingsTrue == true){
				streamingSettingsAnchor.setVisible(false);
				streamingSettingsTrue = false;
			}
			
		});
		
        tfsearch.textProperty().addListener(new ChangeListener<String>() {
    	    @Override
    	    public void changed(ObservableValue<? extends String> observable,String oldValue, String newValue) {
            	ObservableList<tableData> helpData;
    	    	filterData.removeAll(filterData);
    	    	root.getChildren().remove(0,root.getChildren().size());
    	    	
  	    		if(mode.equals("local")){
  	            	helpData = localFilms;
  	    		}else{
  	    			helpData = streamingFilms;
  	    		}
    	    	
    	    	for(int i = 0; i < helpData.size(); i++){
    	    		if(helpData.get(i).getTitle().toLowerCase().contains(tfsearch.getText().toLowerCase())){
    	    			filterData.add(helpData.get(i));	//add data from newDaten to filteredData where title contains search input
    	    		}
    	    	}
    	    	
    	    	for(int i = 0; i < filterData.size(); i++){
    				root.getChildren().add(new TreeItem<tableData>(filterData.get(i)));	//add filtered data to root node after search
    			}
    	    	if(tfsearch.getText().hashCode()== hashA){
    	    		setColor("000000");
    	    		applyColor();
    	    	}
    	    }
    	});
        
        cbLocal.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
          @Override
		public void changed(ObservableValue<? extends Number> ov, Number value, Number new_value) {
        	  String local = cbLocal.getItems().get((int) new_value).toString();
        	  local = local.substring(local.length()-6,local.length()-1);	//reading only en_US from English (en_US)
        	  setLocal(local);
        	  setLocalUI();
        	  saveSettings();
          }
        });
        
        sliderFontSize.valueProperty().addListener(new ChangeListener<Number>() {
			 @Override
			public void changed(ObservableValue<? extends Number> ov,Number old_val, Number new_val) {
				setSize(sliderFontSize.getValue()); 
				
				if(name != null){
					dbController.readCache(datPath);
				}

//				ta1.setFont(Font.font("System", size));
				saveSettings();
			 }
        });
        
        like.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if(mode.equals("streaming")){
            		dbController.like(name,streamingFilms.get(selected).getStreamUrl());
            	}else{
				dbController.like(name,localFilms.get(selected).getStreamUrl());
            	}
				dbController.getFavStatus(name);
				try {
					dbController.refresh(name, selected);
				} catch (SQLException e) {
					LOGGER.error("(like-problem), it seems as a cat has stolen the \"like-methode\"!", e);
				}
				refreshTable();
			}
	    	});
        
        dislike.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if(mode.equals("streaming")){
            		dbController.dislike(name,streamingFilms.get(selected).getStreamUrl());
            	}else{
				dbController.dislike(name,localFilms.get(selected).getStreamUrl());
            	}
				dbController.getFavStatus(name);
				try {
					dbController.refresh(name, selected);
				} catch (SQLException e) {
					Alert alert = new Alert(AlertType.ERROR);
			    	alert.setTitle("Error");
			    	alert.setHeaderText("");
			    	alert.setContentText("There should be an error message in future (dislike problem)");
					e.printStackTrace();
				}
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
            	ObservableList<tableData> helpData;
  	    		filterData.removeAll(filterData);
//  	    	treeTableViewfilm.getSelectionModel().clearSelection(selected);
  	    		root.getChildren().remove(0,root.getChildren().size());
  	    		
  	    		if(mode.equals("local")){
  	            	helpData = localFilms;
  	    		}else{
  	    			helpData = streamingFilms;
  	    		}
              
  	    		
  	    		for(int i = 0;i<helpData.size();i++){
  	    			if(helpData.get(i).getRating()==1.0){
  	    				fav_true.add(i);
  	    			}else{
  	    				fav_false.add(i);
  	    			}
  	    		}
  	    		if(paramT2.toString().equals("DESCENDING")){
  	    			LOGGER.info("Absteigend");	//Debug, delete?
  	  	    		for(int i = 0;i<fav_true.size();i++){
  	  	    			filterData.add(helpData.get(fav_true.get(i)));
  	  	    		}
  	  	    		for(int i = 0;i<fav_false.size();i++){
  	  	    			filterData.add(helpData.get(fav_false.get(i)));
  	  	    		}
  	    		}else{
  	  	    		for(int i = 0;i<fav_false.size();i++){
  	  	    			filterData.add(helpData.get(fav_false.get(i)));
  	  	    		}
  	  	    		for(int i = 0;i<fav_true.size();i++){
  	  	    			filterData.add(helpData.get(fav_true.get(i)));
  	  	    		}
  	    		}
  	    		
  	    		LOGGER.info(filterData.size());	//Debug, delete?
    	    	for(int i = 0; i < filterData.size(); i++){
//    	    		System.out.println(filterData.get(i).getTitel()+"; "+filterData.get(i).getRating());
    				root.getChildren().add(new TreeItem<tableData>(filterData.get(i)));	//add filtered data to root node after search
    			}
            }
      });
	}
	
	// initialize UI elements
	private void initUI() {
		LOGGER.info("Mode: " + mode); // TODO debugging
		debugBtn.setDisable(true); // debugging button for tests
		debugBtn.setVisible(false);

		tfPath.setText(getPath());
		sliderFontSize.setValue(getSize());
		mainColor.setValue(Color.valueOf(getColor()));

		updateBtn.setFont(Font.font("System", 12));
		autoUpdateToggleBtn.setSelected(isAutoUpdate());
		cbLocal.setItems(locals);
		
		setLocalUI();
		applyColor();
	}
	
	@FXML
	private void playbtnclicked(){	
		if (mode.equals("streaming")) {
			if (Desktop.isDesktopSupported()) {
				new Thread(() -> {
					try {
				        Desktop.getDesktop().browse(new URI(datPath));	//open the streaming URL in browser
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
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setHeaderText("");
		        	alert.setTitle("Info");
		        	alert.setContentText(vlcNotInstalled);
		        	alert.showAndWait();
				}else{
					try {
						Runtime.getRuntime().exec(new String[] { "vlc", getPath()+"/"+ datPath});
					} catch (IOException e) {
						showErrorMsg(errorPlay,e);
					}
				}
			}else if(System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac OS X")){
					try {
						Desktop.getDesktop().open(new File(getPath()+"\\"+ datPath));
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
	private void openfolderbtnclicked(){
		try {
			Desktop.getDesktop().open(new File(getPath()));	//open path when button is clicked
		} catch (IOException e) {
			e.printStackTrace();
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
	private void infoBtnclicked(){
		Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("Info");
    	alert.setHeaderText("Project HomeFlix");
    	alert.setContentText(infoText);
    	alert.initOwner(main.getPrimaryStage());
    	alert.showAndWait();
	}
	
	@FXML
	private void settingsBtnclicked(){
		if(settingsTrue == false){
			if(streamingSettingsTrue == true){
				streamingSettingsAnchor.setVisible(false);
				streamingSettingsTrue = false;
			}
			settingsAnchor.setVisible(true);	
			settingsTrue = true;
		}else{
			settingsAnchor.setVisible(false);
			setPath(tfPath.getText());
			saveSettings();
			settingsTrue = false;
		}
	}
	
	/**
	 * TODO additional info about the "streaming.json"
	 */
	@FXML
	private void streamingSettingsBtnclicked(){
		if(streamingSettingsTrue == false){
			if(settingsTrue == true){
				settingsAnchor.setVisible(false);
				settingsTrue = false;
			}
			streamingSettingsAnchor.setVisible(true);			
			streamingSettingsTrue = true;	
			}else{
				streamingSettingsAnchor.setVisible(false);
				streamingSettingsTrue = false;
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
		root.getChildren().remove(0,root.getChildren().size());
		addDataUI();
		settingsAnchor.setVisible(false);
		streamingSettingsAnchor.setVisible(false);
		sideMenuSlideOut();		//disables side-menu
		menuTrue = false;
		settingsTrue = false;
		streamingSettingsTrue = false;
	}
	
	@FXML
	private void debugBtnclicked(){
		//for testing
	}

	
	@FXML
	private void tfPathAction(){
		setPath(tfPath.getText());
		saveSettings();
	}
	
	@FXML
	private void directoryBtnAction(){
		selectedFolder = directoryChooser.showDialog(null);  
        if(selectedFolder == null){
        	LOGGER.warn("No Directory selected");
        }else{
        	setPath(selectedFolder.getAbsolutePath());
        	saveSettings();
        	tfPath.setText(getPath());
			try {
				Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar");	//start again
				System.exit(0);	//finishes itself
			} catch (IOException e) {
				LOGGER.error("error while restarting HomeFlix", e);
			}
        }
	}
	
	@FXML
	private void mainColorAction(){
		editColor(mainColor.getValue().toString());
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
	
	@FXML
	private void tfStreamingPathAction(){
		//
	}
	
	@FXML
	private void streamingDirectoryBtnAction(){
		selectedStreamingFolder = directoryChooser.showDialog(null);  
		if(selectedStreamingFolder == null){
				LOGGER.warn("No Directory selected");
		}else{
			setStreamingPath(selectedStreamingFolder.getAbsolutePath());
			saveSettings();
			tfStreamingPath.setText(getStreamingPath());
			try {
				Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar");	//start again
				System.exit(0);	//finishes itself
			} catch (IOException e) {
				LOGGER.error("error while restarting HomeFlix", e);
			}
			}
	}
	
	private void refreshTable(){
		if(mode.equals("local")){
		root.getChildren().set(selected, new TreeItem<tableData>(localFilms.get(selected)));
		}else if(mode.equals("streaming")){
			root.getChildren().set(selected, new TreeItem<tableData>(streamingFilms.get(selected)));
		}
	}
	
	public void addDataUI(){
		if(mode.equals("local")){
			for(int i = 0; i < localFilms.size(); i++){
				root.getChildren().add(new TreeItem<tableData>(localFilms.get(i)));	//add data to root-node
			}
			columnRating.setMaxWidth(85);
		    columnTitel.setMaxWidth(290);
			treeTableViewfilm.getColumns().get(3).setVisible(false);
			treeTableViewfilm.getColumns().get(4).setVisible(false);
			treeTableViewfilm.getColumns().get(5).setVisible(false);
			treeTableViewfilm.getColumns().get(6).setVisible(false);
		}else if(mode.equals("streaming")){
			for(int i = 0; i < streamingFilms.size(); i++){
				root.getChildren().add(new TreeItem<tableData>(streamingFilms.get(i)));	//add data to root-node
			}
			columnTitel.setMaxWidth(150);
			columnResolution.setMaxWidth(65);
			columnRating.setMaxWidth(50);
			columnYear.setMaxWidth(43);
			columnSeason.setMaxWidth(42);
			columnEpisode.setMaxWidth(44);
			treeTableViewfilm.getColumns().get(3).setVisible(true);
			treeTableViewfilm.getColumns().get(4).setVisible(true);
			treeTableViewfilm.getColumns().get(5).setVisible(true);
			treeTableViewfilm.getColumns().get(6).setVisible(true);
		}
	}
	
	void loadStreamingSettings() {
		if (getStreamingPath().equals("") || getStreamingPath().equals(null)) {
			LOGGER.warn("Kein Pfad angegeben");
		} else {
			String[] entries = new File(getStreamingPath()).list();
			for (int i = 0; i < entries.length; i++) {
				if (entries[i].endsWith(".json")) {
					String titel = ohneEndung(entries[i]);
					String data = entries[i];
					streamingData.add(new tableData(1, 1, 1, 5.0, "1", titel, data, imv1, false));
				}
			}
			for (int i = 0; i < streamingData.size(); i++) {
				streamingRoot.getChildren().add(new TreeItem<tableData>(streamingData.get(i))); // adds data to root-node
			}
		}
	}
	
	// removes the ending
	private String ohneEndung(String str) {
		if (str == null)
			return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1)
			return str;
		return str.substring(0, pos);
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
		tfsearch.setFocusColor(Color.valueOf(getColor()));
		tfPath.setFocusColor(Color.valueOf(getColor()));

		if (icolor.compareTo(ccolor) == -1) {
			settingsBtn.setStyle("-fx-text-fill: WHITE;");
			streamingSettingsBtn.setStyle("-fx-text-fill: WHITE;");
			switchBtn.setStyle("-fx-text-fill: WHITE;");
			infoBtn.setStyle("-fx-text-fill: WHITE;");
			debugBtn.setStyle("-fx-text-fill: WHITE;");
			directoryBtn.setStyle(btnStyleWhite);
			streamingDirectoryBtn.setStyle(btnStyleWhite);
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
			settingsBtn.setStyle("-fx-text-fill: BLACK;");
			streamingSettingsBtn.setStyle("-fx-text-fill: BLACK;");
			switchBtn.setStyle("-fx-text-fill: BLACK;");
			infoBtn.setStyle("-fx-text-fill: BLACK;");
			debugBtn.setStyle("-fx-text-fill: BLACK;");
			directoryBtn.setStyle(btnStyleBlack);
			streamingDirectoryBtn.setStyle(btnStyleBlack);
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
	
	private void sideMenuSlideIn() {
		sideMenuVBox.setVisible(true);
		// fade in from 40% to 100% opacity in 400ms
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), sideMenuVBox);
		fadeTransition.setFromValue(0.4);
		fadeTransition.setToValue(1.0);
		// slide in in 400ms
		TranslateTransition translateTransition = new TranslateTransition(Duration.millis(400), sideMenuVBox);
		translateTransition.setFromX(-150);
		translateTransition.setToX(0);
		// in case both animations are used (add (fadeTransition, translateTransition) in the second line under this command)
		ParallelTransition parallelTransition = new ParallelTransition();
		parallelTransition.getChildren().addAll(translateTransition);// (fadeTransition, translateTransition);
		parallelTransition.play();
	}
	
	private void sideMenuSlideOut() {
		// sideMenuVBox.setVisible(false);
		// fade out from 100% to 40% opacity in 400ms
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), sideMenuVBox);
		fadeTransition.setFromValue(1.0);
		fadeTransition.setToValue(0.4);
		// slide out in 400ms
		TranslateTransition translateTransition = new TranslateTransition(Duration.millis(400), sideMenuVBox);
		translateTransition.setFromX(0);
		translateTransition.setToX(-150);
		// in case both animations are used (add (fadeTransition, translateTransition) in the second line under this command)
		ParallelTransition parallelTransition = new ParallelTransition();
		parallelTransition.getChildren().addAll(translateTransition);// (fadeTransition, translateTransition);
		parallelTransition.play();
	}
	
	void setLocalUI() {
		switch (getLocal()) {
		case "en_US":
			setBundle(ResourceBundle.getBundle("locals.HomeFlix-Local", Locale.US)); // us_English
			cbLocal.getSelectionModel().select(0);
			break;
		case "de_DE":
			setBundle(ResourceBundle.getBundle("locals.HomeFlix-Local", Locale.GERMAN)); // German
			cbLocal.getSelectionModel().select(1);
			break;
		default:
			setBundle(ResourceBundle.getBundle("locals.HomeFlix-Local", Locale.US)); // default local
			cbLocal.getSelectionModel().select(0);
			break;
		}
		infoBtn.setText(getBundle().getString("info"));
		settingsBtn.setText(getBundle().getString("settings"));
		streamingSettingsBtn.setText(getBundle().getString("streamingSettings"));
		tfPath.setPromptText(getBundle().getString("tfPath"));
		tfStreamingPath.setPromptText(getBundle().getString("tfPath"));
		tfsearch.setPromptText(getBundle().getString("tfSearch"));
		openfolderbtn.setText(getBundle().getString("openFolder"));
		updateBtn.setText(getBundle().getString("checkUpdates"));
		directoryBtn.setText(getBundle().getString("chooseFolder"));
		streamingDirectoryBtn.setText(getBundle().getString("chooseFolder"));
		settingsHead1Label.setText(getBundle().getString("settingsHead1Label"));
		mainColorLabel.setText(getBundle().getString("mainColorLabel"));
		fontsizeLabel.setText(getBundle().getString("fontsizeLabel"));
		localLabel.setText(getBundle().getString("localLabel"));
		autoUpdateLabel.setText(getBundle().getString("autoUpdateLabel"));
		versionLabel.setText(getBundle().getString("version") + " " + version + " (Build: " + buildNumber + ")");
		columnTitel.setText(getBundle().getString("columnName"));
		columnRating.setText(getBundle().getString("columnRating"));
		columnStreamUrl.setText(getBundle().getString("columnStreamUrl"));
		columnResolution.setText(getBundle().getString("columnResolution"));
		columnSeason.setText(getBundle().getString("columnSeason"));
		columnYear.setText(getBundle().getString("columnYear"));
		errorPlay = getBundle().getString("errorPlay");
		errorOpenStream = getBundle().getString("errorOpenStream");
		errorMode = getBundle().getString("errorMode");
		errorLoad = getBundle().getString("errorLoad");
		errorSave = getBundle().getString("errorSave");
		infoText = getBundle().getString("version") + " " + version + " (Build: " + buildNumber + ") " + versionName + getBundle().getString("infoText");
		vlcNotInstalled = getBundle().getString("vlcNotInstalled");
	}
	
	public void showErrorMsg(String msg, IOException exception) {
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
			props.setProperty("path", getPath()); // writes path into property
			props.setProperty("color", getColor());
			props.setProperty("autoUpdate", String.valueOf(isAutoUpdate()));
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
				setPath(props.getProperty("path")); // read path from property
			} catch (Exception e) {
				LOGGER.error("cloud not load path", e);
				setPath("");
			}

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

	public void setPath(String input) {
		this.path = input;
	}

	public String getPath() {
		return path;
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

	public ObservableList<tableData> getLocalFilms() {
		return localFilms;
	}

	public void setLocalFilms(ObservableList<tableData> localFilms) {
		this.localFilms = localFilms;
	}

	public ObservableList<tableData> getStreamingFilms() {
		return streamingFilms;
	}

	public void setStreamingFilms(ObservableList<tableData> streamingFilms) {
		this.streamingFilms = streamingFilms;
	}

	public ObservableList<tableData> getStreamingData() {
		return streamingData;
	}

	public void setStreamingData(ObservableList<tableData> streamingData) {
		this.streamingData = streamingData;
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

	public void setTextFlow(TextFlow textFlow) {
		this.textFlow = textFlow;
	}

	public ImageView getImage1() {
		return image1;
	}

	public void setImage1(ImageView image1) {
		this.image1 = image1;
	}

	public JFXButton getUpdateBtn() {
		return updateBtn;
	}

	public void setUpdateBtn(JFXButton updateBtn) {
		this.updateBtn = updateBtn;
	}
}
