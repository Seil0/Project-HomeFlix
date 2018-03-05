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
 */

package kellerkinder.HomeFlix.controller;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import kellerkinder.HomeFlix.application.Main;
import kellerkinder.HomeFlix.application.MainWindowController;
import kellerkinder.HomeFlix.datatypes.tableData;

public class DBController {

	public DBController(Main main, MainWindowController mainWindowController) {
		this.main = main;
		this.mainWindowController = mainWindowController;
	}

	private MainWindowController mainWindowController;
	private Main main;
	private String DB_PATH = System.getProperty("user.home") + "\\Documents\\HomeFlix" + "\\" + "Homeflix.db"; //path to database file
	private Image favorite_black = new Image("icons/ic_favorite_black_18dp_1x.png");
	private Image favorite_border_black = new Image("icons/ic_favorite_border_black_18dp_1x.png");
	private List<String> filmsdbAll = new ArrayList<String>();
	private List<String> filmsdbDir = new ArrayList<String>(); // needed
	private List<String> filmsdbStreamURL = new ArrayList<String>(); // needed
	private List<String> filmsAll = new ArrayList<String>();
	private List<String> filmsStreamURL = new ArrayList<String>(); // needed
	private Connection connection = null;
	private static final Logger LOGGER = LogManager.getLogger(DBController.class.getName());
	
	public void init() {
		LOGGER.info("<========== starting loading sql ==========>");
		initDatabaseConnection();
		createDatabase();
		refreshDataBase();
		LOGGER.info("<========== finished loading sql ==========>");
	}

