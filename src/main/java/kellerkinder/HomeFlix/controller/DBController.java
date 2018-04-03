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
import kellerkinder.HomeFlix.datatypes.SourceDataType;
import kellerkinder.HomeFlix.datatypes.FilmTabelDataType;

public class DBController {
	
	private MainWindowController mainWindowController;
	private Main main;
	private String DB_PATH = System.getProperty("user.home") + "\\Documents\\HomeFlix" + "\\" + "Homeflix.db"; //path to database file
	private Image favorite_black = new Image("icons/ic_favorite_black_18dp_1x.png");
	private Image favorite_border_black = new Image("icons/ic_favorite_border_black_18dp_1x.png");
	private List<String> filmsdbAll = new ArrayList<String>();
	private List<String> filmsdbDir = new ArrayList<String>();
	private List<String> filmsdbStreamURL = new ArrayList<String>(); // needed
	private List<String> filmsStreamURL = new ArrayList<String>(); // needed
	private Connection connection = null;
	private static final Logger LOGGER = LogManager.getLogger(DBController.class.getName());
	
	/**
	 * constructor for DBController
	 * @param main					the main object
	 * @param mainWindowController	the mainWindowController object
	 */
	public DBController(Main main, MainWindowController mainWindowController) {
		this.main = main;
		this.mainWindowController = mainWindowController;
	}
	
	/**
	 * initialize the {@link DBController}
	 * initialize the database connection
	 * check if there is a need to create a new database
	 * refresh the database
	 */
	public void init() {
		LOGGER.info("<========== starting loading sql ==========>");
		initDatabaseConnection();
		createDatabase();
		refreshDataBase();
		LOGGER.info("<========== finished loading sql ==========>");
	}

	/**
	 * create a new connection to the HomeFlix.db database
	 * AutoCommit is set to false to prevent some issues, so manual commit is active!
	 */
	private void initDatabaseConnection() {
		DB_PATH = main.getDirectory() + "/Homeflix.db";
		try {
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			// if the error message is "out of memory", it probably means no database file is found
			LOGGER.error("error while loading the ROM database", e);
		}
		LOGGER.info("ROM database loaded successfull");
	}
	
