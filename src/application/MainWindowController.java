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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Scanner;

import org.apache.commons.lang3.SystemUtils;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;

public class MainWindowController {
	@FXML
	private AnchorPane anpane;
	@FXML
	private AnchorPane settingsan = new AnchorPane();
	@FXML
	private VBox topVBox;
	@FXML
	private VBox menuBox = new VBox();
	@FXML
	private VBox settingsBox = new VBox();
	@FXML
	private TreeTableView<uiData> treeTableViewfilm;
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
    private JFXButton infoBtn = new JFXButton("Info");
    @FXML
    private JFXButton demoBtn = new JFXButton("Debugging");
    @FXML
    private JFXButton settingsBtn = new JFXButton("Settings");
    @FXML
    private JFXButton updateBtn = new JFXButton("Auf Update pr�fen");
    @FXML
    private JFXButton directoryBtn = new JFXButton("Ordner ausw�hlen");
    @FXML
    private JFXToggleButton autoupdateBtn = new JFXToggleButton();
    @FXML
    public JFXTextField tfPfad = new JFXTextField();
    @FXML
    private JFXTextField tfsearch;
    @FXML
    public JFXColorPicker mainColor = new JFXColorPicker();
    @FXML
    public	ChoiceBox<String> cbLocal = new ChoiceBox<String>();
    @FXML
    public JFXSlider sl1 = new JFXSlider();
    @FXML
    private JFXDialog dialog = new JFXDialog();
    @FXML
    private Label versionlbl = new Label();
    @FXML
    private Label sizelbl = new Label("Schriftgr��e:");
    @FXML
    private Label aulbl = new Label("beim starten nach Updates suchen:");
    @FXML
    private ImageView image1;
    
    @FXML
    TreeItem<uiData> root = new TreeItem<>(new uiData(1.0, "filme","1"));
    @FXML
    TreeTableColumn<uiData, Double> columnRating = new TreeTableColumn<>("Bewertung");
    @FXML
    TreeTableColumn<uiData, String> columnName = new TreeTableColumn<>("Name");
    @FXML
    TreeTableColumn<uiData, String> columnDatName = new TreeTableColumn<>("Datei Name");
	
	private boolean menutrue = false;	//merker f�r menubtn (�ffnen oder schlie�en)
	private boolean settingstrue = false;
	private String version = "0.3.6";
	private String versionURL = "https://raw.githubusercontent.com/Seil0/Project-HomeFlix/master/updates/version.txt";
	private String downloadLink = "https://raw.githubusercontent.com/Seil0/Project-HomeFlix/master/updates/downloadLink.txt"; 
	
	private String updateDataURL;
	private String errorUpdateD;
	private String errorUpdateV;
	private String errorPlay;
	private String infoText;
	private String linuxBugText;
	private String vlcNotInstalled;
	private String aktVersion;
	private String path;
	private String color;
	private String Name;
	private String datPath;
	private String autoUpdate;
	private double size;
	private int last;
	private int selected;
	private int next;
	private int local;
	private File selectedFolder;
	private ResourceBundle bundle;

	private ObservableList<uiData> newDaten = FXCollections.observableArrayList();
	private ObservableList<uiData> filterData = FXCollections.observableArrayList();
	private ObservableList<String> locals = FXCollections.observableArrayList("english", "deutsch");
	private Image imHF = new Image("recources/Homeflix_Poster.png");
	private ImageView menu_icon_black = new ImageView(new Image("recources/menu_icon_black.png"));
	private ImageView menu_icon_white = new ImageView(new Image("recources/menu_icon_white.png"));
	private DirectoryChooser directoryChooser = new DirectoryChooser();
	Properties props = new Properties();
	
