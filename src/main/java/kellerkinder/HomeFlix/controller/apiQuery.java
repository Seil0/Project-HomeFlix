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
 * 
 */

package kellerkinder.HomeFlix.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import kellerkinder.HomeFlix.application.Main;
import kellerkinder.HomeFlix.application.MainWindowController;

public class apiQuery{
	
	public apiQuery(MainWindowController m, DBController db, Main main){
		mainWindowController=m;
		dbController=db;
		this.main = main;
	}
	
	private MainWindowController mainWindowController;
	private DBController dbController;
	private Main main;
	private Image im;
	private String[] responseString = new String[20];
	private String posterCache;
	private String apiURL = "https://www.omdbapi.com/?apikey=";
	private String apiKey = "";
	ArrayList<Text> responseText = new ArrayList<Text>();
	ArrayList<Text> nameText = new ArrayList<Text>();
	
	/**
	 * apiQuery for Project HomeFlix, sends a query to the omdb api
	 */
	public void startQuery(String titel, String streamUrl){
		URL queryURL = null;
		Scanner sc = null;
		String moviename = null;
		String retdata = null;
		String posterPath = null;
		InputStream is = null;
		BufferedReader br = null;
		String fontFamily = main.getFONT_FAMILY();
		posterCache = main.getPosterCache().toString();
		int fontSize = (int) Math.round(mainWindowController.size);
		
		responseText.removeAll(responseText);
		nameText.removeAll(nameText);

		try {

			//get film title
			sc = new Scanner(System.in);
			moviename = titel;

			// in case of no or "" Film title
			if (moviename == null || moviename.equals("")) {
				System.out.println("No movie found");
			}

			//remove unwanted blank
			moviename = moviename.trim();

			//replace blank with +
			moviename = moviename.replace(" ", "+");

			//queryURL is apiURL and additional parameters, response-types: http,json,xml (must be json, since the response is processed with minimal-json )
			queryURL = new URL(apiURL + apiKey + "&t=" + moviename + "&plot=full&r=json");
			is = queryURL.openStream();
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			//read data from response Stream
			while ((retdata = br.readLine()) != null) {
				//cut the json response into separate strings
				System.out.println(retdata);
				JsonObject object = Json.parse(retdata).asObject();
				
				responseString[0] = object.getString("Title", "");
				responseString[1] = object.getString("Year", "");
				responseString[2] = object.getString("Rated", "");
				responseString[3] = object.getString("Released", "");
				responseString[4] = object.getString("Runtime", "");
				responseString[5] = object.getString("Genre", "");
				responseString[6] = object.getString("Director", "");
				responseString[7] = object.getString("Writer", "");
				responseString[8] = object.getString("Actors", "");
				responseString[9] = object.getString("Plot", "");
				responseString[10] = object.getString("Language", "");
				responseString[11] = object.getString("Country", "");
				responseString[12] = object.getString("Awards", "");
				responseString[13] = object.getString("Metascore", "");
				responseString[14] = object.getString("imdbRating", "");
				responseString[15] = object.getString("Type", "");
				responseString[16] = object.getString("imdbVotes", "");
				responseString[17] = object.getString("imdbID", "");
				responseString[18] = object.getString("Poster", "");
				responseString[19] = object.getString("Response", "");

				//adding poster to cache
				BufferedImage originalImage = ImageIO.read(new URL(responseString[18]));//change path to where file is located
			    int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
			    BufferedImage resizeImagePNG = resizeImage(originalImage, type, 198, 297);
				if (System.getProperty("os.name").equals("Linux")) {
			    	posterPath = posterCache+"/"+titel+".png";
			    	ImageIO.write(resizeImagePNG, "png", new File(posterCache+"/"+titel+".png")); //change path where you want it saved
			    } else {
			    	ImageIO.write(resizeImagePNG, "png", new File(posterCache+"\\"+titel+".png")); //change path where you want it saved
			    	posterPath = posterCache+"\\"+titel+".png";
			    }
		    	System.out.println("adding poster to cache: "+posterPath);
				
				//adding strings to the cache
				dbController.addCache(	streamUrl, responseString[0], responseString[1],responseString[2], responseString[3], responseString[4], responseString[5],
										responseString[6], responseString[7], responseString[8], responseString[9], responseString[10],responseString[11], responseString[12],
										responseString[13], responseString[14], responseString[15], responseString[16], responseString[17], posterPath,
										responseString[19]);
				dbController.setCached(streamUrl);
				
				for (int i = 0; i < 20; i++) {
					Text text = new Text(responseString[i] + "\n");
					responseText.add(text);
					responseText.get(i).setFont(Font.font(fontFamily, fontSize));
				}

				// if response == false then show mainWindowController.noFilmFound else create new Texts and add them to flowText
				if (retdata.contains("\"Response\":\"False\"")) { // TODO + FIXME
					mainWindowController.getTextFlow().getChildren().add(new Text(mainWindowController.noFilmFound));
					im = new Image("resources/icons/close_black_2048x2048.png");
					mainWindowController.getImage1().setImage(im);
				} else {
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

					for (int i = 0; i < nameText.size(); i++) {
						nameText.get(i).setFont(Font.font(fontFamily, FontWeight.BOLD, fontSize));
					}

					mainWindowController.getTextFlow().getChildren().remove(0,
							mainWindowController.getTextFlow().getChildren().size());

					for (int i = 0; i < nameText.size(); i++) {
						mainWindowController.getTextFlow().getChildren().addAll(nameText.get(i), responseText.get(i));
					}

					// if there is no poster
					if (responseString[18].equals("N/A")) {
						im = new Image("resources/icons/close_black_2048x2048.png");
					} else {
						im = new Image(responseString[18]);
					}
					mainWindowController.getImage1().setImage(im);
				}
			}

		} catch (Exception e) {
			mainWindowController.getTextFlow().getChildren().remove(0, mainWindowController.getTextFlow().getChildren().size());
			mainWindowController.getTextFlow().getChildren().add(new Text(e.toString()));
			System.out.println(e);
		} finally {
			//closes datainputStream, InputStream,Scanner if not already done
			try {
				if (br != null) {
					br.close();
				}

				if (is != null) {
					is.close();
				}

				if (sc != null) {
					sc.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
		
	private static BufferedImage resizeImage(BufferedImage originalImage, int type, int IMG_WIDTH, int IMG_HEIGHT) {
		    BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
		    Graphics2D g = resizedImage.createGraphics();
		    g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		    g.dispose();

		    return resizedImage;
	}
}
