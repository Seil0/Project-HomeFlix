/**
 * DBController for Project HomeFlix
 * connection is in manual commit!
 * TODO arraylists not string -> streamUIData
 */

package application;

import java.io.File;
import java.io.FileNotFoundException;
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

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DBController {

	public DBController(MainWindowController m) {
		mainWindowController = m;
	}

	private MainWindowController mainWindowController;
	private String DB_PATH = System.getProperty("user.home") + "\\Documents\\HomeFlix" + "\\" + "Homeflix.db"; //path to database file
	private Image favorite_black = new Image("recources/icons/ic_favorite_black_18dp_1x.png");
	private Image favorite_border_black = new Image("recources/icons/ic_favorite_border_black_18dp_1x.png");
	private List<String> filmsdbAll = new ArrayList<String>();
	private List<String> filmsdbLocal = new ArrayList<String>();
	private List<String> filmsdbStream = new ArrayList<String>();
	private List<String> filmsdbStreamURL = new ArrayList<String>();
	private List<String> filmsAll = new ArrayList<String>();
	private List<String> filmsDir = new ArrayList<String>();
	private List<String> filmsStream = new ArrayList<String>();
	private List<String> filmsStreamURL = new ArrayList<String>();	
	private List<String> filmsStreamData = new ArrayList<String>();
	Connection connection = null;

	public void main() {
		if (System.getProperty("os.name").equals("Linux")) {
			DB_PATH = System.getProperty("user.home") + "/HomeFlix/Homeflix.db";
		}else{
			DB_PATH = System.getProperty("user.home") + "\\Documents\\HomeFlix" + "\\" + "Homeflix.db";
		}
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
			// Statement statement = connection.createStatement();
			// statement.setQueryTimeout(30); // set timeout to 30 sec. TODO don't know what to do with this

			connection.setAutoCommit(false);	//AutoCommit to false -> manual commit is active
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
		System.out.println("<==========starting loading sql==========>");
		
		PreparedStatement ps;
		PreparedStatement psS;	
	
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("create table if not exists film_local (rating, titel, streamUrl, favIcon)");
			stmt.executeUpdate("create table if not exists film_streaming (year, season, episode, rating, resolution, titel, streamUrl, favIcon)");
			stmt.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		} 
			
			try { 
				Statement stmt = connection.createStatement(); 
				ResultSet rs = stmt.executeQuery("SELECT * FROM film_local"); 
				while (rs.next()) { 
					filmsdbLocal.add(rs.getString(2));
				}
				stmt.close();
				rs.close();

				rs = stmt.executeQuery("SELECT * FROM film_streaming;"); 
				while (rs.next()) { 
					filmsdbStream.add(rs.getString(6));
					filmsdbStreamURL.add(rs.getString(7));
				}
				stmt.close();
				rs.close();
			}catch (SQLException ea){
				System.err.println("Ups! an error occured!"); 
				ea.printStackTrace();
			}
			
			String[] entries = new File(mainWindowController.getPath()).list();
			if(mainWindowController.getPath().equals("") || mainWindowController.getPath() == null){
				System.out.println("Kein Pfad angegeben");	//if path == null or ""
			}else{
				System.out.println(entries.length);
				for(int i=0;i!=entries.length;i++){
					filmsDir.add(cutOffEnd(entries[i]));
				}
			}
				
			for(int v=0; v< mainWindowController.streamingData.size(); v++){
				String fileName = mainWindowController.getStreamingPath()+"/"+mainWindowController.streamingData.get(v).getStreamUrl();
				try {
					JsonObject object = Json.parse(new FileReader(fileName)).asObject();
					JsonArray items = object.get("entries").asArray();
					for (JsonValue item : items) {
						filmsStream.add(item.asObject().getString("titel",""));
						filmsStreamURL.add(item.asObject().getString("streamUrl",""));
						filmsStreamData.add(fileName);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				}		
			filmsAll.addAll(filmsDir);
			filmsAll.addAll(filmsStream);
			filmsdbAll.addAll(filmsdbLocal);
			filmsdbAll.addAll(filmsdbStream);
			System.out.println("films in directory: "+filmsAll.size());
			System.out.println("filme in db: "+filmsdbAll.size());

				
				if(filmsdbAll.size() == 0){
					System.out.println("creating entries ...");
					
					try{
						ps = connection.prepareStatement("insert into film_local values (?, ?, ?, ?)");
						psS = connection.prepareStatement("insert into film_streaming values (?, ?, ?, ?, ?, ?, ?, ?)");
						
						if(mainWindowController.getPath().equals("") || mainWindowController.getPath() == null){
							System.out.println("Kein Pfad angegeben");	//if path == null or ""
						}else{
							for(int j=0;j!=entries.length;j++) //goes through all the files in the directory
							{
								ps.setInt(1, 0); //rating as integer 1. column
								ps.setString(2, cutOffEnd(entries[j])); //name as String without ending 2. column
								ps.setString(3,entries[j]); //path as String 3. column
								ps.setString(4, "favorite_border_black");
								ps.addBatch(); // add command to prepared statement
							}
						}
					
						if(mainWindowController.getStreamingPath().equals("")||mainWindowController.getStreamingPath().equals(null)){
							System.out.println("Kein Pfad angegeben");	//if path == null or ""
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
									psS.setString(8, "favorite_border_black");
									psS.addBatch(); // add command to prepared statement
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						}
						ps.executeBatch();  //execute statement to write entries into table
						psS.executeBatch();			
						connection.commit();
						ps.close();
						psS.close();
					}catch (SQLException ea) { 
						System.err.println("Konnte nicht ausgef�hrt werden"); 
						ea.printStackTrace(); 
					}
				}else {

					
					try {
						try {
							checkAddEntry();
						} catch (IOException e) {
							e.printStackTrace();
						}	//check if added a new file
						checkRemoveEntry();	//check if removed a file
					} catch (SQLException e) {
						e.printStackTrace();
					}
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
				if(rs.getString(4).equals("favorite_black")){
					mainWindowController.newData.add( new streamUiData(1, 1, 1, rs.getDouble(1), "1", rs.getString(2), rs.getString(3), new ImageView(favorite_black)));
				}else{
					mainWindowController.newData.add( new streamUiData(1, 1, 1, rs.getDouble(1), "1", rs.getString(2), rs.getString(3), new ImageView(favorite_border_black)));
				}
			}
			stmt.close();
			rs.close();
			
			//load streaming Data TODO check if there are streaming data before loading -> maybe there is an issue now
			rs = stmt.executeQuery("SELECT * FROM film_streaming;"); 
			while (rs.next()) {
				if(rs.getString(8).equals("favorite_black")){
					mainWindowController.streamData.add(new streamUiData(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getDouble(4), rs.getString(5), rs.getString(6), rs.getString(7),  new ImageView(favorite_black)));
				}else{
					mainWindowController.streamData.add(new streamUiData(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getDouble(4), rs.getString(5), rs.getString(6), rs.getString(7), new ImageView(favorite_border_black)));
				}
			}
			stmt.close();
			rs.close(); 		
		} catch (SQLException e) { 
			System.err.println("Ups! an error occured!"); 
			e.printStackTrace(); 
		}
		System.out.println("<==========finished loading sql==========>"); 
	}
	
	//Refreshes the data in mainWindowController.newDaten and mainWindowController.streamData
	//TODO it seems that there is an issue at the moment with streaming refreshing wrong entry if there is more than one with the same name
	void refresh(String name,int i) throws SQLException{
		System.out.println("refresh ...");
		Statement stmt;		
		
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM film_local WHERE titel = '"+name+"';" );
			if(rs.getString(4).equals("favorite_black")){
				mainWindowController.newData.set(i, new streamUiData(1, 1, 1, rs.getDouble(1), "1", rs.getString(2), rs.getString(3),  new ImageView(favorite_black)));
			}else{
				mainWindowController.newData.set(i, new streamUiData(1, 1, 1, rs.getDouble(1), "1", rs.getString(2), rs.getString(3), new ImageView(favorite_border_black)));
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			try {
				stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM film_streaming WHERE titel = '"+name+"';" );
				if(rs.getString(8).equals("favorite_black")){
					mainWindowController.streamData.set(i,new streamUiData(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getDouble(4), rs.getString(5), rs.getString(6), rs.getString(7),  new ImageView(favorite_black)));
				}else{
					mainWindowController.streamData.set(i,new streamUiData(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getDouble(4), rs.getString(5), rs.getString(6), rs.getString(7), new ImageView(favorite_border_black)));
				}
				stmt.close();
				rs.close();
			} catch (SQLException e1) {
				System.err.println("Ups! an error occured!"); 
				e1.printStackTrace(); 
			} 
		} 
	}
	
	private void checkRemoveEntry() throws SQLException{
		System.out.println("checking for entrys to remove to DB ...");
		Statement stmt = connection.createStatement(); 
		
		for(int a=0; a<filmsdbLocal.size(); a++){
			if(filmsDir.contains(filmsdbLocal.get(a))){
			}else{
				stmt.executeUpdate("delete from film_local where titel = '"+filmsdbLocal.get(a)+"'");
				connection.commit();
				stmt.close();
				System.out.println("removed \""+filmsdbLocal.get(a)+"\" from databsae");
			}
		}
		
		for(int b=0; b<filmsdbStreamURL.size(); b++){
			if(filmsStreamURL.contains(filmsdbStreamURL.get(b))){
			}else{
				stmt.executeUpdate("delete from film_streaming where titel = '"+filmsdbStream.get(b)+"'");
				connection.commit();
				stmt.close();
				System.out.println("removed \""+filmsdbStream.get(b)+"\" from databsae");
			}
		}
		
	}
	
	private void checkAddEntry() throws SQLException, FileNotFoundException, IOException{	//TODO sort alphabetical
		System.out.println("checking for entrys to add to DB ...");
		String[] entries = new File(mainWindowController.getPath()).list();
		Statement stmt = connection.createStatement();
		PreparedStatement ps = connection.prepareStatement("insert into film_streaming values (?, ?, ?, ?, ?, ?, ?, ?)");;
		int i=0;
		
		for(int a=0; a<filmsDir.size(); a++){
			if(filmsdbLocal.contains(filmsDir.get(a))){
			}else{			
				stmt.executeUpdate("insert into film_local values (0, '"+cutOffEnd(entries[a])+"', '"+entries[a]+"','favorite_border_black')");
				connection.commit();
				stmt.close();
				System.out.println("added \""+filmsDir.get(a)+"\" to databsae");
			}
		}
		
		for(int b=0; b<filmsStreamURL.size(); b++){
			if(filmsdbStreamURL.contains(filmsStreamURL.get(b))){
			}else{
				JsonObject object = Json.parse(new FileReader(filmsStreamData.get(b))).asObject();
				JsonArray items = object.get("entries").asArray();
				System.out.println(items.size()+", "+i);
					String streamURL = items.get(i).asObject().getString("streamUrl","");
					String titel = items.get(i).asObject().getString("titel","");
					
					if(streamURL.equals(filmsStreamURL.get(b))){
						System.out.println("hinzuf�gen \""+titel+"\"");
						
						ps.setInt(1, items.get(i).asObject().getInt("year", 0));
						ps.setInt(2, items.get(i).asObject().getInt("season", 0));
						ps.setInt(3, items.get(i).asObject().getInt("episode", 0));
						ps.setInt(4, 0);
						ps.setString(5, items.get(i).asObject().getString("resolution", ""));
						ps.setString(6, items.get(i).asObject().getString("titel",""));
						ps.setString(7, items.get(i).asObject().getString("streamUrl", ""));
						ps.setString(8, "favorite_border_black");
						ps.addBatch(); // adds the entry
						
//						stmt.executeUpdate("insert into film_streaming values ("+items.get(i).asObject().getInt("year", 0)+", "+items.get(i).asObject().getInt("season", 0)+", "+items.get(i).asObject().getInt("episode", 0)+", 0, '"+items.get(i).asObject().getString("resolution", ""+"', '"+items.get(i).asObject().getString("titel","")+"', '"+items.get(i).asObject().getString("streamUrl", "")+"')"));
					}
				i++;
			}
		}
		ps.executeBatch();			
		connection.commit();
		ps.close();
	}
	
	void ausgeben(){
	System.out.println("Eintraege ausgeben ... \n"); 
	try { 
		Statement stmt = connection.createStatement(); 
		ResultSet rs = stmt.executeQuery("SELECT * FROM film_local"); 
		while (rs.next()) { 
			System.out.println(rs.getString(1));
			System.out.println(rs.getString(2));
			System.out.println(rs.getString(3));
			System.out.println(rs.getString(4)+"\n");
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
			System.out.println(rs.getString(7));
			System.out.println(rs.getString(8)+"\n");
		}
		stmt.close();
		rs.close(); 

	} catch (SQLException e) { 
		System.err.println("Ups! an error occured!"); 
		e.printStackTrace(); 
	}
}
	
	//gibt die Favorisierung eines bestimmten Films
	void getFavStatus(String name){
		try{
			Statement stmt = connection.createStatement(); 
			ResultSet rs = stmt.executeQuery("SELECT titel, rating, favIcon FROM film_local WHERE titel = '"+name+"';" ); //SQL Befehl
			System.out.println("local:"+rs.getString("rating")+", "+rs.getString("titel")+", "+rs.getString("favIcon"));
			stmt.close();
			rs.close();
		}catch(SQLException e){
			try {
				Statement stmtS = connection.createStatement(); 
				ResultSet rsS = stmtS.executeQuery("SELECT titel, rating, favIcon FROM film_streaming WHERE titel = '"+name+"';" );
				System.out.println("streaming:"+rsS.getString("rating")+", "+rsS.getString("titel")+", "+rsS.getString("favIcon"));
				stmtS.close();
				rsS.close();
			} catch (SQLException e1) {
				System.out.println("Ups! an error occured!");
				e1.printStackTrace();
			}
		}
		
	}
	//setzt die Defavorisierung eines bestimmten Films
	void dislike(String name,String streamUrl){
		System.out.println("defavorisieren ...");		
		try{
			Statement stmt = connection.createStatement(); 
			stmt.executeUpdate("UPDATE film_local SET rating=0,favIcon='favorite_border_black' WHERE titel='"+name+"';");
			connection.commit();
			stmt.close();
		}catch(SQLException e){
			System.out.println("Ups! an error occured!");
			e.printStackTrace();
		}
		try {
			Statement stmt = connection.createStatement(); 
			stmt.executeUpdate("UPDATE film_streaming SET rating=0,favIcon='favorite_border_black' WHERE streamUrl='"+streamUrl+"';");
			connection.commit();
			stmt.close();
		} catch (SQLException e1) {
			System.out.println("Ups! an error occured!");
			e1.printStackTrace();
		}
	}
	//setzt die Favorisierung eines bestimmten Films
	void like(String name,String streamUrl){
		System.out.println("favorisieren ...");
		try{
			Statement stmt = connection.createStatement(); 
			stmt.executeUpdate("UPDATE film_local SET rating=1,favIcon='favorite_black' WHERE titel='"+name+"';");
			connection.commit();
			stmt.close();
		}catch(SQLException e){
			System.out.println("Ups! an error occured!");
			e.printStackTrace();
		}
		try {
			Statement stmt = connection.createStatement(); 
			stmt.executeUpdate("UPDATE film_streaming SET rating=1,favIcon='favorite_black' WHERE streamUrl='"+streamUrl+"';");
			connection.commit();
			stmt.close();
		} catch (SQLException e1) {
			System.out.println("Ups! an error occured!");
			e1.printStackTrace();
		}
	}
	
//removes the ending
	private String cutOffEnd (String str) {

		if (str == null) return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1) return str;
		return str.substring(0, pos);
	}
	
}

