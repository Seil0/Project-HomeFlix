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
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

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
	TextFlow textFlow;
	@FXML
	ScrollPane scrollPane;
	@FXML
	private JFXButton menubtn;	//TODO switch to hamburger menu
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
    private JFXToggleButton autoupdateBtn;
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
    private Label versionlbl;
    @FXML
    private Label sizelbl;
    @FXML
    private Label aulbl;
    @FXML 
    ImageView image1;
    private ImageView imv1;
    
    @FXML
    TreeItem<tableData> root = new TreeItem<>(new tableData(1, 1, 1, 5.0,"1", "filme","1", imv1, false));
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
	
	private boolean menutrue = false;	//saves the position of menuBtn (opened or closed)
	private boolean settingstrue = false;
	private boolean streamingSettingsTrue = false;
	private boolean autoUpdate = false;
	static boolean firststart = false;
	private int hashA = -2055934614;
	private String version = "0.5.1";
	private String buildNumber = "127";
	private String versionName = "plasma cow";
	private File dirWin = new File(System.getProperty("user.home") + "/Documents/HomeFlix");
	private File dirLinux = new File(System.getProperty("user.home") + "/HomeFlix");
	private File fileWin = new File(dirWin + "/config.xml");
	private File fileLinux = new File(dirLinux + "/config.xml");	
	
	String errorUpdateD;
	String errorUpdateV;
	String noFilmFound;
	private String errorPlay;
	private String errorOpenStream;
	private String errorMode;
	private String errorLoad;
	private String errorSave;
	private String infoText;
	private String linuxBugText;
	private String vlcNotInstalled;
	private String currentWorkingDirectory;
	private String path;
	private String streamingPath;
	private String color;
	private String name;
	private String datPath;
	private String mode;
	@SuppressWarnings("unused")
	private String ratingSortType;
	private String local;
	String title;
	String year;
	String rating;
	String publishedOn;
	String duration;
	String genre;
	String director;
	String writer;
	String actors;
	String plot;
	String language;
	String country;
	String awards;
	String metascore;
	String imdbRating;
	String type;	
	double size;
	private int last;
	private int selected;
	private int next;
	private File selectedFolder;
	private File selectedStreamingFolder;
	ResourceBundle bundle;

	private ObservableList<tableData> filterData = FXCollections.observableArrayList();
	private ObservableList<String> locals = FXCollections.observableArrayList("English (en_US)", "Deutsch (de_DE)");
	ObservableList<tableData> localFilms = FXCollections.observableArrayList();
	ObservableList<tableData> streamingFilms = FXCollections.observableArrayList();
	ObservableList<tableData> streamingData = FXCollections.observableArrayList();
	private ImageView menu_icon_black = new ImageView(new Image("recources/icons/menu_icon_black.png"));
	private ImageView menu_icon_white = new ImageView(new Image("recources/icons/menu_icon_white.png"));
	private ImageView skip_previous_white = new ImageView(new Image("recources/icons/ic_skip_previous_white_18dp_1x.png"));
	private ImageView skip_previous_black = new ImageView(new Image("recources/icons/ic_skip_previous_black_18dp_1x.png"));
	private ImageView skip_next_white = new ImageView(new Image("recources/icons/ic_skip_next_white_18dp_1x.png"));
	private ImageView skip_next_black = new ImageView(new Image("recources/icons/ic_skip_next_black_18dp_1x.png"));
	private ImageView play_arrow_white = new ImageView(new Image("recources/icons/ic_play_arrow_white_18dp_1x.png"));
	private ImageView play_arrow_black = new ImageView(new Image("recources/icons/ic_play_arrow_black_18dp_1x.png"));
	private DirectoryChooser directoryChooser = new DirectoryChooser();
    private MenuItem like = new MenuItem("like");
    private MenuItem dislike = new MenuItem("dislike");	//TODO one option (like or dislike)
	private ContextMenu menu = new ContextMenu(like, dislike);
	Properties props = new Properties();
	
	private Main main;
	private updater Updater;
	private apiQuery ApiQuery;
	DBController dbController;
	
	/**
	 * TODO change value of Text-color change
	 */
	@FXML
	private void menubtnclicked(){
		if(menutrue == false){
			sideMenuSlideIn();
			menutrue = true;
		}else{
			sideMenuSlideOut();
			menutrue = false;
		}
		if(settingstrue == true){
			settingsAnchor.setVisible(false);
			setPath(tfPath.getText());
			saveSettings();
			settingstrue = false;
		}
		if(streamingSettingsTrue == true){
			streamingSettingsAnchor.setVisible(false);
			streamingSettingsTrue = false;
		}
	}
	
	@FXML
	private void playbtnclicked(){
		System.out.println(System.getProperty("os.name"));
		if(System.getProperty("os.name").contains("Linux")){
			System.out.println("This is "+System.getProperty("os.name"));
			String line;
			String output = "";
			Process p;
			try {
				p = Runtime.getRuntime().exec("which vlc");
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				while ((line = input.readLine()) != null) {
				output = line;
				}
				System.out.println(output);
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
			}else if(datPath.contains(" ")){
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setHeaderText("");
	        	alert.setTitle("Info");
	        	alert.setContentText(linuxBugText);
	        	alert.initOwner(main.primaryStage);
	        	alert.showAndWait();
			}else{
				try {
					Runtime.getRuntime().exec("vlc "+getPath()+"/"+ datPath);
				} catch (IOException e) {
					showErrorMsg(errorPlay,e);
				}
			}
		}else if(System.getProperty("os.name").contains("Windows") || System.getProperty("os.name").contains("Mac OS X")){
			System.out.println("This is "+System.getProperty("os.name"));
			if(mode.equals("local")){
				try {
					Desktop.getDesktop().open(new File(getPath()+"\\"+ datPath));
				} catch (IOException e) {
					showErrorMsg(errorPlay,e);
				}
			}else if(mode.equals("streaming")){
				try {
					Desktop.getDesktop().browse(new URI(datPath));	//open the streaming URL in browser (TODO other option?)
				} catch (URISyntaxException | IOException e) {
					showErrorMsg(errorOpenStream, (IOException) e);
				}
			}else{
				IOException e = new IOException("error");
				showErrorMsg(errorMode, e);
				
			}
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
    	alert.initOwner(main.primaryStage);
    	alert.showAndWait();
	}
	
	@FXML
	private void settingsBtnclicked(){
		if(settingstrue == false){
			if(streamingSettingsTrue == true){
				streamingSettingsAnchor.setVisible(false);
				streamingSettingsTrue = false;
			}
			settingsAnchor.setVisible(true);	
			settingstrue = true;
		}else{
			settingsAnchor.setVisible(false);
			setPath(tfPath.getText());
			saveSettings();
			settingstrue = false;
		}
	}
	
	/**
	 * TODO additional info about the "streaming.json"
	 */
	@FXML
	private void streamingSettingsBtnclicked(){
		if(streamingSettingsTrue == false){
			if(settingstrue == true){
				settingsAnchor.setVisible(false);
				settingstrue = false;
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
		menutrue = false;
		settingstrue = false;
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
            System.out.println("No Directory selected");
        }else{
        	setPath(selectedFolder.getAbsolutePath());
        	saveSettings();
        	tfPath.setText(getPath());
			try {
				Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar");	//start again
				System.exit(0);	//finishes itself
			} catch (IOException e) {
				System.out.println("es ist ein Fehler aufgetreten");
				e.printStackTrace();
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
		Thread updateThread = new Thread(Updater);
		updateThread.setName("Updater");
		updateThread.start();	
	}
	
	@FXML
	private void autoupdateBtnAction(){
		if(autoUpdate){
    		setAutoUpdate(false);
    	}else{
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
				System.out.println("No Directory selected");
		}else{
			setStreamingPath(selectedStreamingFolder.getAbsolutePath());
			saveSettings();
			tfStreamingPath.setText(getStreamingPath());
			try {
				Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar");	//start again
				System.exit(0);	//finishes itself
			} catch (IOException e) {
				System.out.println("es ist ein Fehler aufgetreten");
				e.printStackTrace();
			}
			}
	}
	
	
	/**"Main" Method called in Main.java main() when starting
	 * Initialize other objects: Updater, dbController and ApiQuery
	 */
	void setMain(Main main) {
		this.main = main;
		Updater = new updater(this, buildNumber);
		dbController = new DBController(this, this.main);	
		ApiQuery = new apiQuery(this, dbController, this.main);
	}
	
	//Initialize the tables (treeTableViewfilm and tableViewStreamingdata)
	@SuppressWarnings({ "unchecked"})	//TODO
	void initTabel(){

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

        treeTableViewfilm.getColumns().addAll(columnTitel, columnRating, columnStreamUrl, columnResolution, columnYear, columnSeason, columnEpisode);
	    treeTableViewfilm.getColumns().get(2).setVisible(false); //hide columnStreamUrl (column with file path important for the player)
	
	    //Change-listener for TreeTable
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
						System.out.println("loading from cache: "+name);
						dbController.readCache(datPath);
					}else{
						ApiQuery.startQuery(name,datPath); // start api query
					}
				}else{
					System.out.println(streamingFilms.size());
					if(streamingFilms.get(selected).getCached()==true){
						System.out.println("loading from cache: "+name);
						dbController.readCache(datPath);
					}else{
						ApiQuery.startQuery(name,datPath); // start api query
					}
				}
			}
		});
	    
	    //context menu for treetableview  
	    treeTableViewfilm.setContextMenu(menu);

	    //Streaming-Settings Table
	    dataNameColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
	    dataNameEndColumn.setCellValueFactory(cellData -> cellData.getValue().streamUrlProperty());
		
	    tableViewStreamingdata.getColumns().addAll(dataNameColumn, dataNameEndColumn);
	    tableViewStreamingdata.setItems(streamingData); 
	}
	
	//Initializing the actions
	void initActions(){
		
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
    	    	if(tfsearch.getText().hashCode() == hashA){
    	    		setColor("000000");
    	    		applyColor();
    	    	}
    	    }
    	});
        
        cbLocal.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
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
					Alert alert = new Alert(AlertType.ERROR);
			    	alert.setTitle("Error");
			    	alert.setHeaderText("");
			    	alert.setContentText("There should be an error message in the future (like problem)\nIt seems as a cat has stolen the like-methode");
					e.printStackTrace();
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
			    	alert.setContentText("There should be an error message in the future (dislike problem)");
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
            	System.out.println("NAME Clicked -- sortType = " + paramT1 + ", SortType=" + paramT2);
            	ArrayList<Integer> fav_true = new ArrayList<Integer>();
            	ArrayList<Integer> fav_false = new ArrayList<Integer>();
            	ObservableList<tableData> helpData;
  	    		filterData.removeAll(filterData);
