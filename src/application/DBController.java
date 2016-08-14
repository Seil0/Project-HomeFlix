package application;

import java.sql.Connection;  //für Datenbank
import java.sql.DriverManager; //für Datenbank
import java.sql.PreparedStatement; //für Datenbank
import java.sql.ResultSet; //für Datenbank
import java.sql.SQLException; //für Datenbank
import java.sql.Statement; //für Datenbank
import java.io.File;//für Dateien einlesen

class DBController { 
	
	@SuppressWarnings("unused")
	private MainWindowController mainWindowController;
	private static final DBController dbcontroller = new DBController(); 
	private static Connection connection; 
	private static final String DB_PATH = System.getProperty("user.dir") + "/" + "Homeflix.db"; // der Pfad der Datenbank-Datei
	private String path;
	File f; 
	File file[]; //ArrayList für die Dateien

	static { 
		try { 
			Class.forName("org.sqlite.JDBC"); //Datenbanktreiber
		} catch (ClassNotFoundException e) { 
			System.err.println("Fehler beim Laden des JDBC-Treibers"); 
			e.printStackTrace(); 
		} 
	} 

	public static void main(String input) { 
		DBController datenbank = DBController.getInstance(); //neues Datenbank-Objekt wird erstellt
		datenbank.setPath(input); // Pfad zuweisen
		datenbank.f = new File(datenbank.getPath()); // für Datenbank-Datei einlesen
		datenbank.file = datenbank.f.listFiles(); // für Datenbank-Datei einlesen
		datenbank.verbindeDatenbank(); 
		datenbank.fuelleDatenbank(); 
		//datenbank.defavorisieren("Frozen");
		//datenbank.favorisieren("Frozen");
		//datenbank.ausgebenTitel();
		//System.out.println("Pfad: " + datenbank.getPfad("Frozen"));
		//System.out.println("Bewertung: " + datenbank.getFavStatus("Frozen"));
	} 

	DBController(){ 
	} 

	private static DBController getInstance(){ 
		return dbcontroller; 
	} 
// Die Datenbak wird mit Hilfe des JDBC-Treibers eingebunden
	public void verbindeDatenbank() { 
		try { 
			if (connection != null) 
				return; 
			System.out.println("Erstelle Verbindung zur Datenbank..."); 
			connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH); 
			if (!connection.isClosed()) 
				System.out.println("...Verbindung hergestellt"); 
		} catch (SQLException e) { 
			throw new RuntimeException(e); 
		} 

		Runtime.getRuntime().addShutdownHook(new Thread() { 
			public void run() { 
				try { 
					if (!connection.isClosed() && connection != null) { 
						connection.close(); 
						if (connection.isClosed()) 
							System.out.println("Verbindung getrennt"); 
					} 
				} catch (SQLException e) { 
					e.printStackTrace(); 
				} 
			} 
		}); 
	} 
// Die Dateien werden in die Datenbank geschrieben
	public void fuelleDatenbank() { 

		try { 
			System.out.println("Erstelle Einträge");
			Statement stmt = connection.createStatement(); 
			stmt.executeUpdate("DROP TABLE IF EXISTS filme;"); 
			stmt.executeUpdate("CREATE TABLE filme (titel, pfad, bewertung);"); // Tabelle "filme" und die Spalten "titel", "pfad", "bewertung" erstellen

			PreparedStatement ps = connection.prepareStatement("INSERT INTO filme VALUES (?, ?, ?);"); // SQL Befehl

			//System.out.println(file.length);

			for(int i=0;i!=file.length;i++) // Geht alle Dateien im Verzeichniss durch
			{
				//System.out.println(file[i].getName());
				ps.setString(1, ohneEndung(file[i].getName())); // definiert Name als String in der ersten Spalte
				ps.setString(2,file[i].getName()); // definiert Pfad als String in der zweiten Spalte
				ps.setInt(3, 0); // definiert Bewertung als Integer in der dritten Spalte
				ps.addBatch(); // fügt den Eintrag hinzu
			}

			connection.setAutoCommit(false); 
			ps.executeBatch();  // scheibt alle Einträge in die Datenbank
			connection.setAutoCommit(true); 
			//connection.close(); 
		} catch (SQLException e) { 
			System.err.println("Konnte nicht ausgeführt werden"); 
			e.printStackTrace(); 
		} 
	} 
	
	public void ausgeben(){
		System.out.println("Einträge ausgeben"); 
		try { 
			Statement stmt = connection.createStatement(); 
		mainWindowController = new MainWindowController();
			ResultSet rs = stmt.executeQuery("SELECT * FROM filme;"); 
			while (rs.next()) { 
//				MainWindowController mainWindowController = new MainWindowController();
			} 
			//rs.close(); 
//			mainWindowController.initTabel();


		} catch (SQLException e) { 
			System.err.println("Konnte nicht ausgeführt werden"); 
			e.printStackTrace(); 
		}

		
	}