	//wenn menubtn clicked
	/**
	 * TODO change value of Text-color change
	 * TODO animation of side menu
	 */
	@FXML
	private void menubtnclicked(){
		if(menutrue == false){
			anpane.getChildren().addAll(menuBox);
			
			//infobtn clicked
			infoBtn.setOnAction(new EventHandler<ActionEvent>(){
	            @Override
	            public void handle(ActionEvent event) {
	            	Alert alert = new Alert(AlertType.INFORMATION);
	            	alert.setTitle("Info");
	            	alert.setHeaderText("Project HomeFlix");
	            	alert.setContentText(infoText);
	            	alert.showAndWait();
	            }
			});
			
			//setteingsbtn clicked, deklarieren der actions der Objekte die bei settingsbtn angezeigt werden
			settingsBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event){
					if(settingstrue == false){
						anpane.getChildren().addAll(settingsBox);
						
						tfPfad.setOnAction(new EventHandler<ActionEvent>(){
							@Override
							public void handle(ActionEvent event){
								setPath(tfPfad.getText());
								saveSettings();
							}
						});
						directoryBtn.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event){
								selectedFolder = directoryChooser.showDialog(null);  
				                if(selectedFolder == null){
				                    System.out.println("No Directory selected");
				                }else{
				                	setPath(selectedFolder.getAbsolutePath());
				                	saveSettings();
									tfPfad.setText(getPath());
									try {
										Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar");	//starte neu
										System.exit(0);	//beendet sich selbst
									} catch (IOException e) {
										System.out.println("es ist ein Fehler aufgetreten");
										e.printStackTrace();
									}
				                }
							}
						});
						mainColor.setOnAction(new EventHandler<ActionEvent>(){
							@Override
							public void handle(ActionEvent event){
								editColor(mainColor.getValue().toString());
								applyColor();
							}
						});
						sl1.valueProperty().addListener(new ChangeListener<Number>() {
							 @Override
							public void changed(ObservableValue<? extends Number> ov,Number old_val, Number new_val) {
								setSize(sl1.getValue()); 
								ta1.setFont(Font.font("System", size));
								saveSettings();
							 }
						});
						
