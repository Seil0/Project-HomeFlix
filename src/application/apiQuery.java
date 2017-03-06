/**
 * apiQuery for Project HomeFlix
 * sends a query to the omdb api
 */
package application;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

@SuppressWarnings("unused") //TODO
public class apiQuery{
	
	public apiQuery(MainWindowController m, DBController db){
		mainWindowController=m;
		dbController=db;
	}
	
	private MainWindowController mainWindowController;
	private DBController dbController;
	private Image im;
	private int fontSize = 20;
	private String fontFamily = "System";
	
	void startQuery(String titel, String streamUrl){
		URL url = null;
		Scanner sc = null;
		String apiurl = "https://www.omdbapi.com/?";	//API URL
		String moviename = null;
		String dataurl = null;
		String retdata = null;
		InputStream is = null;
		BufferedReader br = null;

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

			//replace blank with + for api-query
			moviename = moviename.replace(" ", "+");

			//URL wird zusammengestellt abfragetypen: http,json,xml (muss json sein um späteres trennen zu ermöglichen)
			dataurl = apiurl + "t=" + moviename + "&plot=full&r=json";

			url = new URL(dataurl);
			is = url.openStream();
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			// lesen der Daten aus dem Antwort Stream
			while ((retdata = br.readLine()) != null) {
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

				String metascoreV = object.getString("Metascore", "");
				String imdbRatingV = object.getString("imdbRating", "");
				String imdbVotesV = object.getString("imdbVotes", "");
				String imdbIDV = object.getString("imdbID", "");
				String typeV = object.getString("Type", "");
				
				String posterURL = object.getString("Poster", "");
				String response = object.getString("Response", "");
				
				dbController.addCache(	streamUrl, titelV, yearV, ratedV, releasedV, runtimeV, genreV, directorV, writerV, actorsV, plotV, languageV, countryV,
										awardsV, metascoreV, imdbRatingV, imdbVotesV, imdbIDV, typeV, posterURL, response);
				dbController.setCached(streamUrl);

				
//				Text titelR = new Text (object.getString("Title", "")+"\n");
//				titelR.setFont(Font.font (fontFamily, fontSize));
//				Text yearR = new Text (object.getString("Year", "")+"\n");
//				yearR.setFont(Font.font (fontFamily, fontSize));
//				Text ratedR = new Text (object.getString("Rated", "")+"\n");
//				ratedR.setFont(Font.font (fontFamily, fontSize));
//				Text releasedR = new Text (object.getString("Released", "")+"\n");
//				releasedR.setFont(Font.font (fontFamily, fontSize));
//				Text runtimeR = new Text (object.getString("Runtime", "")+"\n");
//				runtimeR.setFont(Font.font (fontFamily, fontSize));
//				Text genreR = new Text (object.getString("Genre", ""));
//				genreR.setFont(Font.font (fontFamily, fontSize));
//				Text directorR = new Text (object.getString("Director", "")+"\n");
//				directorR.setFont(Font.font (fontFamily, fontSize));
//				Text writerR = new Text (object.getString("Writer", "")+"\n");
//				writerR.setFont(Font.font (fontFamily, fontSize));
//				Text actorsR  = new Text (object.getString("Actors", "")+"\n");
//				actorsR.setFont(Font.font (fontFamily, fontSize));
//				Text plotR = new Text (object.getString("Plot", "")+"\n");
//				plotR.setFont(Font.font (fontFamily, fontSize));
//				Text languageR = new Text (object.getString("Language", "")+"\n");
//				languageR.setFont(Font.font (fontFamily, fontSize));
//				Text countryR = new Text (object.getString("Country", "")+"\n");
//				countryR.setFont(Font.font (fontFamily, fontSize));
//				Text awardsR = new Text (object.getString("Awards", "")+"\n");
//				awardsR.setFont(Font.font (fontFamily, fontSize));
//				Text metascoreR = new Text (object.getString("Metascore", "")+"\n");
//				metascoreR.setFont(Font.font (fontFamily, fontSize));
//				Text imdbRatingR = new Text (object.getString("imdbRating", "")+"\n");
//				imdbRatingR.setFont(Font.font (fontFamily, fontSize));
//				@SuppressWarnings("unused")
//				Text imdbVotesR = new Text (object.getString("imdbVotes", "")+"\n");
//				imdbVotesR.setFont(Font.font (fontFamily, fontSize));
//				@SuppressWarnings("unused")
//				Text imdbIDR = new Text (object.getString("imdbID", "")+"\n");
//				imdbIDR.setFont(Font.font (fontFamily, fontSize));
//				Text typeR = new Text (object.getString("Type", "")+"\n");
//				typeR.setFont(Font.font (fontFamily, fontSize));
				
				
				if(response.equals("False")){
					mainWindowController.ta1.appendText(mainWindowController.noFilmFound);
					im = new Image("recources/icons/close_black_2048x2048.png");
					mainWindowController.image1.setImage(im);
				}else{
				//ausgabe des Textes in ta1 in jeweils neuer Zeile 
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
					
//					mainWindowController.ta1.setVisible(false);
					
//					Text title = new Text(15, 20, mainWindowController.title+": ");
//					title.setFont(Font.font (fontFamily, FontWeight.BOLD, fontSize));		
//					Text year = new Text(15, 20, mainWindowController.year+": ");
//					year.setFont(Font.font (fontFamily, FontWeight.BOLD, fontSize));	
//					Text rating = new Text(15, 20, mainWindowController.rating+": ");
//					rating.setFont(Font.font (fontFamily, FontWeight.BOLD, fontSize));		
//					Text publishedOn = new Text(15, 20, mainWindowController.publishedOn+": ");
//					publishedOn.setFont(Font.font (fontFamily, FontWeight.BOLD, fontSize));		
//					Text duration = new Text(15, 20, mainWindowController.duration+": ");
//					duration.setFont(Font.font (fontFamily, FontWeight.BOLD, fontSize));			
//					Text genre = new Text(15, 20, mainWindowController.genre+": ");
//					genre.setFont(Font.font (fontFamily, FontWeight.BOLD, fontSize));
//					Text director = new Text(15, 20, mainWindowController.director+": ");
//					director.setFont(Font.font (fontFamily, FontWeight.BOLD, fontSize));			
//					Text writer = new Text(15, 20, mainWindowController.writer+": ");
//					writer.setFont(Font.font (fontFamily, FontWeight.BOLD, fontSize));			
//					Text actors = new Text(15, 20, mainWindowController.actors+": ");
//					actors.setFont(Font.font (fontFamily, FontWeight.BOLD, fontSize));			
//					Text plot = new Text(15, 20, mainWindowController.plot+": ");
//					plot.setFont(Font.font (fontFamily, FontWeight.BOLD, fontSize));			
//					Text language = new Text(15, 20, mainWindowController.language+": ");
//					language.setFont(Font.font (fontFamily, FontWeight.BOLD, fontSize));				
//					Text country = new Text(15, 20, mainWindowController.country+": ");
//					country.setFont(Font.font (fontFamily, FontWeight.BOLD, fontSize));				
//					Text awards = new Text(15, 20, mainWindowController.awards+": ");
//					awards.setFont(Font.font (fontFamily, FontWeight.BOLD, fontSize));		
//					Text metascore = new Text(15, 20, mainWindowController.metascore+": ");
//					metascore.setFont(Font.font (fontFamily, FontWeight.BOLD, fontSize));
//					Text imdbRating = new Text(15, 20, mainWindowController.imdbRating+": ");
//					imdbRating.setFont(Font.font (fontFamily, FontWeight.BOLD, fontSize));					
//					Text type = new Text(15, 20, mainWindowController.type+": ");
//					type.setFont(Font.font (fontFamily, FontWeight.BOLD, fontSize));
//				
//					mainWindowController.textFlow.getChildren().remove(0, mainWindowController.textFlow.getChildren().size());
//					
//					ObservableList<Node> list = mainWindowController.textFlow.getChildren();
//					
//					list.addAll(title,titelR,year,yearR,rating,ratedR,
//						   publishedOn,releasedR,duration,runtimeR,genre,genreR,director,directorR,writer,writerR,
//						   actors,actorsR,plot,plotR,language,languageR,country,countryR,awards,awardsR,metascore,
//						   metascoreR,imdbRating,imdbRatingR,type,typeR);
//					
				
					if(posterURL.equals("N/A")){
						im = new Image("recources/icons/close_black_2048x2048.png");
					}else{
						im = new Image(posterURL);
					}
					mainWindowController.image1.setImage(im);
				}
			}

		} catch (Exception e) {
			mainWindowController.ta1.setText(e.toString());
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
			} catch (Exception e2) {
				;
			}
		}
	}
}
