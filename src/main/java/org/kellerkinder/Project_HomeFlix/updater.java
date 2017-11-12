/**
 * updater for Project HomeFlix
 * checks for Updates and downloads it in case there is one
 */
package org.kellerkinder.Project_HomeFlix;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;

import org.apache.commons.io.FileUtils;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import javafx.application.Platform;

public class updater implements Runnable{
	
	private MainWindowController mainWindowController;
	private String buildNumber;
	private String apiOutput;
	private String updateBuildNumber;	//tag_name from Github
	private String browserDownloadUrl;	//update download link
	private String githubApi = "https://api.github.com/repos/Seil0/Project-HomeFlix/releases/latest";
	
	
	public updater(MainWindowController m, String buildNumber){
		mainWindowController=m;
		this.buildNumber=buildNumber;
	}
	
	public void run(){
		System.out.println("check for updates ...");
		Platform.runLater(() -> {
			mainWindowController.updateBtn.setText(mainWindowController.bundle.getString("checkingUpdates"));
         });

        try {
			URL githubApiUrl = new URL(githubApi);
	        BufferedReader ina = new BufferedReader(new InputStreamReader(githubApiUrl.openStream()));
			apiOutput = ina.readLine();
	        ina.close();
		} catch (IOException e1) {
			Platform.runLater(() -> {
				mainWindowController.showErrorMsg(mainWindowController.errorUpdateV, e1);
			});
		}

    	JsonObject object = Json.parse(apiOutput).asObject();
    	JsonArray objectAssets = Json.parse(apiOutput).asObject().get("assets").asArray();
    	
    	updateBuildNumber = object.getString("tag_name", "");
//    	updateName = object.getString("name", "");
//    	updateChanges = object.getString("body", "");
    	for (JsonValue asset : objectAssets) {
    		browserDownloadUrl = asset.asObject().getString("browser_download_url", "");
    		
    	}
		System.out.println("Build: "+buildNumber+", Update: "+updateBuildNumber);
		
		//Compares the program BuildNumber with the current BuildNumber if  program BuildNumber <  current BuildNumber then perform a update
		int iversion = Integer.parseInt(buildNumber);
		int iaktVersion = Integer.parseInt(updateBuildNumber.replace(".", ""));
		
		if(iversion >= iaktVersion){
			Platform.runLater(() -> {
				mainWindowController.updateBtn.setText(mainWindowController.bundle.getString("updateBtnNotavail"));
	         });
			System.out.println("no update available");
		}else{
			Platform.runLater(() -> {
				mainWindowController.updateBtn.setText(mainWindowController.bundle.getString("updateBtnavail"));
	         });
			System.out.println("update available");
			System.out.println("download link: " + browserDownloadUrl);
			try {		
				//open new Http connection, ProgressMonitorInputStream for downloading the data
				HttpURLConnection conn = (HttpURLConnection) new URL(browserDownloadUrl).openConnection();
				ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(null, "Downloading...", conn.getInputStream());
				ProgressMonitor pm = pmis.getProgressMonitor();
		        pm.setMillisToDecideToPopup(0);
		        pm.setMillisToPopup(0);
		        pm.setMinimum(0);// tell the progress bar that we start at the beginning of the stream
		        pm.setMaximum(conn.getContentLength());// tell the progress bar the total number of bytes we are going to read.
				FileUtils.copyInputStreamToFile(pmis, new File("ProjectHomeFlix_update.jar"));	//download update			
				org.apache.commons.io.FileUtils.copyFile(new File("ProjectHomeFlix_update.jar"), new File("ProjectHomeFlix.jar"));	//TODO rename update to old name
				org.apache.commons.io.FileUtils.deleteQuietly(new File("ProjectHomeFlix_update.jar"));	//delete update
				Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar");	//start again
				System.exit(0);	//finishes itself
				
			} catch (IOException e) {
				Platform.runLater(() -> {
					mainWindowController.showErrorMsg(mainWindowController.errorUpdateD, e);
				});
			}
		}
	}
}
