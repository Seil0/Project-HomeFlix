/**
 * Project-HomeFlix
 * 
 * Copyright 2018  <@Seil0>
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
package kellerkinder.HomeFlix.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import javafx.application.Platform;
import kellerkinder.HomeFlix.application.Main;
import kellerkinder.HomeFlix.application.MainWindowController;
import kellerkinder.HomeFlix.datatypes.OMDbAPIResponseDataType;

public class OMDbAPIController implements Runnable {
	
	private MainWindowController mainWindowController;
	private DBController dbController;
	private Main main;
	private String URL = "https://www.omdbapi.com/?apikey=";
	private static final Logger LOGGER = LogManager.getLogger(MainWindowController.class.getName());
	
	/**
	 * constructor for the OMDbAPIController
	 * @param mainWindowController	the MainWindowController object
	 * @param dbController			the DBController object
	 * @param main					the Main object
	 */
	public OMDbAPIController(MainWindowController mainWindowController, DBController dbController, Main main){
		this.mainWindowController = mainWindowController;
		this.dbController = dbController;
		this.main = main;
		
		
	}

	@Override
	public void run() {
		JsonObject object;
		object = getByTitle(mainWindowController.getCurrentTitle());
		if (object == null) return;
		
		if (object.getString("Error", "").contains("not found!")) {
			String title = searchByTitle(mainWindowController.getCurrentTitle());
			if (title.length() > 0) {
				object = getByTitle(title);
			} else {
				return;
			}
		}
		
		OMDbAPIResponseDataType omdbResponse = new OMDbAPIResponseDataType();
		omdbResponse.setTitle(object.getString("Title", ""));
		omdbResponse.setYear(object.getString("Year", ""));
		omdbResponse.setRated(object.getString("Rated", ""));
		omdbResponse.setReleased(object.getString("Release", ""));
		omdbResponse.setSeason(object.getString("Season", ""));
		omdbResponse.setEpisode(object.getString("Episode", ""));
		omdbResponse.setRuntime(object.getString("Runtime", ""));
		omdbResponse.setGenre(object.getString("Genre", ""));
		omdbResponse.setDirector(object.getString("Director", ""));
		omdbResponse.setWriter(object.getString("Writer", ""));
		omdbResponse.setActors(object.getString("Actors", ""));
		omdbResponse.setPlot(object.getString("Plot", ""));
		omdbResponse.setLanguage(object.getString("Language", ""));
		omdbResponse.setCountry(object.getString("Country", ""));
		omdbResponse.setAwards(object.getString("Awards", ""));
		omdbResponse.setMetascore(object.getString("Metascore", ""));
		omdbResponse.setImdbRating(object.getString("imdbRating", ""));
		omdbResponse.setImdbVotes(object.getString("imdbVotes", ""));
		omdbResponse.setImdbID(object.getString("imdbID", ""));
		omdbResponse.setType(object.getString("Type", ""));
		omdbResponse.setDvd(object.getString("DVD", ""));
		omdbResponse.setBoxOffice(object.getString("BoxOffice", ""));
		omdbResponse.setProduction(object.getString("Production", ""));
		omdbResponse.setWebsite(object.getString("Website", ""));
		omdbResponse.setResponse(object.getString("Response", ""));

		// resize the image to fit in the posterImageView and add it to the cache
		try {
			BufferedImage originalImage = ImageIO.read(new URL(object.getString("Poster", "")));
			// change path to where file is located
			omdbResponse.setPoster(main.getPosterCache() + "/" + mainWindowController.getCurrentTitle() + ".png");
			ImageIO.write(originalImage, "png", new File(omdbResponse.getPoster()));
			LOGGER.info("adding poster to cache: " + omdbResponse.getPoster());
		} catch (Exception e) {
			LOGGER.error(e);
		}
		
		// adding to cache
		dbController.addCache(mainWindowController.getCurrentStreamUrl(), omdbResponse);
		dbController.setCached(mainWindowController.getCurrentStreamUrl());
		
		// load data to the MainWindowController
		Platform.runLater(() -> {
			dbController.readCache(mainWindowController.getCurrentStreamUrl());
		});
		
		return;
	}
	
	private JsonObject getByTitle(String title) {
		String output = null;
		URL apiUrl;
		try {		
			if (mainWindowController.getCurrentTableFilm().getSeason().length() > 0) {
				apiUrl = new URL(URL + mainWindowController.getOmdbAPIKey() + "&t="
						+ title.replace(" ", "%20")
						+ "&Season=" + mainWindowController.getCurrentTableFilm().getSeason()
						+ "&Episode=" + mainWindowController.getCurrentTableFilm().getEpisode());
			} else {
				apiUrl = new URL(URL + mainWindowController.getOmdbAPIKey() + "&t="
					+ title.replace(" ", "%20"));
			}

			BufferedReader ina = new BufferedReader(new InputStreamReader(apiUrl.openStream()));
			output = ina.readLine();
			ina.close();
			LOGGER.info("response from '" + URL + "&t=" + title + "' was:" + output);
		} catch (IOException e) {
			LOGGER.error("error while making api request or reading response");
			LOGGER.error("response from '" + URL + "&t=" + title + "' was:" + output, e);
			return null;
		}
		
		return Json.parse(output).asObject();
	}
	
	private String searchByTitle(String title) {
		String output = null;
		// if the movie was not found try to search it
		LOGGER.warn("Movie was not found at first try, searching again!");
		/**
		 * TODO split the name intelligent as it may contain the film title search for
		 * English name use tmdb
		 */
		try {
			URL apiUrl = new URL(URL + mainWindowController.getOmdbAPIKey() + "&s=" + title.replace(" ", "%20"));
			BufferedReader ina = new BufferedReader(new InputStreamReader(apiUrl.openStream()));
			output = ina.readLine();
			ina.close();
			LOGGER.info("response from '" + URL + "&s=" + title + "' was:" + output);
		} catch (Exception e) {
			LOGGER.error("error while making api request or reading response");
			LOGGER.error("response from '" + URL + "&s=" + title + "' was:" + output, e);
			return "";
		}

		JsonObject searchObject = Json.parse(output).asObject();
		if (searchObject.getString("Response", "").equals("True")) {
			for (JsonValue movie : searchObject.get("Search").asArray()) {
				// get first entry from the array and set object = movie
				return movie.asObject().getString("Title", "");
			}
		} else {
			LOGGER.warn("Movie not found! Not adding cache!");
		}
		return "";
	}
}
