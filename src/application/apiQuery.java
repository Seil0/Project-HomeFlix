/**
 * apiQuery for Project HomeFlix
 * sends a query to the omdb api
 * 
 * TODO build in a caching function
 */
package application;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import javafx.scene.image.Image;

public class apiQuery{
	
	public apiQuery(MainWindowController m){
		mainWindowController=m;
	}
	
	private MainWindowController mainWindowController;
	private Image im;
	
	@SuppressWarnings("deprecation")
	void startQuery(String input){
		URL url = null;
		Scanner sc = null;
		String apiurl = "https://www.omdbapi.com/?";	//API URL
		String moviename = null;
		String dataurl = null;
		String retdata = null;
		InputStream is = null;
		DataInputStream dis = null;

		try {

			//get film title
			sc = new Scanner(System.in);
			moviename = input;

			// in case of no or "" Film title
			if (moviename == null || moviename.equals("")) {
				System.out.println("No movie found");
			}

			//remove unwanted blank
			moviename = moviename.trim();

			//replace blank with + for api-query
			moviename = moviename.replace(" ", "+");

			//URL wird zusammengestellt abfragetypen: http,json,xml (muss json sein um späteres trennen zu ermöglichen)
			dataurl = apiurl + "t=" + moviename + "&plot=full&r=json";

			url = new URL(dataurl);
			is = url.openStream();
			dis = new DataInputStream(is);

			// lesen der Daten aus dem Antwort Stream
			while ((retdata = dis.readLine()) != null) {
				//retdata in json object parsen und anschließend das json Objekt "zerschneiden"
				System.out.println(retdata);
				JsonObject object = Json.parse(retdata).asObject();
				String titelV = object.getString("Title", "");
				String yearV = object.getString("Year", "");
				String ratedV = object.getString("Rated", "");
				String releasedV = object.getString("Released", "");
				String runtimeV = object.getString("Runtime", "");
				String genreV = object.getString("Genre", "");
				String directorV = object.getString("Director", "");
				String writerV = object.getString("Writer", "");
				String actorsV  = object.getString("Actors", "");
				String plotV = object.getString("Plot", "");
				String languageV = object.getString("Language", "");
				String countryV = object.getString("Country", "");
				String awardsV = object.getString("Awards", "");
				String posterURL = object.getString("Poster", "");
				String metascoreV = object.getString("Metascore", "");
				String imdbRatingV = object.getString("imdbRating", "");
				@SuppressWarnings("unused")
				String imdbVotesV = object.getString("imdbVotes", "");
				@SuppressWarnings("unused")
				String imdbIDV = object.getString("imdbID", "");
				String typeV = object.getString("Type", "");
				String response = object.getString("Response", "");
				
				
				if(response.equals("False")){
					mainWindowController.ta1.appendText(mainWindowController.noFilmFound);
					im = new Image("recources/icons/close_black_2048x2048.png");
					mainWindowController.image1.setImage(im);
				}else{
				//ausgabe des Textes in ta1 in jeweils neuer Zeile //TODOformatting
					mainWindowController.ta1.appendText(mainWindowController.title+": "+titelV+"\n");
					mainWindowController.ta1.appendText(mainWindowController.year+": "+ yearV+"\n");
					mainWindowController.ta1.appendText(mainWindowController.rating+": "+ratedV+"\n");
					mainWindowController.ta1.appendText(mainWindowController.publishedOn+": "+releasedV+"\n");
					mainWindowController.ta1.appendText(mainWindowController.duration+": "+runtimeV+"\n");
					mainWindowController.ta1.appendText(mainWindowController.genre+": "+genreV+"\n");
					mainWindowController.ta1.appendText(mainWindowController.director+": "+directorV+"\n");
					mainWindowController.ta1.appendText(mainWindowController.writer+": "+writerV+"\n");
					mainWindowController.ta1.appendText(mainWindowController.actors+": "+actorsV+"\n");
					mainWindowController.ta1.appendText(mainWindowController.plot+": "+plotV+"\n");
					mainWindowController.ta1.appendText(mainWindowController.language+": "+languageV+"\n");
					mainWindowController.ta1.appendText(mainWindowController.country+": "+countryV+"\n");
					mainWindowController.ta1.appendText(mainWindowController.awards+": "+awardsV+"\n");
					mainWindowController.ta1.appendText(mainWindowController.metascore+": "+metascoreV+"\n");
					mainWindowController.ta1.appendText(mainWindowController.imdbRating+": "+imdbRatingV+"\n");
					mainWindowController.ta1.appendText(mainWindowController.type+": "+typeV+"\n");
				
					if(posterURL.equals("N/A")){
						im = new Image("recources/icons/close_black_2048x2048.png");
					}else{
						im = new Image(posterURL);
					}
					mainWindowController.image1.setImage(im);
				}
			}

		} catch (Exception e) {
			System.out.println(e);
		} finally {
			//closes datainputStream, InputStream,Scanner if not already done
			try {
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
