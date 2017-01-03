/**
 * DBController for Project HomeFlix
 * 
 * connection is in manual commit!
 */

package application;

import java.sql.Connection; //für Datenbank
import java.sql.DriverManager; //für Datenbank
import java.sql.PreparedStatement; //für Datenbank
import java.sql.ResultSet; //für Datenbank
import java.sql.SQLException; //für Datenbank
import java.sql.Statement; //für Datenbank

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DBController {

	public DBController(MainWindowController m) {
		mainWindowController = m;
	}

	private MainWindowController mainWindowController;
	private String DB_PATH = System.getProperty("user.home") + "\\Documents\\HomeFlix" + "\\" + "Homeflix.db"; // der Pfad der Datenbank-Datei
	Connection connection = null;

	public void main() {
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
			// Statement statement = connection.createStatement();
			// statement.setQueryTimeout(30); // set timeout to 30 sec. TODO don't know wath to do with this

			connection.setAutoCommit(false);	//Autocommit to false -> manual commit is active!
			fuelleDatenbank();
//			ausgeben();
//			getFavStatus("House of Cards");
//			favorisieren("House of Cards");
//			getFavStatus("House of Cards");
//			defavorisieren("House of Cards");
//			getFavStatus("House of Cards");

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
	
	public void fuelleDatenbank() { 

		try { 
			System.out.println("Erstelle Einträge local");
			Statement stmt = connection.createStatement();
			Statement stmtS = connection.createStatement(); 
			stmt.executeUpdate("drop table if exists film_local");
			stmtS.executeUpdate("drop table if exists film_streaming"); 
			stmt.executeUpdate("create table film_local (rating, titel, streamUrl)"); // Tabelle "filme" und die Spalten "titel", "pfad", "bewertung" erstellen
			stmtS.executeUpdate("create table film_streaming (year, season, episode, rating, resolution, titel, streamUrl)"); // Tabelle "filme" und die Spalten "titel", "pfad", "bewertung" erstellen


			PreparedStatement ps = connection.prepareStatement("insert into film_local values (?, ?, ?)"); // SQL Befehl
			PreparedStatement psS = connection.prepareStatement("insert into film_streaming values (?, ?, ?, ?, ?, ?, ?)"); // SQL Befehl

			String[] entries = new File(mainWindowController.getPath()).list();

			for(int i=0;i!=entries.length;i++) // Geht alle Dateien im Verzeichniss durch
			{
				//System.out.println(file[i].getName());
				ps.setInt(1, 0); // definiert Bewertung als Integer in der dritten Spalte
				ps.setString(2, ohneEndung(entries[i])); // definiert Name als String in der ersten Spalte
				ps.setString(3,entries[i]); // definiert Pfad als String in der zweiten Spalte
				ps.addBatch(); // fügt den Eintrag hinzu
			}
			
			
			System.out.println("Erstelle Einträge streaming \n");
			if(mainWindowController.getStreamingPath().equals("")||mainWindowController.getStreamingPath().equals(null)){
				System.out.println("Kein Pfad angegeben");	//falls der Pfad null oder "" ist
			}else{
				for(int i=0; i< mainWindowController.streamingData.size(); i++){
				String fileName = mainWindowController.getStreamingPath()+"/"+mainWindowController.streamingData.get(i).getStreamUrl();
				try {
					JsonObject object = Json.parse(new FileReader(fileName)).asObject();
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
					//Auto-generated catch block
					e.printStackTrace();
				}
			}
			}
//			connection.setAutoCommit(false); 
			ps.executeBatch();  // scheibt alle Einträge in die Datenbank
			psS.executeBatch();			
			connection.commit();
			ps.close();
			psS.close();
			//connection.close(); 
		} catch (SQLException ea) { 
			System.err.println("Konnte nicht ausgeführt werden"); 
			ea.printStackTrace(); 
		} 
	}
	
	public void ausgeben(){
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
		
		ResultSet rsS = stmt.executeQuery("SELECT * FROM film_streaming;"); 
		while (rsS.next()) { 
			System.out.println(rsS.getString(1));
			System.out.println(rsS.getString(2));
			System.out.println(rsS.getString(3));
			System.out.println(rsS.getString(4));
			System.out.println(rsS.getString(5));
			System.out.println(rsS.getString(6));
			System.out.println(rsS.getString(7)+"\n");
		}
		stmt.close();
		rsS.close(); 

	} catch (SQLException e) { 
		System.err.println("Konnte nicht ausgeführt werden"); 
		e.printStackTrace(); 
	}
    mainWindowController.ta1.setText("Hallo");
}
	
//gibt die Favorisierung eines bestimmten Films
	public void getFavStatus(String name){
		try{
			Statement stmta = connection.createStatement(); 
			ResultSet rs = stmta.executeQuery("SELECT titel, rating FROM film_local WHERE titel = '"+name+"';" ); //SQL Befehl
			System.out.println("local:"+rs.getString("rating"));
			stmta.close();
			rs.close();
		}catch(SQLException e){
			
			try {
				System.out.println("streaming");
				Statement stmtSa = connection.createStatement(); 
				ResultSet rsS = stmtSa.executeQuery("SELECT titel, rating FROM film_streaming WHERE titel = '"+name+"';" );
				System.out.println("streaming:"+rsS.getString("rating"));
				stmtSa.close();
				rsS.close();
			} catch (SQLException e1) {
//				System.out.println("Ups! an error occured!");
				e1.printStackTrace();
			}
			
			
//			System.out.println("Ups! an error occured!");
//			e.printStackTrace();
		}
		
	}
//setzt die Defavorisierung eines bestimmten Films
	public void defavorisieren(String name){
		System.out.println("defavorisieren ...");		
		try{
			Statement stmt = connection.createStatement(); 
			stmt.executeUpdate("UPDATE film_local SET rating=0 WHERE titel='"+name+"';");
			connection.commit();
		}catch(SQLException e){
			System.out.println("Ups! an error occured!");
			e.printStackTrace();
		}
		try {
			Statement stmtS = connection.createStatement(); 
			stmtS.executeUpdate("UPDATE film_streaming SET rating=0 WHERE titel='"+name+"';");
			connection.commit();
		} catch (SQLException e1) {
			System.out.println("Ups! an error occured!");
			e1.printStackTrace();
		}
	}
//setzt die Favorisierung eines bestimmten Films
	public void favorisieren(String name){
		System.out.println("favorisieren ...");
		try{
			Statement stmt = connection.createStatement(); 
			stmt.executeUpdate("UPDATE film_local SET rating=1 WHERE titel='"+name+"';");
			connection.commit();
		}catch(SQLException e){
			System.out.println("Ups! an error occured!");
			e.printStackTrace();
		}
		try {
			Statement stmtS = connection.createStatement(); 
			stmtS.executeUpdate("UPDATE film_streaming SET rating=1 WHERE titel='"+name+"';");
			connection.commit();
		} catch (SQLException e1) {
			System.out.println("Ups! an error occured!");
			e1.printStackTrace();
		}
	}
	
//entfernt die Endung
	private String ohneEndung (String str) {

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
