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
import java.net.URLConnection;
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

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import kellerkinder.HomeFlix.application.Main;
import kellerkinder.HomeFlix.application.MainWindowController;
import kellerkinder.HomeFlix.datatypes.SourceDataType;
import kellerkinder.HomeFlix.datatypes.FilmTabelDataType;
import kellerkinder.HomeFlix.datatypes.OMDbAPIResponseDataType;

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
	 * @param main					the Main object
	 * @param mainWindowController	the MainWindowController object
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
					+ "streamUrl, Title, Year, Rated, Released, Season, Episode ,Runtime, Genre, Director, Writer,"
					+ " Actors, Plot, Language, Country, Awards, Poster, Metascore, imdbRating, imdbVotes,"
					+ " imdbID, Type, dvd, BoxOffice, Website, Response)");
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
						if (file.isFile() && isVideoFile(file.getPath())) {
							filmsStreamURL.add(file.getPath());
						} else if(file.isDirectory()) {
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
		mainWindowController.addDataUI(mainWindowController.getFilmsList());
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
			
			while (rs.next()) {
				if (rs.getBoolean("favorite") == true) {
					mainWindowController.getFilmsList().set(indexList, new FilmTabelDataType(rs.getString("streamUrl"),
							rs.getString("title"), rs.getString("season"), rs.getString("episode"), rs.getBoolean("favorite"),
							rs.getBoolean("cached"), new ImageView(favorite_black)));
				} else {
					mainWindowController.getFilmsList().set(indexList, new FilmTabelDataType(rs.getString("streamUrl"),
							rs.getString("title"), rs.getString("season"), rs.getString("episode"), rs.getBoolean("favorite"),
							rs.getBoolean("cached"), new ImageView(favorite_border_black)));
				}
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
		filmsdbAll.clear();
		filmsdbDir.clear();
		filmsdbStreamURL.clear();
		filmsStreamURL.clear();
		
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
				// if the directory doen't contain the entry form the db, remove it
				if (!filmsStreamURL.contains(entry)) {
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
					String mimeType = URLConnection.guessContentTypeFromName(file.getPath());
					// if file is file and has mime type "video"
					if (file.isFile() && mimeType != null && mimeType.contains("video")) {
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
					} else if (file.isDirectory()) {
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
	 * @param streamUrl 	URL of the film
	 * @param omdbResponse	the response data from omdbAPI
	 */
	void addCache(String streamUrl, OMDbAPIResponseDataType omdbResponse) {
		try {
			PreparedStatement ps = connection.prepareStatement("insert into cache values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			LOGGER.info("adding to cache: " + omdbResponse.getTitle());
			ps.setString(1,streamUrl);
			ps.setString(2,omdbResponse.getTitle());
			ps.setString(3,omdbResponse.getYear());
			ps.setString(4,omdbResponse.getRated());
			ps.setString(5,omdbResponse.getReleased());
			ps.setString(6,omdbResponse.getSeason());
			ps.setString(7,omdbResponse.getEpisode());
			ps.setString(8,omdbResponse.getRuntime());
			ps.setString(9,omdbResponse.getGenre());
			ps.setString(10,omdbResponse.getDirector());
			ps.setString(11,omdbResponse.getWriter());
			ps.setString(12,omdbResponse.getActors());
			ps.setString(13,omdbResponse.getPlot());
			ps.setString(14,omdbResponse.getLanguage());
			ps.setString(15,omdbResponse.getCountry());
			ps.setString(16,omdbResponse.getAwards());
			ps.setString(17,omdbResponse.getPoster());
			ps.setString(18,omdbResponse.getMetascore());
			ps.setString(19,omdbResponse.getImdbRating());
			ps.setString(20,omdbResponse.getImdbVotes());
			ps.setString(21,omdbResponse.getImdbID());		
			ps.setString(22,omdbResponse.getType());
			ps.setString(23,omdbResponse.getDvd());
			ps.setString(24,omdbResponse.getBoxOffice());
			ps.setString(25,omdbResponse.getWebsite());
			ps.setString(26,omdbResponse.getResponse());
			
			ps.addBatch();
			ps.executeBatch();			
			connection.commit();
			ps.close();
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}
	
	/**
	 * checks if there is already a entry with the given streamUrl in the cache
	 * @param streamUrl URL of the element
	 * @return true if the element is already cached, else false
	 */
	public boolean searchCacheByURL(String streamUrl) {
		boolean retValue = false;
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM cache WHERE streamUrl = \"" + streamUrl + "\";");
			retValue = rs.next();
			rs.close();
			stmt.close();
		} catch (Exception e) {
			LOGGER.error("Ups! error while getting the current time!", e);
		}
		
		return retValue;
	}
	
	/**
	 * sets the cached data to mwc's TextFlow
	 * @param streamUrl URL of the film
	 */
	public void readCache(String streamUrl) {
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM cache WHERE streamUrl=\"" + streamUrl + "\";");
			Font font = Font.font("System", FontWeight.BOLD, (int) Math.round(mainWindowController.getFontSize()));
			ObservableList<Node> textFlow = mainWindowController.getTextFlow().getChildren();	
			ArrayList<Text> nameText = new ArrayList<Text>();
			
			nameText.add(new Text(mainWindowController.getBundle().getString("title") + ": "));
			nameText.add(new Text(mainWindowController.getBundle().getString("year") + ": "));
			nameText.add(new Text(mainWindowController.getBundle().getString("rated") + ": "));
			nameText.add(new Text(mainWindowController.getBundle().getString("released") + ": "));
			nameText.add(new Text(mainWindowController.getBundle().getString("season") + ": "));
			nameText.add(new Text(mainWindowController.getBundle().getString("episode") + ": "));
			nameText.add(new Text(mainWindowController.getBundle().getString("runtime") + ": "));
			nameText.add(new Text(mainWindowController.getBundle().getString("genre") + ": "));
			nameText.add(new Text(mainWindowController.getBundle().getString("director") + ": "));
			nameText.add(new Text(mainWindowController.getBundle().getString("writer") + ": "));
			nameText.add(new Text(mainWindowController.getBundle().getString("actors") + ": "));
			nameText.add(new Text(mainWindowController.getBundle().getString("plot") + ": "));
			nameText.add(new Text(mainWindowController.getBundle().getString("language") + ": "));
			nameText.add(new Text(mainWindowController.getBundle().getString("country") + ": "));
			nameText.add(new Text(mainWindowController.getBundle().getString("awards") + ": "));
			nameText.add(new Text(mainWindowController.getBundle().getString("metascore") + ": "));
			nameText.add(new Text(mainWindowController.getBundle().getString("imdbRating") + ": "));
			nameText.add(new Text(mainWindowController.getBundle().getString("type") + ": "));
			nameText.add(new Text(mainWindowController.getBundle().getString("boxOffice") + ": "));
			nameText.add(new Text(mainWindowController.getBundle().getString("website") + ": "));
			
			// set the correct font for the  nameText
			for (Text text : nameText) {
				text.setFont(font);
			}
			
			// clear the textFlow and all the new text
			textFlow.clear();
			textFlow.addAll(nameText.get(0), new Text(rs.getString("Title") + "\n"));
			textFlow.addAll(nameText.get(1), new Text(rs.getString("Year") + "\n"));
			textFlow.addAll(nameText.get(2), new Text(rs.getString("Rated") + "\n"));
			textFlow.addAll(nameText.get(3), new Text(rs.getString("Released") + "\n"));
			
			if (rs.getString("Episode").length() > 0) {
				textFlow.addAll(nameText.get(4), new Text(rs.getString("Season") + "\n"));
				textFlow.addAll(nameText.get(5), new Text(rs.getString("Episode") + "\n"));
			}

			textFlow.addAll(nameText.get(6), new Text(rs.getString("Runtime") + "\n"));
			textFlow.addAll(nameText.get(7), new Text(rs.getString("Genre") + "\n"));
			textFlow.addAll(nameText.get(8), new Text(rs.getString("Director") + "\n"));
			textFlow.addAll(nameText.get(9), new Text(rs.getString("Writer") + "\n"));
			textFlow.addAll(nameText.get(10), new Text(rs.getString("Actors") + "\n"));
			textFlow.addAll(nameText.get(11), new Text(rs.getString("Plot") + "\n"));
			textFlow.addAll(nameText.get(12), new Text(rs.getString("Language") + "\n"));
			textFlow.addAll(nameText.get(13), new Text(rs.getString("Country") + "\n"));
			textFlow.addAll(nameText.get(14), new Text(rs.getString("Awards") + "\n"));
			textFlow.addAll(nameText.get(15), new Text(rs.getString("metascore") + "\n"));
			textFlow.addAll(nameText.get(16), new Text(rs.getString("imdbRating") + "\n"));
			textFlow.addAll(nameText.get(17), new Text(rs.getString("Type") + "\n"));
			textFlow.addAll(nameText.get(18), new Text(rs.getString("BoxOffice") + "\n"));
			textFlow.addAll(nameText.get(19), new Text(rs.getString("Website") + "\n"));
			
			mainWindowController.getTextFlow().setStyle("-fx-font-size : " + ((int) Math.round(mainWindowController.getFontSize()) + 1) + "px;");
			
			// add the image
			try {
				mainWindowController.getPosterImageView().setImage(new Image(new File(rs.getString("Poster")).toURI().toString()));
			} catch (Exception e) {
				mainWindowController.getPosterImageView().setImage(new Image("icons/close_black_2048x2048.png"));
				LOGGER.error("No Poster found, useing default.");
			}
			
			stmt.close();
			rs.close();
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
			LOGGER.error("Ups! error while getting the current time!", e);
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
	public FilmTabelDataType getNextEpisode(String title, int episode, int season) {
		FilmTabelDataType nextFilm = null;
		ResultSet rs;
		int nextEpisode = 3000;
		
		try {
			Statement stmt = connection.createStatement();
			
			rs = stmt.executeQuery("SELECT * FROM films WHERE title = \"" + title + "\" AND season = \"" + season + "\";");
			while(rs.next()) {
				int rsEpisode = Integer.parseInt(rs.getString("episode"));
				if (rsEpisode > episode && rsEpisode < nextEpisode) {
					// fitting episode found in current season, if rsEpisode < nextEpisode -> nextEpisode = rsEpisode
					nextEpisode = rsEpisode;
					System.out.println("next episode is: " + nextEpisode);
					// favorite image is black
					nextFilm = new FilmTabelDataType(rs.getString("streamUrl"), rs.getString("title"),
							rs.getString("season"), rs.getString("episode"), rs.getBoolean("favorite"),
							rs.getBoolean("cached"), new ImageView(favorite_black));
				}
			}
			
			if (nextFilm == null) {
				int nextSeason = 3000;
				System.out.println("searching next season");
				rs = stmt.executeQuery("SELECT * FROM films WHERE title = \"" + title + "\";");
				while(rs.next()) {
					int rsSeason = Integer.parseInt(rs.getString("season"));
					if (rsSeason > season && rsSeason < nextSeason) {
						nextSeason = rsSeason;
					}
				}
				
				if (nextSeason != 3000) {
					System.out.println("next season is: " + nextSeason);
					rs = stmt.executeQuery("SELECT * FROM films WHERE title = \"" + title + "\" AND season = \"" + season + "\";");
					while(rs.next()) {
						int rsEpisode = Integer.parseInt(rs.getString("episode"));
						if (rsEpisode > episode && rsEpisode < nextEpisode) {
							// fitting episode found in current season, if rsEpisode < nextEpisode -> nextEpisode = rsEpisode
							nextEpisode = rsEpisode;
							System.out.println("next episode is: " + nextEpisode);
							// favorite image is black
							nextFilm = new FilmTabelDataType(rs.getString("streamUrl"), rs.getString("title"),
									rs.getString("season"), rs.getString("episode"), rs.getBoolean("favorite"),
									rs.getBoolean("cached"), new ImageView(favorite_black));
						}
					}
				}
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			LOGGER.error("Ups! error while getting next episode!", e);
		}
		return nextFilm;
	}
	
	/**
	 * get the last watched episode
	 * @param title the title of the series
	 * @return the last watched episode as {@link FilmTabelDataType} object
	 */
	public FilmTabelDataType getLastWatchedEpisode(String title) {
		LOGGER.info("last watched episode of: " + title);
		FilmTabelDataType nextFilm = null;
		double lastCurrentTime = 0;
		
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM films WHERE title = \"" + title + "\";");
			while (rs.next()) {
				// favorite image is black
				nextFilm = new FilmTabelDataType(rs.getString("streamUrl"), rs.getString("title"),
						rs.getString("season"), rs.getString("episode"), rs.getBoolean("favorite"),
						rs.getBoolean("cached"), new ImageView(favorite_black));
				if (rs.getDouble("currentTime") > lastCurrentTime) {
					lastCurrentTime = rs.getDouble("currentTime");
					// favorite image is black
					nextFilm = new FilmTabelDataType(rs.getString("streamUrl"), rs.getString("title"),
							rs.getString("season"), rs.getString("episode"), rs.getBoolean("favorite"),
							rs.getBoolean("cached"), new ImageView(favorite_black));
					break;
				}
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			LOGGER.error("Ups! error while getting the last watched episode!", e);
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
	
	/**
	 * check if a file is a video
	 * @param path the path to the file
	 * @return true if the file is a video, else false
	 */
	public static boolean isVideoFile(String path) {
	    String mimeType = URLConnection.guessContentTypeFromName(path);    
	    return mimeType != null && mimeType.startsWith("video");
	}
	
}