						//updater
						updateBtn.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event){
								update();
							}
						});
						autoupdateBtn.setOnAction(new EventHandler<ActionEvent>(){
				            @Override
							public void handle(ActionEvent event) {
				            	if(autoUpdate.equals("0")){
				            		setAutoUpdate("1");
				            		saveSettings();
				            	}else{
				            		setAutoUpdate("0");
				            		saveSettings();
				            	}
				            }
						});
						
						settingstrue = true;
					}else{
						anpane.getChildren().removeAll(settingsBox);
						setPath(tfPfad.getText());
						saveSettings();
						settingstrue = false;
					}
				}
			});
			
			//demoBtn clicked debbuging
			demoBtn.setOnAction(new EventHandler<ActionEvent>(){
	            @Override
				public void handle(ActionEvent event) {
	            	/**
	            	 * TODO DBController
	            	 */
//	            	loadData();
	            }
			});
			
			menutrue = true;
		}else{
			anpane.getChildren().removeAll(menuBox);
			menutrue = false;
		}
		if(settingstrue == true){
			anpane.getChildren().removeAll(settingsBox);
			setPath(tfPfad.getText());
			saveSettings();
			settingstrue = false;
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
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText("");
		        	alert.setTitle("Info");
		        	alert.setContentText(errorPlay);
		        	alert.showAndWait();
					e.printStackTrace();
				}
			}
		}else if(SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_MAC_OSX){
			System.out.println("This is Windows or Mac OSX");
			try {
				Desktop.getDesktop().open(new File(getPath()+"\\"+ datPath));
			} catch (IOException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("");
	        	alert.setTitle("Info");
	        	alert.setContentText(errorPlay);
	        	alert.showAndWait();
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	private void openfolderbtnclicked(){
		try {
			Desktop.getDesktop().open(new File(getPath()));	//�ffnet den aktuellen Pfad
		} catch (IOException e) {
			// Auto-generated catch block
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
	
	//"Main" Methode die beim start von der Klasse Main aufgerufen wird, initialiesirung der einzellnen UI-Objekte 
	@SuppressWarnings({ "static-access"})
	public void setMain(Main main) {
		
		loadSettings();
		initTabel();
		
		infoBtn.setPrefWidth(130);
        infoBtn.setPrefHeight(32);
        infoBtn.setFont(Font.font("System", FontWeight.BOLD, 15));
        
        settingsBtn.setPrefWidth(130);
        settingsBtn.setPrefHeight(32);
        settingsBtn.setFont(Font.font("System", FontWeight.BOLD, 15));
        
        demoBtn.setPrefWidth(130);
        demoBtn.setPrefHeight(32);
        demoBtn.setFont(Font.font("System", FontWeight.BOLD, 15));
        demoBtn.setDisable(false);
        
        menubtn.setText("");
        menubtn.setMaxSize(32, 32);
       
        tfPfad.setPrefWidth(250);
        tfPfad.setPromptText("Pfad");
        tfPfad.setText(getPath());

        sl1.setMaxWidth(250);
        sl1.setMin(2);
        sl1.setMax(48);
        sl1.setValue(getSize());
        
        cbLocal.setItems(locals);
        
        updateBtn.setFont(Font.font("System", 12));
        
        directoryBtn.setFont(Font.font("System", 12));
        directoryBtn.setMaxSize(180, 25);
        
        if(autoUpdate.equals("1")){
    		autoupdateBtn.setSelected(true);
    		update();
    	}else{
    		autoupdateBtn.setSelected(false);
    	}
        
        versionlbl.setText("Version: "+version);
        
    	menuBox.setSpacing(2.5);	//Zeilenabstand
    	menuBox.setPadding(new Insets(2.5,0,0,2.5)); // abstand zum Rand
        menuBox.getChildren().addAll(infoBtn, settingsBtn,demoBtn);
    	menuBox.setFillWidth(true);
    	
    	AnchorPane.setTopAnchor(menuBox, 33d);
		AnchorPane.setBottomAnchor(menuBox, 0d);
		
		settingsBox.setStyle("-fx-background-color: #FFFFFF;");
		settingsBox.getChildren().add(settingsan);
		
		settingsan.getChildren().addAll(tfPfad, directoryBtn, mainColor, sizelbl, sl1, cbLocal, updateBtn, aulbl, autoupdateBtn, versionlbl);
		
		settingsan.setTopAnchor(tfPfad, 5d);
		settingsan.setLeftAnchor(tfPfad, 5d);
		
		settingsan.setTopAnchor(directoryBtn, 5d);
		settingsan.setLeftAnchor(directoryBtn, 260d);
		
		settingsan.setTopAnchor(mainColor, 40d);
		settingsan.setLeftAnchor(mainColor, 5d);
		
		settingsan.setTopAnchor(sizelbl, 75d);
		settingsan.setLeftAnchor(sizelbl, 5d);
		
		settingsan.setTopAnchor(sl1, 110d);
		settingsan.setLeftAnchor(sl1, 5d);
		
		settingsan.setTopAnchor(cbLocal, 145d);
		settingsan.setLeftAnchor(cbLocal, 5d);
		
		settingsan.setTopAnchor(updateBtn, 180d);
		settingsan.setLeftAnchor(updateBtn, 5d);
		
		settingsan.setTopAnchor(aulbl, 215d);
		settingsan.setLeftAnchor(aulbl, 5d);
		
		settingsan.setTopAnchor(autoupdateBtn, 230d);
		settingsan.setLeftAnchor(autoupdateBtn, 5d);
		
		settingsan.setTopAnchor(versionlbl, 280d);
		settingsan.setLeftAnchor(versionlbl, 5d);
		
		AnchorPane.setTopAnchor(settingsBox, 34d);
		AnchorPane.setRightAnchor(settingsBox, 0d);
		AnchorPane.setBottomAnchor(settingsBox, 0d);
		AnchorPane.setLeftAnchor(settingsBox, 130d);
    	
    	ta1.setWrapText(true);
    	ta1.setEditable(false);
    	ta1.setFont(Font.font("System", getSize()));
    	
    	image1.setImage(imHF);
    	
        tfsearch.textProperty().addListener(new ChangeListener<String>() {
    	    @SuppressWarnings("unchecked")
			@Override
    	    public void changed(ObservableValue<? extends String> observable,String oldValue, String newValue) {
    	    	int counter = newDaten.size();
    	    	filterData.removeAll(filterData);
    	    	root.getChildren().remove(0,root.getChildren().size());
    	    	
    	    	for(int i = 0; i < counter; i++){
    	    		if(newDaten.get(i).getFilmName().toLowerCase().contains(tfsearch.getText().toLowerCase())){
    	    			filterData.add(newDaten.get(i));
    	    		}
    	    	}
    	    	
    	    	for(int i = 0; i < filterData.size(); i++){
    				root.getChildren().addAll(new TreeItem<uiData>(filterData.get(i)));	//f�gt daten zur Rootnode hinzu
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
	}
	
	//initialisierung der TreeTabelView
	@SuppressWarnings("unchecked")
	private void initTabel(){

	    root.setExpanded(true); 
	        
	    columnRating.setMaxWidth(120);
	    columnName.setMaxWidth(240);
	    columnDatName.setMaxWidth(0);
		
        treeTableViewfilm.setRoot(root);
        treeTableViewfilm.setColumnResizePolicy( TreeTableView.CONSTRAINED_RESIZE_POLICY );
        treeTableViewfilm.setShowRoot(false);
		
        //inhalt in Zelle schreiben
        columnName.setCellValueFactory((CellDataFeatures<uiData, String> p) -> 
        new ReadOnlyStringWrapper(p.getValue().getValue().getFilmName())); 
       
        columnRating.setCellValueFactory((CellDataFeatures<uiData, Double> p) -> 
        new ReadOnlyObjectWrapper<Double>(p.getValue().getValue().getFilmBewertung()));
        
        columnDatName.setCellValueFactory((CellDataFeatures<uiData, String> p) -> 
        new ReadOnlyStringWrapper(p.getValue().getValue().getDataName()));

        treeTableViewfilm.getColumns().addAll(columnName, columnRating, columnDatName);
	
	    //Changelistener f�r TreeTable
	    treeTableViewfilm.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
	        	
			@Override
			public void changed(ObservableValue<?> observable, Object oldVal, Object newVal) {
				// last = selected; //f�r autoplay
				selected = treeTableViewfilm.getSelectionModel().getSelectedIndex(); // holt aktuelles Item
				last = selected - 1;
				next = selected + 1;
				Name = columnName.getCellData(selected); // holt Namen des Aktuelle Items aus der ColumnName
				datPath = columnDatName.getCellData(selected); // holt den aktuellen Datei Pfad aus der ColumnDatName
				ta1.setText(""); // l�scht Text in ta1
				apiAbfrage(Name); // startet die api abfrage
				ta1.positionCaret(0); // setzt die startposition des Cursors in
										// ta1
			}
		});
	    
	    treeTableViewfilm.getColumns().get(2).setVisible(false); //blendet die Column mit den Dateinamen aus (wichtig um sie abzuspielen)
	}
	
	//pr�ft auf Update und f�ht es gegebenenfalls aus
	private void update(){

		System.out.println("check for updates ...");
		try {
			URL url = new URL(versionURL); //URL der Datei mit aktueller Versionsnummer
	        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	        aktVersion = in.readLine();	//schreibt inputstream in String
	        in.close();
		} catch (IOException e1) {
			Alert alert = new Alert(AlertType.ERROR);
        	alert.setTitle("Error");
        	alert.setHeaderText("");
        	alert.setContentText(errorUpdateV);
        	alert.showAndWait();
			e1.printStackTrace();
		}
		System.out.println("Version: "+version+", Update: "+aktVersion);
		
		//vergleicht die Versionsnummern, bei aktversion > version wird ein Update durchgrf�hrt
		int iversion = Integer.parseInt(version.replace(".", ""));
		int iaktVersion = Integer.parseInt(aktVersion.replace(".", ""));
		
		if(iversion >= iaktVersion){
			updateBtn.setText("kein Update verf�gbar");
			System.out.println("kein Update verf�gbar");
		}else{
			updateBtn.setText("Update verf�gbar");
			System.out.println("Update verf�gbar");
		try {
			URL website;
			URL downloadURL = new URL(downloadLink);
			BufferedReader in = new BufferedReader(new InputStreamReader(downloadURL.openStream()));
			updateDataURL = in.readLine();
			website = new URL(updateDataURL);	//Update URL
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());	//�ffnet neuen Stream/Channel
			FileOutputStream fos = new FileOutputStream("ProjectHomeFlix.jar");	//neuer fileoutputstram f�r ProjectHomeFLix.jar
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);	//holt datei von 0 bis max gr��e
			fos.close();	//schlie�t den fos (extrem wichtig!)
			Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar");	//starte neu
			System.exit(0);	//beendet sich selbst
		} catch (IOException e) {
			//Falls ein Fehler auftritt
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
        	alert.setTitle("Error");
        	alert.setHeaderText("");
        	alert.setContentText(errorUpdateD);
        	alert.showAndWait();
			e.printStackTrace();
		}
		}
	}
	
	//l�dt die Daten im angegeben Ordner in newDaten
	public void loadData(){
		if(getPath().equals("")||getPath().equals(null)){
			System.out.println("Kein Pfad angegeben");	//falls der Pfad null oder "" ist
		}else{
		String[] entries = new File(getPath()).list();
		for(int i = 0; i < entries.length; i++){
			String titel = ohneEndung(entries[i]);
			String data = entries[i];
			newDaten.add(new uiData(5.0, titel ,data));
		}
		for(int i = 0; i < newDaten.size(); i++){
			root.getChildren().add(new TreeItem<uiData>(newDaten.get(i)));	//f�gt daten zur Rootnode hinzu
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
		
		menuBox.setStyle(style);
		topVBox.setStyle(style);
		tfsearch.setFocusColor(Color.valueOf(getColor()));
		tfPfad.setFocusColor(Color.valueOf(getColor()));
		
		if(icolor.compareTo(ccolor) == -1){
			settingsBtn.setStyle("-fx-text-fill: WHITE;");
			infoBtn.setStyle("-fx-text-fill: WHITE;");
			demoBtn.setStyle("-fx-text-fill: WHITE;");
			directoryBtn.setStyle(btnStylewhite);
			updateBtn.setStyle(btnStylewhite);
			playbtn.setStyle(btnStylewhite);
			openfolderbtn.setStyle(btnStylewhite);
			returnBtn.setStyle(btnStylewhite);
			forwardBtn.setStyle(btnStylewhite);
			menubtn.setGraphic(menu_icon_white);
		}else{
			settingsBtn.setStyle("-fx-text-fill: BLACK;");
			infoBtn.setStyle("-fx-text-fill: BLACK;");
			demoBtn.setStyle("-fx-text-fill: BLACK;");
			directoryBtn.setStyle(btnStyle);
			updateBtn.setStyle(btnStyle);
			playbtn.setStyle(btnStyle);
			openfolderbtn.setStyle(btnStyle);
			returnBtn.setStyle(btnStyle);
			forwardBtn.setStyle(btnStyle);
			menubtn.setGraphic(menu_icon_black);
		}
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
		settingsBtn.setText(bundle.getString("settings"));
		infoBtn.setText(bundle.getString("info"));
		playbtn.setText(bundle.getString("play"));
		openfolderbtn.setText(bundle.getString("openFolder"));
		updateBtn.setText(bundle.getString("checkUpdates"));
		directoryBtn.setText(bundle.getString("chooseFolder"));
		sizelbl.setText(bundle.getString("fontSize"));
		aulbl.setText(bundle.getString("autoUpdate"));
		versionlbl.setText(bundle.getString("version")+" "+version);
		columnName.setText(bundle.getString("columnName"));
		columnRating.setText(bundle.getString("columnRating"));
		columnDatName.setText(bundle.getString("columnDatName"));
		errorUpdateD = bundle.getString("errorUpdateD");
		errorUpdateV = bundle.getString("errorUpdateV");
		infoText = bundle.getString("version")+" "+version+bundle.getString("infoText");
		linuxBugText = bundle.getString("linuxBug");
		errorPlay = bundle.getString("errorPlay");
		vlcNotInstalled = bundle.getString("vlcNotInstalled");
	}
	
	//speichert die Einstellungen
	public void saveSettings(){
		File configFile = new File("config.xml");	//neue Datei "config.xml"
		try {
			props.setProperty("path", getPath());	//setzt pfad in propselement
			props.setProperty("color", getColor());
			props.setProperty("autoUpdate", getAutoUpdate());
			props.setProperty("size", getSize().toString());
			props.setProperty("local", Integer.toString(getLocal()));
			OutputStream outputStream = new FileOutputStream(configFile);	//neuer outputstream
			props.storeToXML(outputStream, "Project HomeFlix settings");
			outputStream.close();
		} catch (IOException e) {
			System.out.println("An error has occurred!");
			e.printStackTrace();
		}
	}
	
	//l�dt die Einstellungen
	public void loadSettings(){
		File configFile = new File("config.xml");
		try {
			InputStream inputStream = new FileInputStream(configFile);
			props.loadFromXML(inputStream);
			path = props.getProperty("path");	//setzt Propselement in Pfad
			color = props.getProperty("color");
			size = Double.parseDouble(props.getProperty("size"));
			autoUpdate = props.getProperty("autoUpdate");
			local = Integer.parseInt(props.getProperty("local"));
			inputStream.close();
		} catch (IOException e) {
			System.out.println("An error has occurred!");
			e.printStackTrace();
		}
	}
	
	//getter Und setter
	public void setColor(String input){
		this.color = input;
	}
	
	public String getColor(){
		return color;
	}
	
	//entfernt 0x von dem r�ckgabe wert des Colorpickers
	private void editColor(String input){
		StringBuilder sb = new StringBuilder(input);
		sb.delete(0, 2);
		this.color = sb.toString();
		saveSettings();
	}
	
	public void setPath(String input){
		this.path = input;
	}
	
	public String getPath(){
		return path;
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