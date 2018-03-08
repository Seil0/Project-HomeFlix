package kellerkinder.HomeFlix.controller;

import java.awt.Graphics2D;
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

import javafx.application.Platform;
import kellerkinder.HomeFlix.application.Main;
import kellerkinder.HomeFlix.application.MainWindowController;

public class OMDbAPIController implements Runnable {
	
	private MainWindowController mainWindowController;
	private DBController dbController;
	private Main main;
	private String[] responseString = new String[20];
	private String URL = "https://www.omdbapi.com/?apikey=";
	private static final Logger LOGGER = LogManager.getLogger(MainWindowController.class.getName());
	
	public OMDbAPIController(MainWindowController mainWindowController, DBController dbController, Main main){
		this.mainWindowController = mainWindowController;
		this.dbController = dbController;
		this.main = main;
		
		
	}

	@Override
	public void run() {
    	String output = null;
    	String posterPath = null;
		
		// get by title, TODO implement search
		try {
			URL apiUrl = new URL(URL + mainWindowController.getOmdbAPIKey() + "&t="
					+ mainWindowController.getTitle().replace(" ", "%20"));
			BufferedReader ina = new BufferedReader(new InputStreamReader(apiUrl.openStream()));
			output = ina.readLine();
			ina.close();
			LOGGER.info("response from " + URL + " was valid");
			LOGGER.info(output);
		} catch (IOException e) {
			LOGGER.error("error while making api request or reading response");
			LOGGER.error("response from " + URL + " was: \n" + output, e);
			return;
		}
		
		JsonObject object = Json.parse(output).asObject();
		
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
		
		//resize the image to fit in the posterImageView and add it to the cache
	    try {
			BufferedImage originalImage = ImageIO.read(new URL(responseString[18])); //change path to where file is located
		    int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		    BufferedImage resizeImagePNG = resizeImage(originalImage, type, 198, 297);
		    posterPath = main.getPosterCache() + "/" + mainWindowController.getTitle() + ".png";
			ImageIO.write(resizeImagePNG, "png", new File(posterPath));
			LOGGER.info("adding poster to cache: "+posterPath);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	    
		// adding strings to the cache
		dbController.addCache(mainWindowController.getStreamUrl(), responseString[0], responseString[1],
				responseString[2], responseString[3], responseString[4], responseString[5], responseString[6],
				responseString[7], responseString[8], responseString[9], responseString[10], responseString[11],
				responseString[12], responseString[13], responseString[14], responseString[15], responseString[16],
				responseString[17], posterPath, responseString[19]);
		dbController.setCached(mainWindowController.getStreamUrl());
		
		// load data to the MainWindowController
		Platform.runLater(() -> {
			dbController.readCache(mainWindowController.getStreamUrl());
		});
	}
	
	/**
	 * resize a image
	 * @param originalImage is the original image
	 * @param type of the original image
	 * @param IMG_WIDTH width to resize
	 * @param IMG_HEIGHT heigth to resize
	 * @return resized image
	 */
	private static BufferedImage resizeImage(BufferedImage originalImage, int type, int IMG_WIDTH, int IMG_HEIGHT) {
	    BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
	    Graphics2D g = resizedImage.createGraphics();
	    g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
	    g.dispose();

	    return resizedImage;
	}
}
