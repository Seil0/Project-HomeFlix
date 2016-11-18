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

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Scanner;

import org.apache.commons.lang3.SystemUtils;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
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
	private TreeTableView<streamUiData> treeTableViewfilm;
	@FXML
	private TableView<streamUiData> tableViewStreamingdata;
	@FXML
	private JFXTextArea ta1;
	@FXML
	private JFXButton menubtn;
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
    private JFXButton updateBtn;
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
    public	ChoiceBox<String> cbLocal;
    @FXML
    public JFXSlider sliderFontSize;
    @FXML
    private JFXDialog dialog = new JFXDialog();
    @FXML
    private Label versionlbl;
    @FXML
    private Label sizelbl;
    @FXML
    private Label aulbl;
    @FXML
    private ImageView image1;
    
    
    @FXML
    TreeItem<streamUiData> root = new TreeItem<>(new streamUiData(1, 1, 1, 5.0,"1", "filme","1"));
    @FXML
    TreeTableColumn<streamUiData, Double> columnRating = new TreeTableColumn<>("Bewertung");
    @FXML
    TreeTableColumn<streamUiData, String> columnTitel = new TreeTableColumn<>("Name");
    @FXML
    TreeTableColumn<streamUiData, String> columnStreamUrl = new TreeTableColumn<>("Datei Name");
    @FXML
    TreeTableColumn<streamUiData, String> columnResolution = new TreeTableColumn<>("Aufl�sung");
    @FXML
    TreeTableColumn<streamUiData, Integer> columnYear = new TreeTableColumn<>("Jahr");
    @FXML
    TreeTableColumn<streamUiData, Integer> columnSeason = new TreeTableColumn<>("Staffel");
    @FXML
    TreeTableColumn<streamUiData, Integer> columnEpisode = new TreeTableColumn<>("Episode");
    
    @FXML
    private TreeItem<streamUiData> streamingRoot =new TreeItem<>(new streamUiData(1 ,1 ,1 ,1.0 ,"1" ,"filme" ,"1"));
    @FXML
    private TableColumn<streamUiData, String> dataNameColumn = new TableColumn<>("Datei Name");
    @FXML
    private TableColumn<streamUiData, String> dataNameEndColumn = new TableColumn<>("Datei Name mit Endung");
    
	
	private boolean menutrue = false;	//saves the position of menubtn (opened or closed)
	private boolean settingstrue = false;
	private boolean streamingSettingsTrue = false;
	private String version = "0.4.0";
	private String buildNumber = "100";
	private String versionName = "glowing bucket";
	private String buildURL = "https://raw.githubusercontent.com/Seil0/Project-HomeFlix/master/updates/buildNumber.txt";
	private String downloadLink = "https://raw.githubusercontent.com/Seil0/Project-HomeFlix/master/updates/downloadLink.txt";
	private File dir = new File(System.getProperty("user.home") + "/Documents/HomeFlix");	
	private File file = new File(dir + "/config.xml");	
	
	private String updateDataURL;
	private String errorUpdateD;
	private String errorUpdateV;
	private String errorPlay;
	private String errorOpenStream;
	private String errorMode;
	private String infoText;
	private String linuxBugText;
	private String vlcNotInstalled;
	private String aktBuildNumber;
	private String path;
	private String streamingPath;
	private String color;
	private String Name;
	private String datPath;
	private String autoUpdate;
	private String mode;
	private double size;
	private int last;
	private int selected;
	private int next;
	private int local;
	private File selectedFolder;
	private File selectedStreamingFolder;
	private ResourceBundle bundle;

	private ObservableList<streamUiData> newDaten = FXCollections.observableArrayList();
	private ObservableList<streamUiData> filterData = FXCollections.observableArrayList();
	private ObservableList<streamUiData> streamData = FXCollections.observableArrayList();
	private ObservableList<String> locals = FXCollections.observableArrayList("english", "deutsch");
	private ObservableList<streamUiData> streamingData = FXCollections.observableArrayList();
	private ImageView menu_icon_black = new ImageView(new Image("recources/menu_icon_black.png"));
	private ImageView menu_icon_white = new ImageView(new Image("recources/menu_icon_white.png"));
	private DirectoryChooser directoryChooser = new DirectoryChooser();
	Properties props = new Properties();
	
	//wenn menubtn clicked
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
		if(SystemUtils.IS_OS_LINUX){
			System.out.println("This is Linux");
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
				// Auto-generated catch block
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
	        	alert.showAndWait();
			}else{
				try {
					Runtime.getRuntime().exec("vlc "+getPath()+"/"+ datPath);
				} catch (IOException e) {
					showErrorMsg(errorPlay,e);
				}
			}
		}else if(SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_MAC_OSX){
			System.out.println("This is Windows or Mac OSX");
			if(mode.equals("local")){
				try {
					Desktop.getDesktop().open(new File(getPath()+"\\"+ datPath));
				} catch (IOException e) {
					showErrorMsg(errorPlay,e);
				}
			}else if(mode.equals("streaming")){
				try {
					Desktop.getDesktop().browse(new URI(datPath));	//opens the streaming url in browser (other option?)
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
			Desktop.getDesktop().open(new File(getPath()));	//�ffnet den aktuellen Pfad
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
	 * TODO zus�tzliche infos �ber die dateien
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
		sideMenuSlideOut();		//disables sidemenu
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
		update();
	}
	
	@FXML
	private void autoupdateBtnAction(){
		if(autoUpdate.equals("0")){
    		setAutoUpdate("1");
    	}else{
    		setAutoUpdate("0");
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
	
	
	//"Main" Methode die beim start von der Klasse Main aufgerufen wird, initialiesirung der einzellnen UI-Objekte 
	public void setMain(Main main) {
		
		loadSettings();
		initTabel();
		initActions();
		
		System.out.println("Mode: "+mode);
		
		debugBtn.setDisable(true); 	//debugging btn for tests
		debugBtn.setVisible(false);
        
        tfPath.setText(getPath());

        sliderFontSize.setValue(getSize());
        
        cbLocal.setItems(locals);
        
        updateBtn.setFont(Font.font("System", 12));
        
        if(autoUpdate.equals("1")){
    		autoupdateBtn.setSelected(true);
    		update();
    	}else{
    		autoupdateBtn.setSelected(false);
    	}

    	ta1.setWrapText(true);
    	ta1.setEditable(false);
    	ta1.setFont(Font.font("System", getSize()));
	}
	
	//initialisierung der Tabellen f�r filme(beide Modi) und Streaming-Settings
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initTabel(){

		//filmtabelle 
	    columnRating.setMaxWidth(120);
	    columnTitel.setMaxWidth(240);
	    columnStreamUrl.setMaxWidth(0);
	    dataNameColumn.setPrefWidth(130);
	    dataNameEndColumn.setPrefWidth(170);
		
        treeTableViewfilm.setRoot(root);
        treeTableViewfilm.setColumnResizePolicy( TreeTableView.CONSTRAINED_RESIZE_POLICY );
        treeTableViewfilm.setShowRoot(false);
		
        //inhalt in Zelle schreiben
        columnTitel.setCellValueFactory((CellDataFeatures<streamUiData, String> p) -> 
        new ReadOnlyStringWrapper(p.getValue().getValue().getTitel())); 
       
        columnRating.setCellValueFactory((CellDataFeatures<streamUiData, Double> p) -> 
        new ReadOnlyObjectWrapper<Double>(p.getValue().getValue().getRating()));
        
        columnStreamUrl.setCellValueFactory((CellDataFeatures<streamUiData, String> p) -> 
        new ReadOnlyStringWrapper(p.getValue().getValue().getStreamUrl()));
        
        columnResolution.setCellValueFactory((CellDataFeatures<streamUiData, String> p) -> 
        new ReadOnlyStringWrapper(p.getValue().getValue().getResolution()));
        
        columnYear.setCellValueFactory((CellDataFeatures<streamUiData, Integer> p) -> 
        new ReadOnlyObjectWrapper(p.getValue().getValue().getYear()));
        
        columnSeason.setCellValueFactory((CellDataFeatures<streamUiData, Integer> p) -> 
        new ReadOnlyObjectWrapper(p.getValue().getValue().getSeason()));
        
        columnEpisode.setCellValueFactory((CellDataFeatures<streamUiData, Integer> p) -> 
        new ReadOnlyObjectWrapper(p.getValue().getValue().getEpisode()));

        treeTableViewfilm.getColumns().addAll(columnTitel, columnRating, columnStreamUrl, columnResolution, columnYear, columnSeason, columnEpisode);
	    treeTableViewfilm.getColumns().get(2).setVisible(false); //blendet die Column mit den Dateinamen aus (wichtig um sie abzuspielen)
	
	    //Changelistener f�r TreeTable
	    treeTableViewfilm.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {	
			@Override
			public void changed(ObservableValue<?> observable, Object oldVal, Object newVal) {
				// last = selected; //f�r autoplay
				selected = treeTableViewfilm.getSelectionModel().getSelectedIndex(); // holt aktuelles Item
				last = selected - 1;
				next = selected + 1;
				Name = columnTitel.getCellData(selected); // holt Namen des Aktuelle Items aus der ColumnName
				datPath = columnStreamUrl.getCellData(selected); // holt den aktuellen Datei Pfad aus der ColumnDatName
				ta1.setText(""); // l�scht Text in ta1
				apiAbfrage(Name); // startet die api abfrage
				ta1.positionCaret(0); // setzt die startposition des Cursors in
										// ta1
			}
		});
	    
	    //Streaming-Settings Tabelle
	    dataNameColumn.setCellValueFactory(cellData -> cellData.getValue().titelProperty());
	    dataNameEndColumn.setCellValueFactory(cellData -> cellData.getValue().streamUrlProperty());
		
	    tableViewStreamingdata.getColumns().addAll(dataNameColumn, dataNameEndColumn);
	    tableViewStreamingdata.setItems(streamingData);
	}
	
	//initialisierung der Button Actions
	private void initActions(){
		
		//TODO unterscheiden zwischen streaming und local
        tfsearch.textProperty().addListener(new ChangeListener<String>() {
    	    @SuppressWarnings("unchecked")
			@Override
    	    public void changed(ObservableValue<? extends String> observable,String oldValue, String newValue) {
    	    	int counter = newDaten.size();
    	    	filterData.removeAll(filterData);
    	    	root.getChildren().remove(0,root.getChildren().size());
    	    	
    	    	for(int i = 0; i < counter; i++){
    	    		if(newDaten.get(i).getTitel().toLowerCase().contains(tfsearch.getText().toLowerCase())){
    	    			filterData.add(newDaten.get(i));
    	    		}
    	    	}
    	    	
    	    	for(int i = 0; i < filterData.size(); i++){
    				root.getChildren().addAll(new TreeItem<streamUiData>(filterData.get(i)));	//f�gt daten zur Rootnode hinzu
    			}
    	    }
    	});
        
        cbLocal.getSelectionModel().selectedIndexProperty()
        .addListener(new ChangeListener<Number>() {
          public void changed(ObservableValue<? extends Number> ov, Number value, Number new_value) { 
        	  setLocal(new_value.intValue());
        	  setLoaclUI(local);
        	  saveSettings();
          }
        });
        
        sliderFontSize.valueProperty().addListener(new ChangeListener<Number>() {
			 @Override
			public void changed(ObservableValue<? extends Number> ov,Number old_val, Number new_val) {
				setSize(sliderFontSize.getValue()); 
				ta1.setFont(Font.font("System", size));
				saveSettings();
			 }
        });
	}
	
	//pr�ft auf Update und f�rht es gegebenenfalls aus
	private void update(){
		
		System.out.println("check for updates ...");
		try {
			URL url = new URL(buildURL); //URL der Datei mit aktueller Versionsnummer
	        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	        aktBuildNumber = in.readLine();	//schreibt inputstream in String
	        in.close();
		} catch (IOException e1) {
			showErrorMsg(errorUpdateV, e1);
		}
		System.out.println("Build: "+buildNumber+", Update: "+aktBuildNumber);
		
		
		//vergleicht die Versionsnummern, bei aktversion > version wird ein Update durchgrf�hrt
		int iversion = Integer.parseInt(buildNumber.replace(".", ""));
		int iaktVersion = Integer.parseInt(aktBuildNumber.replace(".", ""));
		
		if(iversion >= iaktVersion){
			updateBtn.setText(bundle.getString("updateBtnNotavail"));
			System.out.println("no update available");
		}else{
			updateBtn.setText(bundle.getString("updateBtnavail"));
			System.out.println("update available");
		try {
			URL website;
			URL downloadURL = new URL(downloadLink);
			BufferedReader in = new BufferedReader(new InputStreamReader(downloadURL.openStream()));
			updateDataURL = in.readLine();
			website = new URL(updateDataURL);	//Update URL
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());	//open new Stream/Channel
			FileOutputStream fos = new FileOutputStream("ProjectHomeFlix.jar");	//nea fileoutputstram for ProjectHomeFLix.jar
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);	//gets file from 0 to max size
			fos.close();	//close fos (extrem wichtig!)
			Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar");	//start again
			System.exit(0);	//finishes itself
		} catch (IOException e) {
			//in case there is an error
			showErrorMsg(errorUpdateD, e);
		}
		}
	}
	
	//l�dt die Daten im angegeben Ordner in newDaten
	public void loadData(){
			//load local Data
			if(getPath().equals("")||getPath().equals(null)){
				System.out.println("Kein Pfad angegeben");	//falls der Pfad null oder "" ist
			}else{
			String[] entries = new File(getPath()).list();
				for(int i = 0; i < entries.length; i++){
					String titel = ohneEndung(entries[i]);
					String data = entries[i];
					newDaten.add(new streamUiData(1, 1, 1, 5.0, "1", titel, data));
				}
			}

			//load streaming Data TODO pr�fen ob streaming daten vorhanden -> momentan evtl. fehler
			String titel = null;
        	String resolution = null;
        	String streamUrl = null;  
        	int season;
        	int episode;
        	int year;
        	double rating = 5.0;
        	if(getStreamingPath().equals("")||getStreamingPath().equals(null)){
				System.out.println("Kein Pfad angegeben");	//falls der Pfad null oder "" ist
			}else{
			for(int i=0; i< streamingData.size(); i++){
				String fileName = streamingPath+"/"+streamingData.get(i).getStreamUrl();
				try {
					JsonObject object = Json.parse(new FileReader(fileName)).asObject();
					JsonArray items = object.get("entries").asArray();
					
	            	for (JsonValue item : items) {
	            	  titel = item.asObject().getString("titel","");
	            	  season = item.asObject().getInt("season", 0);
	            	  episode = item.asObject().getInt("episode", 0);
	            	  year = item.asObject().getInt("year", 0);
	            	  resolution = item.asObject().getString("resolution", "");
	            	  streamUrl = item.asObject().getString("streamUrl", "");
	            	  streamData.add(new streamUiData(year, season, episode, rating, resolution, titel, streamUrl));
	            	}
					
				} catch (IOException e) {
					//Auto-generated catch block
					e.printStackTrace();
				}
			}
			}	
	}
	
	public void addDataUI(){
		if(mode.equals("local")){
			for(int i = 0; i < newDaten.size(); i++){
				root.getChildren().add(new TreeItem<streamUiData>(newDaten.get(i)));	//f�gt daten zur Rootnode hinzu
			}
			columnRating.setMaxWidth(120);
		    columnTitel.setMaxWidth(240);
			treeTableViewfilm.getColumns().get(3).setVisible(false);
			treeTableViewfilm.getColumns().get(4).setVisible(false);
			treeTableViewfilm.getColumns().get(5).setVisible(false);
			treeTableViewfilm.getColumns().get(6).setVisible(false);
		}else if(mode.equals("streaming")){
			for(int i = 0; i < streamData.size(); i++){
				root.getChildren().add(new TreeItem<streamUiData>(streamData.get(i)));	//f�gt daten zur Rootnode hinzu
			}
			columnTitel.setMaxWidth(150);
			columnResolution.setMaxWidth(65);
			columnRating.setMaxWidth(52.5);
			columnYear.setMaxWidth(40);
			columnSeason.setMaxWidth(52.5);
			columnEpisode.setMaxWidth(0);	//disabled for ui size reasons
			treeTableViewfilm.getColumns().get(3).setVisible(true);
			treeTableViewfilm.getColumns().get(4).setVisible(true);
			treeTableViewfilm.getColumns().get(5).setVisible(true);
			treeTableViewfilm.getColumns().get(6).setVisible(true);
		}
	}
	
	public void loadStreamingSettings(){
		if(getStreamingPath().equals("")||getStreamingPath().equals(null)){
			System.out.println("Kein Pfad angegeben");	//falls der Pfad null oder "" ist
		}else{
		String[] entries = new File(getStreamingPath()).list();
			for(int i = 0; i < entries.length; i++){
				if(entries[i].endsWith(".json")){
					String titel = ohneEndung(entries[i]);
					String data = entries[i];
					streamingData.add(new streamUiData(1,1,1,5.0,"1",titel ,data));
				}
			}
			for(int i = 0; i < streamingData.size(); i++){
				streamingRoot.getChildren().add( new TreeItem<streamUiData>(streamingData.get(i)));	//f�gt daten zur Rootnode hinzu
			}
		}	
	}
	//entfernt die Endung vom String
	private String ohneEndung (String str) {
		if (str == null) return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1) return str;
		return str.substring(0, pos);
	}
	
	//setzt die Farben f�r die UI-Elemente
	public void applyColor(){
		String style = "-fx-background-color: #"+getColor()+";";
		String btnStyle = "-fx-button-type: RAISED; -fx-background-color: #"+getColor()+"; -fx-text-fill: BLACK;";
		String btnStylewhite = "-fx-button-type: RAISED; -fx-background-color: #"+getColor()+"; -fx-text-fill: WHITE;";
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
			directoryBtn.setStyle(btnStylewhite);
			streamingDirectoryBtn.setStyle(btnStyle);
			updateBtn.setStyle(btnStylewhite);
			playbtn.setStyle(btnStylewhite);
			openfolderbtn.setStyle(btnStylewhite);
			returnBtn.setStyle(btnStylewhite);
			forwardBtn.setStyle(btnStylewhite);
			menubtn.setGraphic(menu_icon_white);
		}else{
			settingsBtn.setStyle("-fx-text-fill: BLACK;");
			streamingSettingsBtn.setStyle("-fx-text-fill: BLACK;");
			switchBtn.setStyle("-fx-text-fill: BLACK;");
			infoBtn.setStyle("-fx-text-fill: BLACK;");
			debugBtn.setStyle("-fx-text-fill: BLACK;");
			directoryBtn.setStyle(btnStyle);
			streamingDirectoryBtn.setStyle(btnStyle);
			updateBtn.setStyle(btnStyle);
			playbtn.setStyle(btnStyle);
			openfolderbtn.setStyle(btnStyle);
			returnBtn.setStyle(btnStyle);
			forwardBtn.setStyle(btnStyle);
			menubtn.setGraphic(menu_icon_black);
		}
		
		//das solte weg kann aber hier bleiben wicht ist dass es zum selben zeitpunkt wie aply color ausgef�hrt wird
		if(mode.equals("local")){
			switchBtn.setText("streaming");	//TODO translate
		}else if(mode.equals("streaming")){
			switchBtn.setText("local");
		}
	}
	
	private void sideMenuSlideIn(){
		sideMenuVBox.setVisible(true);
		//einblenden von 40% nach 100% deckkraft in 400ms
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), sideMenuVBox);
		fadeTransition.setFromValue(0.4);
		fadeTransition.setToValue(1.0);
		//einfahren des side munes in 400ms
		TranslateTransition translateTransition = new TranslateTransition(Duration.millis(400), sideMenuVBox);
		translateTransition.setFromX(-150);
		translateTransition.setToX(0);
		//falls beides verwendet werden soll	    
		ParallelTransition parallelTransition = new ParallelTransition();
		parallelTransition.getChildren().addAll(translateTransition);//(fadeTransition, translateTransition);
		parallelTransition.play();
	}
	
	private void sideMenuSlideOut(){
//		sideMenuVBox.setVisible(false);
		//ausblenden von 100% nach 40% deckkraft in 400ms
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), sideMenuVBox);
		fadeTransition.setFromValue(1.0);
		fadeTransition.setToValue(0.4);
		//ausfahren des side munes in 400ms
		TranslateTransition translateTransition = new TranslateTransition(Duration.millis(400), sideMenuVBox);
		translateTransition.setFromX(0);
		translateTransition.setToX(-150);
		//falls beides verwendet werden soll	    
		ParallelTransition parallelTransition = new ParallelTransition();
		parallelTransition.getChildren().addAll(translateTransition);//(fadeTransition, translateTransition);
		parallelTransition.play();
	}
	
	public void setLoaclUI(int local){
		switch(local){
		case 0: bundle = ResourceBundle.getBundle("recources.HomeFlix-Local", Locale.US);	//us_english
				break;
     	case 1:	bundle = ResourceBundle.getBundle("recources.HomeFlix-Local", Locale.GERMAN);	//german
     			break;
     	default:bundle = ResourceBundle.getBundle("recources.HomeFlix-Local", Locale.US);	//default local
     			break;
		 }
		infoBtn.setText(bundle.getString("info"));
		settingsBtn.setText(bundle.getString("settings"));
		streamingSettingsBtn.setText(bundle.getString("streamingSettings"));
		playbtn.setText(bundle.getString("play"));
		tfPath.setPromptText(bundle.getString("tfPath"));
		tfStreamingPath.setPromptText(bundle.getString("tfPath"));
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
		infoText = bundle.getString("version")+" "+version+" (Build: "+buildNumber+") "+versionName+bundle.getString("infoText");
		linuxBugText = bundle.getString("linuxBug");
		vlcNotInstalled = bundle.getString("vlcNotInstalled");
	}
	
	private void showErrorMsg(String msg, IOException exception){
		Alert alert = new Alert(AlertType.ERROR);
    	alert.setTitle("Error");
    	alert.setHeaderText("");
    	alert.setContentText(msg);
    	
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
	
	//speichert die Einstellungen
	public void saveSettings(){
		try {
			props.setProperty("path", getPath());	//setzt pfad in propselement
			props.setProperty("color", getColor());
			props.setProperty("autoUpdate", getAutoUpdate());
			props.setProperty("size", getSize().toString());
			props.setProperty("local", Integer.toString(getLocal()));
			props.setProperty("streamingPath", getStreamingPath());
			props.setProperty("mode", getMode());
			OutputStream outputStream = new FileOutputStream(file);	//neuer outputstream
			props.storeToXML(outputStream, "Project HomeFlix settings");
			outputStream.close();
		} catch (IOException e) {
			System.out.println("An error has occurred!");
			e.printStackTrace();
		}
	}
	
	//l�dt die Einstellungen
	public void loadSettings(){
		try {
			InputStream inputStream = new FileInputStream(file);
			props.loadFromXML(inputStream);
			path = props.getProperty("path");	//setzt Propselement in Pfad
			streamingPath = props.getProperty("streamingPath");
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
	
	//entfernt 0x von dem R�ckgabewert des Colorpickers
	private void editColor(String input){
		StringBuilder sb = new StringBuilder(input);
		sb.delete(0, 2);
		this.color = sb.toString();
		saveSettings();
	}
	
	//getter Und setter
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
	
	public void setAutoUpdate(String input){
		this.autoUpdate = input;
	}
	
	public String getAutoUpdate(){
		return autoUpdate;
	}
	
	public void setLocal(int input){
		this.local = input;
	}
	
	public int getLocal(){
		return local;
	}
	
	public void setMode(String input){
		this.mode = input;
	}
	
	public String getMode(){
		return mode;
	}
	
	//methode der API-Abfrage
	@SuppressWarnings("deprecation")
	private void apiAbfrage(String input){
		URL url = null;
		Scanner sc = null;
		String apiurl = "https://www.omdbapi.com/?";	//API URL
		String moviename = null;
		String dataurl = null;
		String retdata = null;
		InputStream is = null;
		DataInputStream dis = null;

		try {

			// hohlen des Filmtitels
			sc = new Scanner(System.in);
			moviename = input;

			// f�r keinen oder "" Filmtitel
			if (moviename == null || moviename.equals("")) {
				System.out.println("No movie found");
			}

			//entfernen ungewolter leerzeichen
			moviename = moviename.trim();

			// ersetzen der Leerzeichen durch + f�r api-abfrage
			moviename = moviename.replace(" ", "+");

			//URL wird zusammengestellt abfragetypen: http,json,xml (muss json sein um sp�teres trennen zu erm�glichen)
			dataurl = apiurl + "t=" + moviename + "&plot=full&r=json";

			url = new URL(dataurl);
			is = url.openStream();
			dis = new DataInputStream(is);

			// lesen der Daten aus dem Antwort Stream
			while ((retdata = dis.readLine()) != null) {
				//retdata in json object parsen und anschlie�end das json Objekt "zerschneiden"
				System.out.println(retdata);
				JsonObject object = Json.parse(retdata).asObject();
				String titel = object.getString("Title", "");
				String year = object.getString("Year", "");
				String rated = object.getString("Rated", "");
				String released = object.getString("Released", "");
				String runtime = object.getString("Runtime", "");
				String genre = object.getString("Genre", "");
				String director = object.getString("Director", "");
				String writer = object.getString("Writer", "");
				String actors  = object.getString("Actors", "");
				String plot = object.getString("Plot", "");
				String language = object.getString("Language", "");
				String country = object.getString("Country", "");
				String awards = object.getString("Awards", "");
				String posterURL = object.getString("Poster", "");
				String metascore = object.getString("Metascore", "");
				String imdbRating = object.getString("imdbRating", "");
				@SuppressWarnings("unused")
				String imdbVotes = object.getString("imdbVotes", "");
				@SuppressWarnings("unused")
				String imdbID = object.getString("imdbID", "");
				String type = object.getString("Type", "");
				String response = object.getString("Response", "");
				
				if(response.equals("False")){
					ta1.appendText("Kein Film mit diesem Titel gefunden!!");
					Image im2 = new Image("http://publicdomainvectors.org/photos/jean_victor_balin_cross.png");
					image1.setImage(im2);
				}else{
				//ausgabe des Textes in ta1 in jeweils neuer Zeile
				ta1.appendText("Titel: "+titel+"\n");
				ta1.appendText("Jahr: "+ year+"\n");
				ta1.appendText("Einstufung: "+rated+"\n");
				ta1.appendText("Ver�ffentlicht am: "+released+"\n");
				ta1.appendText("Laufzeit: "+runtime+"\n");
				ta1.appendText("Genre: "+genre+"\n");
				ta1.appendText("Regisseur: "+director+"\n");
				ta1.appendText("Autor: "+writer+"\n");
				ta1.appendText("Schauspieler: "+actors+"\n");
				ta1.appendText("Beschreibung: "+plot+"\n");
				ta1.appendText("Original Sprache: "+language+"\n");
				ta1.appendText("Produktionsland: "+country+"\n");
				ta1.appendText("Auszeichnungen: "+awards+"\n");
				ta1.appendText("Metascore: "+metascore+"\n");
				ta1.appendText("imdb Bewertung: "+imdbRating+"\n");
				ta1.appendText("Type: "+type+"\n");
				
				Image im1 = new Image(posterURL);
				image1.setImage(im1);
				}
			}

		} catch (Exception e) {
			System.out.println(e);
		} finally {
			try {
				//schlie�t datainputStream, InputStream,Scanner
				if (dis != null) {
					dis.close();
				}

				if (is != null) {
					is.close();
				}

				if (sc != null) {
					sc.close();
				}
			} catch (Exception e2) {
				;
			}
		}
	}
}