// Kontroll Methode, ob ein Film wirklich in der Datenbank ist
	public String getTitel(String name){
		try { 
			Statement stmt = connection.createStatement(); 
			ResultSet rs = stmt.executeQuery("SELECT titel, pfad FROM filme WHERE titel = '"+name+"';" ); 
			return rs.getString("titel"); 
		} catch (SQLException e) { 
			System.err.println("Konnte nicht ausgeführt werden"); 
			e.printStackTrace(); 
		} 
		return "Error 404";
	}
// gibt den Pfad eines bestimmten Films
	public String getPfad(String name){
		try { 
			Statement stmt = connection.createStatement(); 
			ResultSet rs = stmt.executeQuery("SELECT titel, pfad FROM filme WHERE titel = '"+name+"';" ); //SQL Befehl
			return rs.getString("pfad"); 
		} catch (SQLException e) { 
			System.err.println("Konnte nicht ausgeführt werden"); 
			e.printStackTrace(); 
		} 
		return "Error 404";
	}
// gibt die Favorisierung eines bestimmten Films
	public boolean getFavStatus(String name){
		try { 
			Statement stmt = connection.createStatement(); 
			ResultSet rs = stmt.executeQuery("SELECT titel, bewertung FROM filme WHERE titel = '"+name+"';" ); //SQL Befehl
		System.out.println(rs.getInt("bewertung"));
			if((rs.getInt("bewertung")) == 1){
				return true;
			}
			else{
				return false;
			}
		} catch (SQLException e) { 
			System.err.println("Konnte nicht ausgeführt werden"); 
			e.printStackTrace(); 
			return false;
		} 
	}
// setzt die Defavorisierung eines bestimmten Films
	public void defavorisieren(String name){
		System.out.println("setze Bewertung");
		try { 
			Statement stmt = connection.createStatement(); 
			String sql = ("UPDATE filme SET bewertung=0 WHERE titel='"+name+"';"); //SQL Befehl
			stmt.executeUpdate(sql);
			connection.setAutoCommit(false);  
			connection.setAutoCommit(true);  
		} catch (SQLException e) { 
			System.err.println("Konnte nicht ausgeführt werden"); 
			e.printStackTrace(); 
		} 
	}
// setzt die Favorisierung eines bestimmten Films
	public void favorisieren(String name){
		System.out.println("setze Bewertung");
		try { 
			Statement stmt = connection.createStatement(); 
			String sql = ("UPDATE filme SET bewertung=1 WHERE titel='"+name+"';"); //SQL Befehl
			stmt.executeUpdate(sql);
			connection.setAutoCommit(false);  
			connection.setAutoCommit(true);  
		} catch (SQLException e) { 
			System.err.println("Konnte nicht ausgeführt werden"); 
			e.printStackTrace(); 
		}
	}
//entfernt die Endung
	private static String ohneEndung (String str) {

		if (str == null) return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1) return str;
		return str.substring(0, pos);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path.replace("\\", "\\\\");
	}
}