//  	    		treeTableViewfilm.getSelectionModel().clearSelection(selected);
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
  	    			System.out.println("Absteigend");
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
  	    		
  	    		System.out.println(filterData.size());
    	    	for(int i = 0; i < filterData.size(); i++){
//    	    		System.out.println(filterData.get(i).getTitel()+"; "+filterData.get(i).getRating());
    				root.getChildren().add(new TreeItem<tableData>(filterData.get(i)));	//add filtered data to root node after search
    			}
            }
      });
	}
	
	//initialize UI elements
	void initUI(){
		System.out.println("Mode: "+mode);	//TODO debugging
		debugBtn.setDisable(true); 	//debugging button for tests
		debugBtn.setVisible(false);
		
        tfPath.setText(getPath());
        sliderFontSize.setValue(getSize());
		mainColor.setValue(Color.valueOf(getColor()));
		
        updateBtn.setFont(Font.font("System", 12));
        cbLocal.setItems(locals);
        
        if(autoUpdate){
    		autoupdateBtn.setSelected(true);
    		try {
    			Thread updateThread = new Thread(Updater);
    			updateThread.setName("Updater");
    			updateThread.start();
    			updateThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}else{
    		autoupdateBtn.setSelected(false);
    	}
	}
	
	private void refreshTable(){
		if(mode.equals("local")){
		root.getChildren().set(selected, new TreeItem<tableData>(localFilms.get(selected)));
		}else if(mode.equals("streaming")){
			root.getChildren().set(selected, new TreeItem<tableData>(streamingFilms.get(selected)));
		}
	}
	
	void addDataUI(){
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
	
	void loadStreamingSettings(){
		if(getStreamingPath().equals("")||getStreamingPath().equals(null)){
			System.out.println("Kein Pfad angegeben");	//if path equals "" or null
		}else{
		String[] entries = new File(getStreamingPath()).list();
			for(int i = 0; i < entries.length; i++){
				if(entries[i].endsWith(".json")){
					String titel = ohneEndung(entries[i]);
					String data = entries[i];
					streamingData.add(new tableData(1,1,1,5.0,"1",titel ,data, imv1, false));
				}
			}
			for(int i = 0; i < streamingData.size(); i++){
				streamingRoot.getChildren().add( new TreeItem<tableData>(streamingData.get(i)));	//adds data to root-node
			}
		}	
	}
	//removes the ending
	private String ohneEndung (String str) {
		if (str == null) return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1) return str;
		return str.substring(0, pos);
	}
	
	//set color of UI-Elements
	void applyColor(){
		String style = "-fx-background-color: #"+getColor()+";";
		String btnStyleBlack = "-fx-button-type: RAISED; -fx-background-color: #"+getColor()+"; -fx-text-fill: BLACK;";
		String btnStyleWhite = "-fx-button-type: RAISED; -fx-background-color: #"+getColor()+"; -fx-text-fill: WHITE;";
		BigInteger icolor = new BigInteger(getColor(),16);
		BigInteger ccolor = new BigInteger("78909cff",16);
		
		sideMenuVBox.setStyle(style);
		topHBox.setStyle(style);
		tfsearch.setFocusColor(Color.valueOf(getColor()));
		tfPath.setFocusColor(Color.valueOf(getColor()));
		
		if(icolor.compareTo(ccolor) == -1){
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
			menubtn.setGraphic(menu_icon_white);
		}else{
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
			menubtn.setGraphic(menu_icon_black);
		}
		
		if(mode.equals("local")){
			switchBtn.setText("streaming");
		}else if(mode.equals("streaming")){
			switchBtn.setText("local");
		}
	}
	
	private void sideMenuSlideIn(){
		sideMenuVBox.setVisible(true);
		//fade in from 40% to 100% opacity in 400ms
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), sideMenuVBox);
		fadeTransition.setFromValue(0.4);
		fadeTransition.setToValue(1.0);
		//slide in in 400ms
		TranslateTransition translateTransition = new TranslateTransition(Duration.millis(400), sideMenuVBox);
		translateTransition.setFromX(-150);
		translateTransition.setToX(0);
		//in case both animations are used (add (fadeTransition, translateTransition) in the second line under this command)    
		ParallelTransition parallelTransition = new ParallelTransition();
		parallelTransition.getChildren().addAll(translateTransition);//(fadeTransition, translateTransition);
		parallelTransition.play();
	}
	
	private void sideMenuSlideOut(){
//		sideMenuVBox.setVisible(false);
		//fade out from 100% to 40% opacity in 400ms
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), sideMenuVBox);
		fadeTransition.setFromValue(1.0);
		fadeTransition.setToValue(0.4);
		//slide out in 400ms
		TranslateTransition translateTransition = new TranslateTransition(Duration.millis(400), sideMenuVBox);
		translateTransition.setFromX(0);
		translateTransition.setToX(-150);
		//in case both animations are used (add (fadeTransition, translateTransition) in the second line under this command)	    
		ParallelTransition parallelTransition = new ParallelTransition();
		parallelTransition.getChildren().addAll(translateTransition);//(fadeTransition, translateTransition);
		parallelTransition.play();
	}
	
	void setLocalUI(){
		switch(getLocal()){
		case "en_US":	
			bundle = ResourceBundle.getBundle("recources.HomeFlix-Local", Locale.US);	//us_English
			cbLocal.getSelectionModel().select(0);
			break;
     	case "de_DE":	
     		bundle = ResourceBundle.getBundle("recources.HomeFlix-Local", Locale.GERMAN);	//German
			cbLocal.getSelectionModel().select(1);
			break;
     	default:		
     		bundle = ResourceBundle.getBundle("recources.HomeFlix-Local", Locale.US);	//default local
			cbLocal.getSelectionModel().select(0);
			break;
		 }
		infoBtn.setText(bundle.getString("info"));
		settingsBtn.setText(bundle.getString("settings"));
		streamingSettingsBtn.setText(bundle.getString("streamingSettings"));
		tfPath.setPromptText(bundle.getString("tfPath"));
		tfStreamingPath.setPromptText(bundle.getString("tfPath"));
		tfsearch.setPromptText(bundle.getString("tfSearch"));
		openfolderbtn.setText(bundle.getString("openFolder"));
		updateBtn.setText(bundle.getString("checkUpdates"));
		directoryBtn.setText(bundle.getString("chooseFolder"));
		streamingDirectoryBtn.setText(bundle.getString("chooseFolder"));
		sizelbl.setText(bundle.getString("fontSize"));
		aulbl.setText(bundle.getString("autoUpdate"));
		versionlbl.setText(bundle.getString("version")+" "+version+" (Build: "+buildNumber+")");
		columnTitel.setText(bundle.getString("columnName"));
		columnRating.setText(bundle.getString("columnRating"));
		columnStreamUrl.setText(bundle.getString("columnStreamUrl"));
		columnResolution.setText(bundle.getString("columnResolution"));
		columnSeason.setText(bundle.getString("columnSeason"));
		columnYear.setText(bundle.getString("columnYear"));
		errorUpdateD = bundle.getString("errorUpdateD");
		errorUpdateV = bundle.getString("errorUpdateV");
		errorPlay = bundle.getString("errorPlay");
		errorOpenStream = bundle.getString("errorOpenStream");
		errorMode = bundle.getString("errorMode");
		errorLoad = bundle.getString("errorLoad");
		errorSave = bundle.getString("errorSave");
		noFilmFound = bundle.getString("noFilmFound");
		infoText = bundle.getString("version")+" "+version+" (Build: "+buildNumber+") "+versionName+bundle.getString("infoText");
		linuxBugText = bundle.getString("linuxBug");
		vlcNotInstalled = bundle.getString("vlcNotInstalled");
		
		title = bundle.getString("title");
		year = bundle.getString("year");
		rating = bundle.getString("rating");
		publishedOn = bundle.getString("publishedOn");
		duration = bundle.getString("duration");
		genre = bundle.getString("genre");
		director = bundle.getString("director");
		writer = bundle.getString("writer");
		actors = bundle.getString("actors");
		plot = bundle.getString("plot");
		language = bundle.getString("language");
		country = bundle.getString("country");
		awards = bundle.getString("awards");
		metascore = bundle.getString("metascore");
		imdbRating = bundle.getString("imdbRating");
		type = bundle.getString("type");
	}
	
	void showErrorMsg(String msg, IOException exception){
		Alert alert = new Alert(AlertType.ERROR);
    	alert.setTitle("Error");
    	alert.setHeaderText("");
    	alert.setContentText(msg);
    	alert.initOwner(main.primaryStage);
    	
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
    	
    	exception.printStackTrace();
		
	}
	
	//saves the Settings
	public void saveSettings(){
		System.out.println("saving settings ...");
		OutputStream outputStream;	//new output-stream
		try {
			props.setProperty("path", getPath());	//writes path into property
			props.setProperty("color", getColor());
			props.setProperty("autoUpdate", String.valueOf(isAutoUpdate()));
			props.setProperty("size", getSize().toString());
			props.setProperty("local", getLocal());
			props.setProperty("streamingPath", getStreamingPath());
			props.setProperty("mode", getMode());
			props.setProperty("ratingSortType", columnRating.getSortType().toString());
			if(System.getProperty("os.name").equals("Linux")){
				outputStream = new FileOutputStream(fileLinux);
			}else{
				outputStream = new FileOutputStream(fileWin);
			}
			props.storeToXML(outputStream, "Project HomeFlix settings");	//writes new .xml
			outputStream.close();
		} catch (IOException e) {
			if(firststart == false){
				showErrorMsg(errorLoad, e);
				e.printStackTrace();
			}
		}
	}
	
	//loads the Settings
	public void loadSettings(){
		System.out.println("loading settings ...");
		InputStream inputStream;
		try {
			if(System.getProperty("os.name").equals("Linux")){
				inputStream = new FileInputStream(fileLinux);
			}else{
				inputStream = new FileInputStream(fileWin);
			}
			props.loadFromXML(inputStream);	//new input-stream from .xml
			path = props.getProperty("path");	//read path from property
			streamingPath = props.getProperty("streamingPath");
			color = props.getProperty("color");
			size = Double.parseDouble(props.getProperty("size"));
			autoUpdate = Boolean.parseBoolean(props.getProperty("autoUpdate"));
			local = props.getProperty("local");
			ratingSortType = props.getProperty("ratingSortType");
			
			switch (props.getProperty("mode")) {
			case "local":
				mode = "local";
				break;
			case "streaming":
				mode = "streaming";
				break;
			default:
				mode = "local";
				break;
			}
			
			inputStream.close();
		} catch (IOException e) {
			if(firststart == false){
				showErrorMsg(errorSave, e);
				e.printStackTrace();
			}
//			showErrorMsg(errorLoad, e); //TODO This should not be visible at first startup
		}
	}
	
	//cuts 0x of the Color-pickers return value
	private void editColor(String input){
		StringBuilder sb = new StringBuilder(input);
		sb.delete(0, 2);
		this.color = sb.toString();
		saveSettings();
	}
	
	//getter and setter
	public void setColor(String input){
		this.color = input;
	}
	
	public String getColor(){
		return color;
	}

	public void setPath(String input){
		this.path = input;
	}
	
	public String getPath(){
		return path;
	}
	
	public void setStreamingPath(String input){
		this.streamingPath = input;
	}
	
	public String getStreamingPath(){
		return streamingPath;
	}
	
	public void setSize(Double input){
		this.size = input;
	}
	
	public Double getSize(){
		return size;
	}
	
	public void setAutoUpdate(boolean input){
		this.autoUpdate = input;
	}
	
	public boolean isAutoUpdate(){
		return autoUpdate;
	}
	
	public void setLocal(String input){
		this.local = input;
	}
	
	public String getLocal(){
		return local;
	}
	
	public void setMode(String input){
		this.mode = input;
	}
	
	public String getMode(){
		return mode;
	}

	public String getCurrentWorkingDirectory() {
		return currentWorkingDirectory;
	}

	public void setCurrentWorkingDirectory(String currentWorkingDirectory) {
		this.currentWorkingDirectory = currentWorkingDirectory;
	}
}
