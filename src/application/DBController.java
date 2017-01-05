/**
 * DBController for Project HomeFlix
 * connection is in manual commit!
 * TODO überprüfen ob neue filme hinzu gekommen sind
 */

package application;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class DBController {

	public DBController(MainWindowController m) {
		mainWindowController = m;
	}

	private MainWindowController mainWindowController;
	private String DB_PATH = System.getProperty("user.home") + "\\Documents\\HomeFlix" + "\\" + "Homeflix.db"; // der Pfad der Datenbank-Datei
	private List<String> filmsdb = new ArrayList<String>();
	private List<String> filmsAll = new ArrayList<String>();
	private List<String> filmsDir = new ArrayList<String>();
	private List<String> filmsStream = new ArrayList<String>();
	private List<Integer> counter = new ArrayList<Integer>();
	Connection connection = null;

	public void main() {
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
			// Statement statement = connection.createStatement();
			// statement.setQueryTimeout(30); // set timeout to 30 sec. TODO don't know wath to do with this

			connection.setAutoCommit(false);	//Autocommit to false -> manual commit is active
//			fuelleDatenbank();
		} catch (SQLException e) {
			// if the error message is "out of memory", it probably means no database file is found
			System.err.println(e.getMessage());
		}
//		finally {
//			try {
//				if (connection != null)
//					connection.close();
//			} catch (SQLException e) {
//				// connection close failed.
//				System.err.println(e);
//			}
//		}
	}
	
	void createDatabase() { 
		System.out.println("<=====starting loading sql=====>");
		
		PreparedStatement ps;
		PreparedStatement psS;	
		String[] entries = new File(mainWindowController.getPath()).list();
	
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("create table if not exists film_local (rating, titel, streamUrl)");
			stmt.executeUpdate("create table if not exists film_streaming (year, season, episode, rating, resolution, titel, streamUrl)");
			stmt.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} 
			
			try { 
				Statement stmt = connection.createStatement(); 
				ResultSet rs = stmt.executeQuery("SELECT * FROM film_local"); 
				while (rs.next()) { 
					filmsdb.add(rs.getString(2));
				}
				stmt.close();
				rs.close();

				rs = stmt.executeQuery("SELECT * FROM film_streaming;"); 
				while (rs.next()) { 
					filmsdb.add(rs.getString(6));
				}
				stmt.close();
				rs.close();
			}catch (SQLException ea){
				//TODO
			}
				
			System.out.println("filme in db: "+filmsdb.size());
				
			for(int i=0;i!=entries.length;i++){
				filmsDir.add(cutOffEnd(entries[i]));
			}
				
			for(int v=0; v< mainWindowController.streamingData.size(); v++){
				String fileName = mainWindowController.getStreamingPath()+"/"+mainWindowController.streamingData.get(v).getStreamUrl();
				try {
					JsonObject object = Json.parse(new FileReader(fileName)).asObject();
					JsonArray items = object.get("entries").asArray();
					for (JsonValue item : items) {
						filmsStream.add(item.asObject().getString("titel",""));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				}		
			filmsAll.addAll(filmsDir);
			filmsAll.addAll(filmsStream);
			System.out.println("films in directory: "+filmsAll.size());

				
				if(filmsdb.size() == 0){
					System.out.println("creating entries ...");
					
					try{
						ps = connection.prepareStatement("insert into film_local values (?, ?, ?)");
						psS = connection.prepareStatement("insert into film_streaming values (?, ?, ?, ?, ?, ?, ?)");
					
						for(int j=0;j!=entries.length;j++) // Geht alle Dateien im Verzeichniss durch
						{
							ps.setInt(1, 0); // definiert Bewertung als Integer in der dritten Spalte
							ps.setString(2, cutOffEnd(entries[j])); // definiert Name als String in der ersten Spalte
							ps.setString(3,entries[j]); // definiert Pfad als String in der zweiten Spalte
							ps.addBatch(); // fügt den Eintrag hinzu
						}
					
						if(mainWindowController.getStreamingPath().equals("")||mainWindowController.getStreamingPath().equals(null)){
							System.out.println("Kein Pfad angegeben");	//falls der Pfad null oder "" ist
						}else{
							for(int i=0; i< mainWindowController.streamingData.size(); i++){
							String fileNamea = mainWindowController.getStreamingPath()+"/"+mainWindowController.streamingData.get(i).getStreamUrl();
							try {
								JsonObject object = Json.parse(new FileReader(fileNamea)).asObject();
								JsonArray items = object.get("entries").asArray();
								for (JsonValue item : items) {
									psS.setInt(1, item.asObject().getInt("year", 0));
									psS.setInt(2, item.asObject().getInt("season", 0));
									psS.setInt(3, item.asObject().getInt("episode", 0));
									psS.setInt(4, 0);
									psS.setString(5, item.asObject().getString("resolution", ""));
									psS.setString(6, item.asObject().getString("titel",""));
									psS.setString(7, item.asObject().getString("streamUrl", ""));
									psS.addBatch(); // fügt den Eintrag hinzu
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						}
						ps.executeBatch();  // scheibt alle Einträge in die Datenbank
						psS.executeBatch();			
						connection.commit();
						ps.close();
						psS.close();
					}catch (SQLException ea) { 
						System.err.println("Konnte nicht ausgeführt werden"); 
						ea.printStackTrace(); 
					}
				}else if(filmsdb.size() == filmsAll.size()){
					for(int i=0;i<filmsAll.size();i++){
						if(filmsAll.contains(filmsdb.get(i))){
						}else{		//calls updateDB if there is a different name between db and dir
							int l=0;
							try {
								Statement stmt = connection.createStatement(); 
								ResultSet rs = stmt.executeQuery("SELECT * FROM film_local");
								while (rs.next()) { 
									if(filmsDir.contains(rs.getString(2))){
										l++;
										System.out.println("gleich L"+l);
									}else{
										l++;
										counter.add(l);
										System.out.println("ungleich L");
									}
								}
								stmt.close();
								rs.close();
								
								rs = stmt.executeQuery("SELECT * FROM film_streaming;"); 
								while (rs.next()) { 
									if(filmsStream.contains(rs.getString(6))){
										l++;
										System.out.println("gleich S"+l);
									}else{
										l++;
										counter.add(l);
										System.out.println("ungleich S");
									}
								}
								stmt.close();
								rs.close();
								System.out.println(counter);
								updateDB();
							} catch (SQLException e1) {
								e1.printStackTrace();
							} 
						}
					}
				}else{
					addEntry();	//TODO calls updateDB if there is a different size between db and dir
				}
	}
	
	//loading data from database to mainWindowController 
	void loadData(){
		System.out.println("loading data to mwc ..."); 
		try { 
			//load local Data
			Statement stmt = connection.createStatement(); 
			ResultSet rs = stmt.executeQuery("SELECT * FROM film_local"); 
			while (rs.next()) {
				mainWindowController.newDaten.add(new streamUiData(1, 1, 1, rs.getDouble(1), "1", rs.getString(2), rs.getString(3)));
			}
			stmt.close();
			rs.close();
			
			//load streaming Data TODO check if there are streaming data before loading -> maybe there is an issue now
			rs = stmt.executeQuery("SELECT * FROM film_streaming;"); 
			while (rs.next()) {
				mainWindowController.streamData.add(new streamUiData(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getDouble(4), rs.getString(5), rs.getString(6), rs.getString(7)));
			}
			stmt.close();
			rs.close(); 		
		} catch (SQLException e) { 
			System.err.println("Ups! an error occured!"); 
			e.printStackTrace(); 
		}
		System.out.println("<=====finished loading sql=====>"); 
	}
	
	//refreshs the data in mainWindowController.newDaten and mainWindowController.streamData
	void refresh(String name,int i) throws SQLException{
		System.out.println("refresh ...");
		Statement stmt;
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM film_local WHERE titel = '"+name+"';" );
			mainWindowController.newDaten.set(i, new streamUiData(1, 1, 1, rs.getDouble(1), "1", rs.getString(2), rs.getString(3)));
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			try {
				stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM film_streaming WHERE titel = '"+name+"';" );
				mainWindowController.streamData.set(i,new streamUiData(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getDouble(4), rs.getString(5), rs.getString(6), rs.getString(7)));
				stmt.close();
				rs.close();
			} catch (SQLException e1) {
				System.err.println("Ups! an error occured!"); 
				e1.printStackTrace(); 
			} 
		} 
	}
	
	private void updateDB(){
		System.out.println("updating DB ...");
		for(int i=0; i<counter.size();i++){
			String ending = "";
			try{
				
				Statement stmt = connection.createStatement(); 
				ResultSet rs = stmt.executeQuery("SELECT streamUrl FROM film_local WHERE titel='"+filmsdb.get(counter.get(i)-1)+"';"); 
				while (rs.next()) {
					ending=rs.getString(1);
					int pos = ending.lastIndexOf(".");
					ending = ending.substring(pos);
					System.out.println(pos);
					System.out.println(ending);
				}
				
				stmt.executeUpdate("UPDATE film_local SET titel='"+filmsAll.get(counter.get(i)-1)+"', streamUrl='"+filmsAll.get(counter.get(i)-1)+ending+"' WHERE titel='"+filmsdb.get(counter.get(i)-1)+"';");
				connection.commit();
				stmt.close();
			}catch(SQLException e){
				System.out.println("Ups! an error occured!");
				e.printStackTrace();
			}
		}
	}
	
	private void addEntry(){
		System.out.println("adding entry to DB ...");
	}
	
	void ausgeben(){
	System.out.println("Einträge ausgeben ... \n"); 
	try { 
		Statement stmt = connection.createStatement(); 
		ResultSet rs = stmt.executeQuery("SELECT * FROM film_local"); 
		while (rs.next()) { 
			System.out.println(rs.getString(1));
			System.out.println(rs.getString(2));
			System.out.println(rs.getString(3)+"\n");
		}
		stmt.close();
		rs.close();
		
		System.out.println("Streaming Entries: \n");
		
		rs = stmt.executeQuery("SELECT * FROM film_streaming;"); 
		while (rs.next()) { 
			System.out.println(rs.getString(1));
			System.out.println(rs.getString(2));
			System.out.println(rs.getString(3));
			System.out.println(rs.getString(4));
			System.out.println(rs.getString(5));
			System.out.println(rs.getString(6));
			System.out.println(rs.getString(7)+"\n");
		}
		stmt.close();
		rs.close(); 

	} catch (SQLException e) { 
		System.err.println("Konnte nicht ausgeführt werden"); 
		e.printStackTrace(); 
	}
}
	
	//gibt die Favorisierung eines bestimmten Films
	void getFavStatus(String name){
		try{
			Statement stmt = connection.createStatement(); 
			ResultSet rs = stmt.executeQuery("SELECT titel, rating FROM film_local WHERE titel = '"+name+"';" ); //SQL Befehl
			System.out.println("local:"+rs.getString("rating")+", "+rs.getString("titel"));
			stmt.close();
			rs.close();
		}catch(SQLException e){
			try {
				Statement stmtS = connection.createStatement(); 
				ResultSet rsS = stmtS.executeQuery("SELECT titel, rating FROM film_streaming WHERE titel = '"+name+"';" );
				System.out.println("streaming:"+rsS.getString("rating")+", "+rsS.getString("titel"));
				stmtS.close();
				rsS.close();
			} catch (SQLException e1) {
				System.out.println("Ups! an error occured!");
				e1.printStackTrace();
			}
		}
		
	}
	//setzt die Defavorisierung eines bestimmten Films
	void dislike(String name){
		System.out.println("defavorisieren ...");		
		try{
			Statement stmt = connection.createStatement(); 
			stmt.executeUpdate("UPDATE film_local SET rating=0 WHERE titel='"+name+"';");
			connection.commit();
			stmt.close();
		}catch(SQLException e){
			System.out.println("Ups! an error occured!");
			e.printStackTrace();
		}
		try {
			Statement stmt = connection.createStatement(); 
			stmt.executeUpdate("UPDATE film_streaming SET rating=0 WHERE titel='"+name+"';");
			connection.commit();
			stmt.close();
		} catch (SQLException e1) {
			System.out.println("Ups! an error occured!");
			e1.printStackTrace();
		}
	}
	//setzt die Favorisierung eines bestimmten Films
	void like(String name){
		System.out.println("favorisieren ...");
		try{
			Statement stmt = connection.createStatement(); 
			stmt.executeUpdate("UPDATE film_local SET rating=1 WHERE titel='"+name+"';");
			connection.commit();
			stmt.close();
		}catch(SQLException e){
			System.out.println("Ups! an error occured!");
			e.printStackTrace();
		}
		try {
			Statement stmt = connection.createStatement(); 
			stmt.executeUpdate("UPDATE film_streaming SET rating=1 WHERE titel='"+name+"';");
			connection.commit();
			stmt.close();
		} catch (SQLException e1) {
			System.out.println("Ups! an error occured!");
			e1.printStackTrace();
		}
	}
	
//entfernt die Endung
	private String cutOffEnd (String str) {

		if (str == null) return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1) return str;
		return str.substring(0, pos);
	}
	
}

//	private static final DBController dbcontroller = new DBController(); 
//	private static Connection connection; 
//	private static final String DB_PATH = System.getProperty("user.home") + "\\Documents\\HomeFlix" + "\\" + "Homeflix.db"; // der Pfad der Datenbank-Datei
//	private String path;
//	File f; 
//	File file[]; //ArrayList für die Dateien
//	
//	public DBController(MainWindowController m){
//		mainWindowController=m;
//	}
//
//	private MainWindowController mainWindowController;
//	
//	static { 
//		try { 
//			Class.forName("org.sqlite.JDBC"); //Datenbanktreiber
//		} catch (ClassNotFoundException e) { 
//			System.err.println("Fehler beim Laden des JDBC-Treibers"); 
//			e.printStackTrace(); 
//		} 
//	} 
//
//	public static void main(String input) { 
//		DBController datenbank = DBController.getInstance(); //neues Datenbank-Objekt wird erstellt
//		datenbank.setPath(input); // Pfad zuweisen
//		datenbank.f = new File(datenbank.getPath()); // für Datenbank-Datei einlesen
//		datenbank.file = datenbank.f.listFiles(); // für Datenbank-Datei einlesen
//		datenbank.verbindeDatenbank(); 
//		datenbank.fuelleDatenbank(); 
//		//datenbank.defavorisieren("Frozen");
//		//datenbank.favorisieren("Frozen");
//		//datenbank.ausgebenTitel();
//		//System.out.println("Pfad: " + datenbank.getPfad("Frozen"));
//		//System.out.println("Bewertung: " + datenbank.getFavStatus("Frozen"));
//	} 
//
//	DBController(){ 
//	} 
//
//	private static DBController getInstance(){ 
//		return dbcontroller; 
//	} 
//// Die Datenbak wird mit Hilfe des JDBC-Treibers eingebunden
//	public void verbindeDatenbank() { 
//		try { 
//			if (connection != null) 
//				return; 
//			System.out.println("Erstelle Verbindung zur Datenbank..."); 
//			connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH); 
//			if (!connection.isClosed()) 
//				System.out.println("...Verbindung hergestellt"); 
//		} catch (SQLException e) { 
//			throw new RuntimeException(e); 
//		} 
//
//		Runtime.getRuntime().addShutdownHook(new Thread() { 
//			public void run() { 
//				try { 
//					if (!connection.isClosed() && connection != null) { 
//						connection.close(); 
//						if (connection.isClosed()) 
//							System.out.println("Verbindung getrennt"); 
//					} 
//				} catch (SQLException e) { 
//					e.printStackTrace(); 
//				} 
//			} 
//		}); 
//	} 
//// Die Dateien werden in die Datenbank geschrieben
//	public void fuelleDatenbank() { 
//
//		try { 
//			System.out.println("Erstelle Einträge local");
//			Statement stmt = connection.createStatement(); 
//			stmt.executeUpdate("DROP TABLE IF EXISTS film_local;"); 
//			stmt.executeUpdate("CREATE TABLE film_local (rating, titel, streamUrl);"); // Tabelle "filme" und die Spalten "titel", "pfad", "bewertung" erstellen
//
//			PreparedStatement ps = connection.prepareStatement("INSERT INTO film_local VALUES (?, ?, ?);"); // SQL Befehl
//			PreparedStatement psS = connection.prepareStatement("INSERT INTO film_streaming VALUES (?, ?, ?, ?, ?, ?, ?);"); // SQL Befehl
//
//			System.out.println("Size: "+file.length);
//
//			for(int i=0;i!=file.length;i++) // Geht alle Dateien im Verzeichniss durch
//			{
//				//System.out.println(file[i].getName());
//				ps.setInt(1, 0); // definiert Bewertung als Integer in der dritten Spalte
//				ps.setString(2, ohneEndung(file[i].getName())); // definiert Name als String in der ersten Spalte
//				ps.setString(3,file[i].getName()); // definiert Pfad als String in der zweiten Spalte
//				ps.addBatch(); // fügt den Eintrag hinzu
//			}
//			
//			
//			System.out.println("Erstelle Einträge streaming");
//			Statement stmtS = connection.createStatement(); 
//			stmtS.executeUpdate("DROP TABLE IF EXISTS film_streaming;"); 
//			stmtS.executeUpdate("CREATE TABLE film_streaming (year, season, episode, rating, resolution, titel, streamUrl);"); // Tabelle "filme" und die Spalten "titel", "pfad", "bewertung" erstellen
//
//			System.out.println(mainWindowController.getStreamingPath());
//			if(mainWindowController.getStreamingPath().equals("")||mainWindowController.getStreamingPath().equals(null)){
//				System.out.println("Kein Pfad angegeben");	//falls der Pfad null oder "" ist
//			}else{
//				for(int i=0; i< mainWindowController.streamingData.size(); i++){
//				String fileName = mainWindowController.getStreamingPath()+"/"+mainWindowController.streamingData.get(i).getStreamUrl();
//				try {
//					JsonObject object = Json.parse(new FileReader(fileName)).asObject();
//					JsonArray items = object.get("entries").asArray();
//					for (JsonValue item : items) {
//						psS.setInt(1, item.asObject().getInt("year", 0));
//						psS.setInt(2, item.asObject().getInt("season", 0));
//						psS.setInt(3, item.asObject().getInt("episode", 0));
//						psS.setInt(4, 0);
//						psS.setString(5, item.asObject().getString("resolution", ""));
//						psS.setString(6, item.asObject().getString("titel",""));
//						psS.setString(7, item.asObject().getString("streamUrl", ""));
//						psS.addBatch(); // fügt den Eintrag hinzu
//					}
//				} catch (IOException e) {
//					//Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			}
//			connection.setAutoCommit(false); 
//			ps.executeBatch();  // scheibt alle Einträge in die Datenbank
//			psS.executeBatch();
//			connection.setAutoCommit(true); 
//			//connection.close(); 
//		} catch (SQLException e) { 
//			System.err.println("Konnte nicht ausgeführt werden"); 
//			e.printStackTrace(); 
//		} 
//	} 
//	
//	public void ausgeben(){
//		System.out.println("Einträge ausgeben ... \n"); 
//		try { 
//			Statement stmt = connection.createStatement(); 
//		mainWindowController = new MainWindowController();
//			ResultSet rs = stmt.executeQuery("SELECT * FROM film_local;"); 
//			while (rs.next()) { 
//				System.out.println(rs.getString(1));
//				System.out.println(rs.getString(2));
//				System.out.println(rs.getString(3)+"\n");
//			} 
//			rs.close(); 
//			
//			ResultSet rsS = stmt.executeQuery("SELECT * FROM film_streaming;"); 
//			while (rsS.next()) { 
//				System.out.println(rsS.getString(1));
//				System.out.println(rsS.getString(2));
//				System.out.println(rsS.getString(3)+"\n");
//			} 
//			rsS.close(); 
////			mainWindowController.initTabel();
//
//
//		} catch (SQLException e) { 
//			System.err.println("Konnte nicht ausgeführt werden"); 
//			e.printStackTrace(); 
//		}
//
//		
//	}
//// Kontroll Methode, ob ein Film wirklich in der Datenbank ist
//	public String getTitel(String name){
//		try { 
//			Statement stmt = connection.createStatement(); 
//			ResultSet rs = stmt.executeQuery("SELECT titel, pfad FROM filme WHERE titel = '"+name+"';" ); 
//			return rs.getString("titel"); 
//		} catch (SQLException e) { 
//			System.err.println("Konnte nicht ausgeführt werden"); 
//			e.printStackTrace(); 
//		} 
//		return "Error 404";
//	}
//// gibt den Pfad eines bestimmten Films
//	public String getPfad(String name){
//		try { 
//			Statement stmt = connection.createStatement(); 
//			ResultSet rs = stmt.executeQuery("SELECT titel, pfad FROM filme WHERE titel = '"+name+"';" ); //SQL Befehl
//			return rs.getString("pfad"); 
//		} catch (SQLException e) { 
//			System.err.println("Konnte nicht ausgeführt werden"); 
//			e.printStackTrace(); 
//		} 
//		return "Error 404";
//	}
//// gibt die Favorisierung eines bestimmten Films
//	public boolean getFavStatus(String name){
//		try { 
//			Statement stmt = connection.createStatement(); 
//			ResultSet rs = stmt.executeQuery("SELECT titel, bewertung FROM filme WHERE titel = '"+name+"';" ); //SQL Befehl
//		System.out.println(rs.getInt("bewertung"));
//			if((rs.getInt("bewertung")) == 1){
//				return true;
//			}
//			else{
//				return false;
//			}
//		} catch (SQLException e) { 
//			System.err.println("Konnte nicht ausgeführt werden"); 
//			e.printStackTrace(); 
//			return false;
//		} 
//	}
//// setzt die Defavorisierung eines bestimmten Films
//	public void defavorisieren(String name){
//		System.out.println("setze Bewertung");
//		try { 
//			Statement stmt = connection.createStatement(); 
//			String sql = ("UPDATE filme SET bewertung=0 WHERE titel='"+name+"';"); //SQL Befehl
//			stmt.executeUpdate(sql);
//			connection.setAutoCommit(false);  
//			connection.setAutoCommit(true);  
//		} catch (SQLException e) { 
//			System.err.println("Konnte nicht ausgeführt werden"); 
//			e.printStackTrace(); 
//		} 
//	}
//// setzt die Favorisierung eines bestimmten Films
//	public void favorisieren(String name){
//		System.out.println("setze Bewertung");
//		try { 
//			Statement stmt = connection.createStatement(); 
//			String sql = ("UPDATE filme SET bewertung=1 WHERE titel='"+name+"';"); //SQL Befehl
//			stmt.executeUpdate(sql);
//			connection.setAutoCommit(false);  
//			connection.setAutoCommit(true);  
//		} catch (SQLException e) { 
//			System.err.println("Konnte nicht ausgeführt werden"); 
//			e.printStackTrace(); 
//		}
//	}
////entfernt die Endung
//	private static String ohneEndung (String str) {
//
//		if (str == null) return null;
//		int pos = str.lastIndexOf(".");
//		if (pos == -1) return str;
//		return str.substring(0, pos);
//	}
//
//	public String getPath() {
//		return path;
//	}
//
//	public void setPath(String path) {
//		this.path = path.replace("\\", "\\\\");
//	}
//}
