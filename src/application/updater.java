/**
 * updater for Project HomeFlix
 * checks for Updates and downloads it in case there is one
 */
package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;

import org.apache.commons.io.FileUtils;

import javafx.application.Platform;

public class updater implements Runnable{
	
	private MainWindowController mainWindowController;
	private String buildURL;
	private String downloadLink;
	private String updateBuildNumber;
	private String buildNumber;
	
	public updater(MainWindowController m, String buildURL,String downloadLink,String buildNumber){
		mainWindowController=m;
		this.buildURL=buildURL;
		this.downloadLink=downloadLink;
		this.buildNumber=buildNumber;
	}
	
	public void run(){
		System.out.println("check for updates ...");
		Platform.runLater(() -> {
			mainWindowController.updateBtn.setText(mainWindowController.bundle.getString("checkingUpdates"));
         });
		try {
			URL url = new URL(buildURL); //URL of the text file with the current build number
	        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	        updateBuildNumber = in.readLine();	//write InputStream in String
	        in.close();
		} catch (IOException e1) {
			mainWindowController.showErrorMsg(mainWindowController.errorUpdateV, e1);
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
			try {
				//get the download-Data URL
				URL downloadURL = new URL(downloadLink);
				BufferedReader in = new BufferedReader(new InputStreamReader(downloadURL.openStream()));
				String updateDataURL = in.readLine();
				
				//open new Http connection, ProgressMonitorInputStream for downloading the data
				HttpURLConnection conn = (HttpURLConnection) new URL(updateDataURL).openConnection();
				ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(null, "Downloading...", conn.getInputStream());
				ProgressMonitor pm = pmis.getProgressMonitor();
		        pm.setMillisToDecideToPopup(0);
		        pm.setMillisToPopup(0);
		        pm.setMinimum(0);// tell the progress bar that we start at the beginning of the stream
		        pm.setMaximum(conn.getContentLength());// tell the progress bar the total number of bytes we are going to read.
				FileUtils.copyInputStreamToFile(pmis, new File("ProjectHomeFlix.jar"));			
				
				Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar");	//start again
				System.exit(0);	//finishes itself
			} catch (IOException e) {
				//in case there is an error
				mainWindowController.showErrorMsg(mainWindowController.errorUpdateD, e);
			}
		}
	}
}
