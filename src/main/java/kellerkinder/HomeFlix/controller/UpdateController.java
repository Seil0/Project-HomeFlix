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
 */
package kellerkinder.HomeFlix.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import javafx.application.Platform;
import kellerkinder.HomeFlix.application.MainWindowController;

public class UpdateController implements Runnable {

	private MainWindowController mainWindowController;
	private String buildNumber;
	private String apiOutput;
	private String updateBuildNumber; // tag_name from Github
//	private String updateName;
//	private String updateChanges;
	private String browserDownloadUrl; // update download link
	private String githubApiRelease = "https://api.github.com/repos/Seil0/Project-HomeFlix/releases/latest";
	private String githubApiBeta = "https://api.github.com/repos/Seil0/Project-HomeFlix/releases";
	
	private URL githubApiUrl;
	private boolean useBeta;
	private static final Logger LOGGER = LogManager.getLogger(UpdateController.class.getName());

	/**
	 * updater for Project HomeFlix based on cemu_UIs, checks for Updates and download it
	 */
	public UpdateController(MainWindowController mwc, String buildNumber, boolean useBeta) {
		mainWindowController = mwc;
		this.buildNumber = buildNumber;
		this.useBeta = useBeta;
	}
	
	@Override
	public void run() {
		LOGGER.info("beta:" + useBeta + "; checking for updates ...");
		Platform.runLater(() -> {
			mainWindowController.getUpdateBtn().setText(mainWindowController.getBundle().getString("updateBtnChecking"));
		});
		
		try {

			if (useBeta) {
				githubApiUrl = new URL(githubApiBeta);
			} else {
				githubApiUrl = new URL(githubApiRelease);
			}

			// URL githubApiUrl = new URL(githubApiRelease);
			BufferedReader ina = new BufferedReader(new InputStreamReader(githubApiUrl.openStream()));
			apiOutput = ina.readLine();
			ina.close();
		} catch (IOException e) {
			Platform.runLater(() -> {
				LOGGER.error("could not check update version", e);
			});
		}
		
		if (useBeta) {
			JsonArray objectArray = Json.parse("{\"items\": " + apiOutput + "}").asObject().get("items").asArray();
			JsonValue object = objectArray.get(0);
			JsonArray objectAssets = object.asObject().get("assets").asArray();

			updateBuildNumber = object.asObject().getString("tag_name", "");
//			updateName = object.asObject().getString("name", "");
//			updateChanges = object.asObject().getString("body", "");

			for (JsonValue asset : objectAssets) {
				browserDownloadUrl = asset.asObject().getString("browser_download_url", "");
			}

		} else {
			JsonObject object = Json.parse(apiOutput).asObject();
			JsonArray objectAssets = Json.parse(apiOutput).asObject().get("assets").asArray();

			updateBuildNumber = object.getString("tag_name", "");
//			updateName = object.getString("name", "");
//			updateChanges = object.getString("body", "");
			for (JsonValue asset : objectAssets) {
				browserDownloadUrl = asset.asObject().getString("browser_download_url", "");

			}
		}
		
		LOGGER.info("Build: " + buildNumber + ", Update: " + updateBuildNumber);

		// Compares the program BuildNumber with the current BuildNumber if program
		// BuildNumber < current BuildNumber then perform a update
		int iversion = Integer.parseInt(buildNumber);
		int iaktVersion = Integer.parseInt(updateBuildNumber.replace(".", ""));

		if (iversion >= iaktVersion) {
			Platform.runLater(() -> {
				mainWindowController.getUpdateBtn().setText(mainWindowController.getBundle().getString("updateBtnNoUpdateAvailable"));
			});
			LOGGER.info("no update available");
		} else {
			Platform.runLater(() -> {
				mainWindowController.getUpdateBtn().setText(mainWindowController.getBundle().getString("updateBtnUpdateAvailable"));
			});
			LOGGER.info("update available");
			LOGGER.info("download link: " + browserDownloadUrl);
			try {
				// open new Http connection, ProgressMonitorInputStream for downloading the data
				HttpURLConnection connection = (HttpURLConnection) new URL(browserDownloadUrl).openConnection();
				ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(null, "Downloading...", connection.getInputStream());
				ProgressMonitor pm = pmis.getProgressMonitor();
				pm.setMillisToDecideToPopup(0);
				pm.setMillisToPopup(0);
				pm.setMinimum(0);// set beginning of the progress bar to 0
				pm.setMaximum(connection.getContentLength());// set the end to the file length
				FileUtils.copyInputStreamToFile(pmis, new File("ProjectHomeFlix_update.jar")); // download update
				org.apache.commons.io.FileUtils.copyFile(new File("ProjectHomeFlix_update.jar"), new File("ProjectHomeFlix.jar"));
				org.apache.commons.io.FileUtils.deleteQuietly(new File("ProjectHomeFlix_update.jar")); // delete update
				Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar"); // start again TODO consider ProcessBuilder to execute
				System.exit(0); // finishes itself
			} catch (IOException e) {
				Platform.runLater(() -> {
					LOGGER.info("could not download update files", e);
				});
			}
		}

	}

}