	/**
	 * if tables don't exist create them
	 * films table: streamUrl is primary key
	 * cache table: streamUrl is primary key
	 */
	private void createDatabase() {	
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("create table if not exists films (streamUrl, title, season, episode, favorite, cached, currentTime)");
			stmt.executeUpdate("create table if not exists cache ("
					+ "streamUrl, Title, Year, Rated, Released, Runtime, Genre, Director, Writer,"
					+ " Actors, Plot, Language, Country, Awards, Metascore, imdbRating, imdbVotes,"
					+ " imdbID, Type, Poster, Response)");
			stmt.close();
		} catch (SQLException e) {
			LOGGER.error(e);
		}
	}
	
	/**
	 * get all database entries
	 */
	private void loadDatabase() {
		// get all entries from the table
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM films");
			while (rs.next()) {
				filmsdbDir.add(rs.getString("title"));
				filmsdbStreamURL.add(rs.getString("streamUrl"));
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			LOGGER.error("Ups! an error occured!", e);
		}
			
		// add all entries to filmsAll and filmsdbAl, for later comparing
		filmsdbAll.addAll(filmsdbDir);
		LOGGER.info("films in directory: " + filmsStreamURL.size());
		LOGGER.info("filme in db: " + filmsdbStreamURL.size());
	}
	
	/**
	 * load sources from sources.json
	 * if mode == local, get all files and series-folder from the directory
	 * else mode must be streaming, read all entries from the streaming file 
	 */
	private void loadSources() {
		// remove sources from table
		mainWindowController.getSourcesList().removeAll(mainWindowController.getSourcesList());
		mainWindowController.getSourceRoot().getChildren().removeAll(mainWindowController.getSourceRoot().getChildren());
		
		try {
			JsonArray sources = Json.parse(new FileReader(main.getDirectory() + "/sources.json")).asArray();
			for (JsonValue source : sources) {
				String path = source.asObject().getString("path", "");
				String mode = source.asObject().getString("mode", "");
				mainWindowController.addSourceToTable(path, mode); // add source to source-table
				if (mode.equals("local")) {
					for (File file : new File(path).listFiles()) {
						if (file.isFile()) {
							// get all files (films)
							filmsStreamURL.add(file.getPath());
						} else {
							// get all folders (series)
							for (File season : file.listFiles()) {
								if (season.isDirectory()) {
									for (File episode : season.listFiles()) {
										if (!filmsdbStreamURL.contains(episode.getPath())) {
											filmsStreamURL.add(episode.getPath());
										}
									}
								}
							}
						}
					}
					LOGGER.info("added files from: " + path);
				} else {
					// getting all entries from the streaming lists
					try {
						JsonObject object = Json.parse(new FileReader(path)).asObject();
						JsonArray items = object.get("entries").asArray();
						for (JsonValue item : items) {
							filmsStreamURL.add(item.asObject().getString("streamUrl", ""));
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
	
	/**
	 * load the data to the mainWindowController
	 * order entries by title
	 */
	private void loadDataToMWC() {
		LOGGER.info("loading data to mwc ...");
		try {
			//load local Data
			Statement stmt = connection.createStatement(); 
			ResultSet rs = stmt.executeQuery("SELECT * FROM films ORDER BY title"); 
			while (rs.next()) {
//				System.out.println(rs.getString("title") + "Season:"  + rs.getString("season") + ":");
				if (rs.getBoolean("favorite") == true) {
					mainWindowController.getFilmsList().add(new FilmTabelDataType(rs.getString("streamUrl"),
							rs.getString("title"), rs.getString("season"), rs.getString("episode") ,rs.getBoolean("favorite"),
							rs.getBoolean("cached"), new ImageView(favorite_black)));
				} else {
					mainWindowController.getFilmsList().add(new FilmTabelDataType(rs.getString("streamUrl"),
							rs.getString("title"), rs.getString("season"), rs.getString("episode"), rs.getBoolean("favorite"),
							rs.getBoolean("cached"), new ImageView(favorite_border_black)));
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
	
	/**
	 * refresh data in mainWindowController for one element
	 * @param streamUrl of the film
	 * @param index of the film in LocalFilms list
	 */
	public void refresh(String streamUrl, int indexList) {
		LOGGER.info("refresh ...");
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM films WHERE streamUrl = \"" + streamUrl + "\";");
			
			if (rs.getBoolean("favorite") == true) {
				mainWindowController.getFilmsList().set(indexList, new FilmTabelDataType(rs.getString("streamUrl"),
						rs.getString("title"), rs.getString("season"), rs.getString("episode"), rs.getBoolean("favorite"),
						rs.getBoolean("cached"), new ImageView(favorite_black)));
			} else {
				mainWindowController.getFilmsList().set(indexList, new FilmTabelDataType(rs.getString("streamUrl"),
						rs.getString("title"), rs.getString("season"), rs.getString("episode"), rs.getBoolean("favorite"),
						rs.getBoolean("cached"), new ImageView(favorite_border_black)));
			}
			
			rs.close();
			stmt.close();
		} catch (Exception e) {
			LOGGER.error("Ups! error while refreshing mwc!", e);
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
		mainWindowController.getFilmsList().removeAll(mainWindowController.getFilmsList());
		mainWindowController.getFilmRoot().getChildren().removeAll(mainWindowController.getFilmRoot().getChildren());
		
		loadDataToMWC(); // load the new data to the mwc
	}

	/**
	 * check if there are any entries that have been removed from the film-directory
	 */
	private void checkRemoveEntry() {
		LOGGER.info("checking for entrys to remove to DB ...");
		
		try {
			Statement stmt = connection.createStatement();
			
			for (String entry : filmsdbStreamURL) {
				if (!filmsStreamURL.contains(entry)) {
					System.out.println(filmsdbStreamURL + "\n");
					System.out.println(filmsStreamURL);
					stmt.executeUpdate("delete from films where streamUrl = \"" + entry + "\"");
					connection.commit();
					LOGGER.info("removed \"" + entry + "\" from database");
				}
			}

			stmt.close();
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
		PreparedStatement ps = connection.prepareStatement("insert into films values (?, ?, ?, ?, ?, ?, ?)");
		LOGGER.info("checking for entrys to add to DB ...");
		
		// source is a single source of the sources list
		for (SourceDataType source : mainWindowController.getSourcesList()) {
			// if it's a local source check the folder for new film
			if (source.getMode().equals("local")) {
				for (File file : new File(source.getPath()).listFiles()) {
					
					if (file.isFile()) {
						// get all files (films)
						if (!filmsdbStreamURL.contains(file.getPath())) {
							stmt.executeUpdate("insert into films values ("
									+ "'" + file.getPath() + "',"
									+ "'" + cutOffEnd(file.getName()) + "', '', '', 0, 0, 0.0)");
							connection.commit();
							stmt.close();
							LOGGER.info("Added \"" + file.getName() + "\" to database");
							filmsdbStreamURL.add(file.getPath());
						}
					} else {
						// get all folders (series)
						int sn = 1;
						for (File season : file.listFiles()) {
							if (season.isDirectory()) {
								int ep = 1;
								for (File episode : season.listFiles()) {
									if (!filmsdbStreamURL.contains(episode.getPath())) {
										LOGGER.info("Added \"" + file.getName() + "\", Episode: " + episode.getName() + " to database");
										stmt.executeUpdate("insert into films values ("
										+ "'" + episode.getPath().replace("'", "''") + "',"
										+ "'" + cutOffEnd(file.getName()) + "','" + sn + "','" + ep + "', 0, 0, 0.0)");
										connection.commit();
										stmt.close();
										filmsStreamURL.add(episode.getPath());
										filmsdbStreamURL.add(episode.getPath());
										ep++;
									}
								}
								sn++;
							}
						}
					}
					
				}
			} else {
				// if it's a streaming source check the file for new films
				for (String entry : filmsStreamURL) {
					if (!filmsdbStreamURL.contains(entry)) {
						JsonArray items = Json.parse(new FileReader(source.getPath())).asObject().get("entries").asArray();
						// for each item, check if it's the needed
						for (JsonValue item : items) {
							String streamUrl = item.asObject().getString("streamUrl", "");
							String title = item.asObject().getString("title", "");
							
							// if it's the needed add it to the database
							if (streamUrl.equals(entry)) {
								ps.setString(1, streamUrl);
								ps.setString(2, title);
								ps.setString(3, item.asObject().getString("season", ""));
								ps.setString(4, item.asObject().getString("episode", ""));
								ps.setInt(5, 0);
								ps.setBoolean(6, false);
								ps.setDouble(7, 0);
								ps.addBatch(); // adds the entry
								LOGGER.info("Added \"" + title + "\" to database");
								filmsdbStreamURL.add(streamUrl);
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
	
	/**
	 * DEBUG
	 * prints all entries from the database to the console
	 */
	public void printAllDBEntriesDEBUG() {
		System.out.println("Outputting all entries ... \n");
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM films");
			while (rs.next()) {
				System.out.println(rs.getString("streamUrl"));
				System.out.println(rs.getString("title"));
				System.out.println(rs.getString("season"));
				System.out.println(rs.getString("episode"));
				System.out.println(rs.getString("rating"));
				System.out.println(rs.getString("cached"));
				System.out.println(rs.getString("currentTime") + "\n");
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			LOGGER.error("An error occured, while printing all entries", e);
		}
	}
	
	/**
	 * update the database entry for the given film, favorite = 0
	 * @param streamUrl URL of the film
	 */
	public void dislike(String streamUrl) {
		LOGGER.info("dislike " + streamUrl);
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("UPDATE films SET favorite=0 WHERE streamUrl=\"" + streamUrl + "\";");
			connection.commit();
			stmt.close();
		} catch (SQLException e) {
			LOGGER.error("Ups! an error occured!", e);
		}
	}
	
	/**
	 * update the database entry for the given film, favorite = 1
	 * @param streamUrl URL of the film
	 */
	public void like(String streamUrl) {
		LOGGER.info("like " + streamUrl);
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("UPDATE films SET favorite=1 WHERE streamUrl=\"" + streamUrl + "\";");
			connection.commit();
			stmt.close();
		} catch (SQLException e) {
			LOGGER.error("Ups! an error occured!", e);
		}
	}
	
	/**
	 * update the database entry for the given film, cached = 1
	 * @param streamUrl URL of the film
	 */
	void setCached(String streamUrl) {
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("UPDATE films SET cached=1 WHERE streamUrl=\"" + streamUrl + "\";");
			connection.commit();
			stmt.close();
		} catch (SQLException e) {
			LOGGER.error("Ups! an error occured!", e);
		}
		
		refresh(streamUrl, mainWindowController.getIndexList());
	}
	
	/**
	 * add the received data to the cache table
	 * @param streamUrl URL of the film
	 * @param Title
	 * @param Year
	 * @param Rated
	 * @param Released
	 * @param Runtime
	 * @param Genre
	 * @param Director
	 * @param Writer
	 * @param Actors
	 * @param Plot
	 * @param Language
	 * @param Country
	 * @param Awards
	 * @param Metascore
	 * @param imdbRating
	 * @param Type
	 * @param imdbVotes
	 * @param imdbID
	 * @param Poster
	 * @param Response
	 * @throws SQLException
	 */
	void addCache(	String streamUrl, String Title, String Year, String Rated, String Released, String Runtime, String Genre, String Director,
					String Writer, String Actors, String Plot, String Language, String Country, String Awards, String Metascore, String imdbRating,
					String Type, String imdbVotes, String imdbID, String Poster, String Response) {
		try {
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
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}
	
	/**
	 * sets the cached data to mwc's TextFlow
	 * @param streamUrl URL of the film
	 */
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
				mainWindowController.getPosterImageView().setImage(im);
			} catch (Exception e) {
				mainWindowController.getPosterImageView().setImage(new Image("resources/icons/close_black_2048x2048.png"));
				LOGGER.error(e);
			}

		} catch (SQLException e) {
			LOGGER.error("Ups! an error occured!", e);
		}
	}
	
	/**
	 * return the currentTime in ms saved in the database
	 * @param streamUrl URL of the film
	 * @return {@link Double} currentTime in ms
	 */
	public double getCurrentTime(String streamUrl) {
		LOGGER.info("currentTime: " + streamUrl);
		double currentTime = 0;
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM films WHERE streamUrl = \"" + streamUrl + "\";");
			currentTime = rs.getDouble("currentTime");
			rs.close();
			stmt.close();
		} catch (Exception e) {
			LOGGER.error("Ups! error while refreshing mwc!", e);
		} 
		
		return currentTime;
	}
	
	/**
	 * save the currentTime to the database
	 * @param streamUrl		URL of the film
	 * @param currentTime	currentTime in ms of the film
	 */
	public void setCurrentTime(String streamUrl, double currentTime) {
		LOGGER.info("currentTime: " + streamUrl);
		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("UPDATE films SET currentTime=" + currentTime + " WHERE streamUrl=\"" + streamUrl + "\";");
			connection.commit();
			stmt.close();
		} catch (SQLException e) {
			LOGGER.error("Ups! an error occured!", e);
		}
	}
	
	/**
	 * get the next episode of a 
	 * @param title	URL of the film
	 * @param nextEp	number of the next episode
	 * @return {@link FilmTabelDataType} the next episode as object
	 */
	public FilmTabelDataType getNextEpisode(String title, int nextEp) {
		FilmTabelDataType nextFilm = null;
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM films WHERE title = \"" + title + "\" AND episode = \"" + nextEp + "\";");
			while (rs.next()) {
				if (rs.getBoolean("favorite") == true) {
					nextFilm = new FilmTabelDataType(rs.getString("streamUrl"),
							rs.getString("title"), rs.getString("season"), rs.getString("episode") ,rs.getBoolean("favorite"),
							rs.getBoolean("cached"), new ImageView(favorite_black));
				} else {
					nextFilm = new FilmTabelDataType(rs.getString("streamUrl"),
							rs.getString("title"), rs.getString("season"), rs.getString("episode"), rs.getBoolean("favorite"),
							rs.getBoolean("cached"), new ImageView(favorite_border_black));
				}
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			LOGGER.error("Ups! error while getting next episode!", e);
		} 
		return nextFilm;
	}
	
	// removes the ending
	private String cutOffEnd(String str) {
		if (str == null) return null;
		int pos = str.lastIndexOf(".");
		if (pos == -1) return str;
		return str.substring(0, pos);
	}
	
}
