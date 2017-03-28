/**
 * updater for Project HomeFlix
 * checks for Updates and downloads it in case there is one
 */
package application;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javafx.application.Platform;

//TODO rework the process after the update is downloaded, need to replace the old config.xml
public class updater extends Thread{
	
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
				URL website;
				URL downloadURL = new URL(downloadLink);
				BufferedReader in = new BufferedReader(new InputStreamReader(downloadURL.openStream()));
				String updateDataURL = in.readLine();
				website = new URL(updateDataURL);	//Update URL
				ReadableByteChannel rbc = Channels.newChannel(website.openStream());	//open new Stream/Channel
				FileOutputStream fos = new FileOutputStream("ProjectHomeFlix.jar");	//new FileOutputStream for ProjectHomeFLix.jar
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);	//gets file from 0 to max size
				fos.close();	//close fos (extrem wichtig!)
				Runtime.getRuntime().exec("java -jar ProjectHomeFlix.jar");	//start again
				System.exit(0);	//finishes itself
			} catch (IOException e) {
				//in case there is an error
				mainWindowController.showErrorMsg(mainWindowController.errorUpdateD, e);
			}
		}
	}
}