	public void initDatabaseConnection() {
		DB_PATH = main.getDirectory() + "/Homeflix.db";
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
			connection.setAutoCommit(false);	//AutoCommit to false -> manual commit is active
		} catch (SQLException e) {
			// if the error message is "out of memory", it probably means no database file is found
			LOGGER.error("error while loading the ROM database", e);
		}
		LOGGER.info("ROM database loaded successfull");
	}
	
	/**
	 * if tables don't exist create them
	 * cache table: streamUrl is primary key
	 */
	private void createDatabase() {	
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("create table if not exists film_local (rating, titel, streamUrl, favIcon, cached)");
			stmt.executeUpdate("create table if not exists film_streaming (year, season, episode,"
					+ " rating, resolution, titel, streamUrl, favIcon, cached)");
			stmt.executeUpdate("create table if not exists cache ("
					+ "streamUrl, Title, Year, Rated, Released, Runtime, Genre, Director, Writer,"
					+ " Actors, Plot, Language, Country, Awards, Metascore, imdbRating, imdbVotes,"
					+ " imdbID, Type, Poster, Response)");
			stmt.close();
		} catch (SQLException e) {
			LOGGER.error(e);
		}
	}
	
	private void loadDatabase() {
		
		// get all entries from the tables
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM film_local");
			while (rs.next()) {
				filmsdbDir.add(rs.getString(2));
			}
			stmt.close();
			rs.close();

			rs = stmt.executeQuery("SELECT * FROM film_streaming;");
			while (rs.next()) {
				filmsdbAll.add(rs.getString(6));
				filmsdbStreamURL.add(rs.getString(7));
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			LOGGER.error("Ups! an error occured!", e);
		}
			
		// add all entries to filmsAll and filmsdbAl, for later comparing
		filmsdbAll.addAll(filmsdbDir);
		LOGGER.info("films in directory: " + filmsAll.size());
		LOGGER.info("filme in db: " + filmsdbAll.size());
	}
	
	// load the sources from sources.json
	private void loadSources() {
		// remove sources from table
		mainWindowController.getSourcesList().removeAll(mainWindowController.getSourcesList());
		mainWindowController.streamingRoot.getChildren().removeAll(mainWindowController.streamingRoot.getChildren());
		
		try {
			JsonArray sources = Json.parse(new FileReader(main.getDirectory() + "/sources.json")).asArray();
			for (JsonValue source : sources) {
				String path = source.asObject().getString("path", "");
				String mode = source.asObject().getString("mode", "");
				mainWindowController.addSourceToTable(path, mode); // add source to source-table
				if (mode.equals("local")) {
					// getting all files from the selected directory
					if (new File(path).exists()) {
						for (String entry : new File(path).list()) {
							filmsAll.add(cutOffEnd(entry));
						}
						LOGGER.info("added files from: " + path);
					} else {
						LOGGER.error(path + "dosen't exist!");
					}
				} else {
					// getting all entries from the streaming lists
					try {
						JsonObject object = Json.parse(new FileReader(path)).asObject();
						JsonArray items = object.get("entries").asArray();
						for (JsonValue item : items) {
							filmsAll.add(item.asObject().getString("titel", ""));
							filmsStreamURL.add(item.asObject().getString("streamUrl", ""));
							// TODO check if all this is needed, maybe only use one table!
//							System.out.println(item.asObject().getString("titel", ""));
//							System.out.println(item.asObject().getString("streamUrl", ""));
						}
						LOGGER.info("added films from: " + path);
					} catch (IOException e) {
						LOGGER.error(e);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// loading data from database to mainWindowController 
	public void loadDataToMWC() {
		LOGGER.info("loading data to mwc ...");
		try {
			//load local Data
			Statement stmt = connection.createStatement(); 
			ResultSet rs = stmt.executeQuery("SELECT * FROM film_local ORDER BY titel"); 
			while (rs.next()) {
				if (rs.getString(4).equals("favorite_black")) {
					mainWindowController.getLocalFilms().add(new tableData(1, 1, 1, rs.getDouble(1), "1",
							rs.getString(2), rs.getString(3), new ImageView(favorite_black), rs.getBoolean(5)));
				} else {
					mainWindowController.getLocalFilms().add(new tableData(1, 1, 1, rs.getDouble(1), "1",
							rs.getString(2), rs.getString(3), new ImageView(favorite_border_black), rs.getBoolean(5)));
				}
			}
			stmt.close();
			rs.close();
			
			//load streaming Data FIXME check if there are streaming data before loading -> maybe there is an issue now
			rs = stmt.executeQuery("SELECT * FROM film_streaming ORDER BY titel;"); 
			while (rs.next()) {
				if (rs.getString(8).equals("favorite_black")) {
					mainWindowController.getStreamingFilms().add(new tableData(rs.getInt(1),
							rs.getInt(2), rs.getInt(3), rs.getDouble(4), rs.getString(5), rs.getString(6),
							rs.getString(7), new ImageView(favorite_black),rs.getBoolean(9)));
				} else {
					mainWindowController.getStreamingFilms().add(new tableData(rs.getInt(1),
							rs.getInt(2), rs.getInt(3), rs.getDouble(4), rs.getString(5), rs.getString(6),
							rs.getString(7), new ImageView(favorite_border_black), rs.getBoolean(9)));
				}
			}
			stmt.close();
			rs.close(); 		
		} catch (SQLException e) {
			LOGGER.error("Ups! an error occured!", e);
		}
		
		LOGGER.info("loading data to the GUI ...");
		mainWindowController.addDataUI();
	}
	
	//Refreshes the data in mainWindowController.newDaten and mainWindowController.streamData
	//FIXME it seems that there is an issue at the moment with streaming refreshing wrong entry if there is more than one with the same name
	public void refresh(String name, int i) throws SQLException {
		LOGGER.info("refresh ...");
		Statement stmt;
		
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM film_local WHERE titel = \""+name+"\";" );
			if (rs.getString(4).equals("favorite_black")) {
				mainWindowController.getLocalFilms().set(i, new tableData(1, 1, 1, rs.getDouble(1), "1",
						rs.getString(2), rs.getString(3), new ImageView(favorite_black), rs.getBoolean(5)));
			} else {
				mainWindowController.getLocalFilms().set(i, new tableData(1, 1, 1, rs.getDouble(1), "1",
						rs.getString(2), rs.getString(3), new ImageView(favorite_border_black), rs.getBoolean(5)));
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			LOGGER.error(e);
			try {
				stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM film_streaming WHERE titel = \""+name+"\";" );
				if (rs.getString(8).equals("favorite_black")) {
					mainWindowController.getStreamingFilms().set(i, new tableData(rs.getInt(1), rs.getInt(2),
							rs.getInt(3), rs.getDouble(4),rs.getString(5), rs.getString(6), rs.getString(7),
							new ImageView(favorite_black), rs.getBoolean(9)));
				} else {
					mainWindowController.getStreamingFilms().set(i, new tableData(rs.getInt(1), rs.getInt(2),
							rs.getInt(3), rs.getDouble(4), rs.getString(5), rs.getString(6), rs.getString(7),
							new ImageView(favorite_border_black), rs.getBoolean(9)));
				}
				stmt.close();
				rs.close();
			} catch (SQLException e1) {
				LOGGER.error("Ups! an error occured!", e1);
			} 
		} 
	}
	
	/**
	 * refresh database to contain all (new added) entries
	 * refresh the MainWindowController content,
	 * to contain all (new added) entries from the database
	 */
	public void refreshDataBase() {
		LOGGER.info("refreshing the Database ...");
		
		// clean all ArraLists
		filmsdbAll.removeAll(filmsdbAll);
		filmsdbDir.removeAll(filmsdbDir);
		filmsdbStreamURL.removeAll(filmsdbStreamURL);
		filmsAll.removeAll(filmsAll);
		filmsStreamURL.removeAll(filmsStreamURL);
		
		loadSources(); // reload all sources
		loadDatabase(); // reload all films saved in the DB
		
		
		try {
			checkAddEntry();
			checkRemoveEntry();
		} catch (Exception e) {
			LOGGER.error("Error while refreshing the database", e);
		}
		
		// remove all films from the mwc lists
		mainWindowController.getLocalFilms().removeAll(mainWindowController.getLocalFilms());
		mainWindowController.getStreamingFilms().removeAll(mainWindowController.getStreamingFilms());
		mainWindowController.root.getChildren().removeAll(mainWindowController.root.getChildren());
		
		loadDataToMWC(); // load the new data to the mwc
	}

	/**
	 * check if there are any entries that have been removed from the film-directory
	 */
	private void checkRemoveEntry() {
		LOGGER.info("checking for entrys to remove to DB ...");
		
		try {
			Statement stmt = connection.createStatement();
			
			for (String entry : filmsdbDir) {
				if (!filmsAll.contains(cutOffEnd(entry))) {
					stmt.executeUpdate("delete from film_local where titel = \"" + entry + "\"");
					connection.commit();
					stmt.close();
					LOGGER.info("removed \"" + entry + "\" from database");
				}
			}
			
			for (String entry : filmsdbStreamURL) {
				if (!filmsStreamURL.contains(entry)) {
					stmt.executeUpdate("delete from film_streaming where streamUrl = \"" + entry + "\"");
					connection.commit();
					stmt.close();
					LOGGER.info("removed \"" + entry + "\" from database");
				}
			}
			
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}
	
	/**
	 * check if there are new films in the film-directory
	 * @throws SQLException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void checkAddEntry() throws SQLException, FileNotFoundException, IOException {
		Statement stmt = connection.createStatement();
		PreparedStatement ps = connection.prepareStatement("insert into film_streaming values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
		LOGGER.info("checking for entrys to add to DB ...");
		
		// source is a single source of the sources list
		for (tableData source : mainWindowController.getSourcesList()) {
			// if it's a local source check the folder for new film
			if (source.getStreamUrl().equals("local")) {
				for (String entry : new File(source.getTitle()).list()) {
					if (!filmsdbAll.contains(cutOffEnd(entry))) {
						stmt.executeUpdate("insert into film_local values (0, \"" + cutOffEnd(entry) + "\", \""
								+ source.getTitle() + "/" + entry + "\",\"favorite_border_black\",0)");
						connection.commit();
						stmt.close();
						LOGGER.info("added \"" + entry + "\" to database");
						filmsAll.add(cutOffEnd(entry));
					}
				}
			} else {
				// if it's a streaming source check the file for new films
				for (String entry : filmsStreamURL) {
					if (!filmsdbStreamURL.contains(entry)) {
						JsonArray items = Json.parse(new FileReader(source.getTitle())).asObject().get("entries").asArray();
						// for each item, check if it's the needed
						for (JsonValue item : items) {
							String streamUrl = item.asObject().getString("streamUrl", "");
							String titel = item.asObject().getString("titel", "");
							
							// if it's the needed add it to the database
							if (streamUrl.equals(entry)) {
								ps.setInt(1, item.asObject().getInt("year", 0));
								ps.setInt(2, item.asObject().getInt("season", 0));
								ps.setInt(3, item.asObject().getInt("episode", 0));
								ps.setInt(4, 0);
								ps.setString(5, item.asObject().getString("resolution", ""));
								ps.setString(6, titel);
								ps.setString(7, streamUrl);
								ps.setString(8, "favorite_border_black");
								ps.setBoolean(9, false);
								ps.addBatch(); // adds the entry
								LOGGER.info("added \"" + titel + "\" to database");
								filmsAll.add(cutOffEnd(titel));
							}
						}
					}
				}
				ps.executeBatch();
				connection.commit();
				ps.close();
			}
		}
	}
	
	// prints all entries from the database to the console
	public void printAllDBEntriesDEBUG() {
		System.out.println("Outputting all entries ... \n");
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM film_local");
			while (rs.next()) {
				System.out.println(rs.getString(1));
				System.out.println(rs.getString(2));
				System.out.println(rs.getString(3));
				System.out.println(rs.getString(4));
				System.out.println(rs.getString(5) + "\n");
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
				System.out.println(rs.getString(8));
				System.out.println(rs.getString(9) + "\n");
			}
			stmt.close();
			rs.close();

		} catch (SQLException e) {
			LOGGER.error("An error occured, while printing all entries", e);
		}
	}

	// get favorite status
	public void getFavStatus(String name) {
		try {
			if (mainWindowController.getMode().equals("local")) {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT titel, rating, favIcon FROM film_local WHERE titel = \"" + name + "\";"); // SQL Befehl
				LOGGER.info("local:" + rs.getString("rating") + ", " + rs.getString("titel") + ", " + rs.getString("favIcon"));
				stmt.close();
				rs.close();
			} else {
				Statement stmtS = connection.createStatement();
				ResultSet rsS = stmtS.executeQuery("SELECT titel, rating, favIcon FROM film_streaming WHERE titel = \"" + name + "\";");
				LOGGER.info("streaming:" + rsS.getString("rating") + ", " + rsS.getString("titel") + ", " + rsS.getString("favIcon"));
				stmtS.close();
				rsS.close();
			}	
		} catch (SQLException e) {
			LOGGER.error("Ups! an error occured!", e);
		}
	}
	
	// set rating=0 and favorite_border_black
	public void dislike(String name, String streamUrl) {
		LOGGER.info("defavorisieren ...");
		try {
			if (mainWindowController.getMode().equals("local")) {
				Statement stmt = connection.createStatement();
				stmt.executeUpdate("UPDATE film_local SET rating=0,favIcon='favorite_border_black' WHERE titel=\"" + name + "\";");
				connection.commit();
				stmt.close();
			} else {
				Statement stmt = connection.createStatement();
				stmt.executeUpdate("UPDATE film_streaming SET rating=0,favIcon='favorite_border_black' WHERE streamUrl=\"" + streamUrl + "\";");
				connection.commit();
				stmt.close();
			}
		} catch (SQLException e) {
			LOGGER.error("Ups! an error occured!", e);
		}
	}
	
	// set rating=1 and favorite_black
	public void like(String name, String streamUrl) {
		LOGGER.info("favorisieren ...");
		try {
			if (mainWindowController.getMode().equals("local")) {
				Statement stmt = connection.createStatement();
				stmt.executeUpdate("UPDATE film_local SET rating=1,favIcon='favorite_black' WHERE titel=\"" + name + "\";");
				connection.commit();
				stmt.close();
			} else {
				Statement stmt = connection.createStatement();
				stmt.executeUpdate("UPDATE film_streaming SET rating=1,favIcon='favorite_black' WHERE streamUrl=\"" + streamUrl + "\";");
				connection.commit();
				stmt.close();
			}
		} catch (SQLException e) {
			LOGGER.error("Ups! an error occured!", e);
		}
	}
	
	void setCached(String streamUrl) throws SQLException {
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("UPDATE film_local SET cached=1 WHERE streamUrl=\"" + streamUrl + "\";");
			connection.commit();
			stmt.close();
		} catch (SQLException e) {
			LOGGER.error("Ups! an error occured!", e);
		}
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("UPDATE film_streaming SET cached=1 WHERE streamUrl=\"" + streamUrl + "\";");
			connection.commit();
			stmt.close();
		} catch (SQLException e) {
			LOGGER.error("Ups! an error occured!", e);
		}
	}
	
	void addCache(	String streamUrl, String Title, String Year, String Rated, String Released, String Runtime, String Genre, String Director,
					String Writer, String Actors, String Plot, String Language, String Country, String Awards, String Metascore, String imdbRating,
					String Type, String imdbVotes, String imdbID, String Poster, String Response) throws SQLException{
		PreparedStatement ps = connection.prepareStatement("insert into cache values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		
		LOGGER.info("adding to cache: " + Title);
		ps.setString(1,streamUrl);
		ps.setString(2,Title);
		ps.setString(3,Year);
		ps.setString(4,Rated);
		ps.setString(5,Released);
		ps.setString(6,Runtime);
		ps.setString(7,Genre);
		ps.setString(8,Director);
		ps.setString(9,Writer);
		ps.setString(10,Actors);
		ps.setString(11,Plot);
		ps.setString(12,Language);
		ps.setString(13,Country);
		ps.setString(14,Awards);
		ps.setString(15,Metascore);
		ps.setString(16,imdbRating);
		ps.setString(17,imdbVotes);
		ps.setString(18,imdbID);
		ps.setString(19,Type);
		ps.setString(20,Poster);
		ps.setString(21,Response);
		ps.addBatch();
		ps.executeBatch();			
		connection.commit();
		ps.close();
		LOGGER.info("done!");
	}
	
	public void readCache(String streamUrl) {
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM cache WHERE streamUrl=\"" + streamUrl + "\";");
			ArrayList<Text> nameText = new ArrayList<Text>();
			ArrayList<Text> responseText = new ArrayList<Text>();
			String fontFamily = main.getFONT_FAMILY();
			Image im;
			int fontSize = (int) Math.round(mainWindowController.size);
			int j = 2;

			nameText.add(0, new Text(mainWindowController.getBundle().getString("title") + ": "));
			nameText.add(1, new Text(mainWindowController.getBundle().getString("year") + ": "));
			nameText.add(2, new Text(mainWindowController.getBundle().getString("rating") + ": "));
			nameText.add(3, new Text(mainWindowController.getBundle().getString("publishedOn") + ": "));
			nameText.add(4, new Text(mainWindowController.getBundle().getString("duration") + ": "));
			nameText.add(5, new Text(mainWindowController.getBundle().getString("genre") + ": "));
			nameText.add(6, new Text(mainWindowController.getBundle().getString("director") + ": "));
			nameText.add(7, new Text(mainWindowController.getBundle().getString("writer") + ": "));
			nameText.add(8, new Text(mainWindowController.getBundle().getString("actors") + ": "));
			nameText.add(9, new Text(mainWindowController.getBundle().getString("plot") + ": "));
			nameText.add(10, new Text(mainWindowController.getBundle().getString("language") + ": "));
			nameText.add(11, new Text(mainWindowController.getBundle().getString("country") + ": "));
			nameText.add(12, new Text(mainWindowController.getBundle().getString("awards") + ": "));
			nameText.add(13, new Text(mainWindowController.getBundle().getString("metascore") + ": "));
			nameText.add(14, new Text(mainWindowController.getBundle().getString("imdbRating") + ": "));
			nameText.add(15, new Text(mainWindowController.getBundle().getString("type") + ": "));

			for (int i = 0; i < 15; i++) {
				responseText.add(new Text(rs.getString(j) + "\n"));
				j++;
			}
			responseText.add(new Text(rs.getString(19) + "\n"));
			im = new Image(new File(rs.getString(20)).toURI().toString());

			stmt.close();
			rs.close();

			for (int i = 0; i < nameText.size(); i++) {
				nameText.get(i).setFont(Font.font(fontFamily, FontWeight.BOLD, fontSize));
				responseText.get(i).setFont(Font.font(fontFamily, fontSize));
			}

			mainWindowController.getTextFlow().getChildren().remove(0,
					mainWindowController.getTextFlow().getChildren().size());

			for (int i = 0; i < nameText.size(); i++) {
				mainWindowController.getTextFlow().getChildren().addAll(nameText.get(i), responseText.get(i));
			}

			try {
				mainWindowController.getImage1().setImage(im);
			} catch (Exception e) {
				mainWindowController.getImage1().setImage(new Image("resources/icons/close_black_2048x2048.png"));
				LOGGER.error(e);
			}
			mainWindowController.getImage1().setImage(im);

		} catch (SQLException e) {
			LOGGER.error("Ups! an error occured!", e);
		}
	}
	
	// removes the ending
	private String cutOffEnd(String str) {
		if (str == null) return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1) return str;
		return str.substring(0, pos);
	}
	
}
