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

public class updater {
	
	public updater(MainWindowController m){
		mainWindowController=m;
	}

	private MainWindowController mainWindowController;
	
	void update(String buildURL,String downloadLink,String aktBuildNumber,String buildNumber){
		System.out.println("check for updates ...");
		try {
			URL url = new URL(buildURL); //URL der Datei mit aktueller Versionsnummer
	        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	        aktBuildNumber = in.readLine();	//schreibt inputstream in String
	        in.close();
		} catch (IOException e1) {
			mainWindowController.showErrorMsg(mainWindowController.errorUpdateV, e1);
		}
		System.out.println("Build: "+buildNumber+", Update: "+aktBuildNumber);
		
		//vergleicht die Versionsnummern, bei aktversion > version wird ein Update durchgrf�hrt
		int iversion = Integer.parseInt(buildNumber);
		int iaktVersion = Integer.parseInt(aktBuildNumber.replace(".", ""));
		
		if(iversion >= iaktVersion){
			mainWindowController.updateBtn.setText(mainWindowController.bundle.getString("updateBtnNotavail"));
			System.out.println("no update available");
		}else{
			mainWindowController.updateBtn.setText(mainWindowController.bundle.getString("updateBtnavail"));
			System.out.println("update available");
		try {
			URL website;
			URL downloadURL = new URL(downloadLink);
			BufferedReader in = new BufferedReader(new InputStreamReader(downloadURL.openStream()));
			String updateDataURL = in.readLine();
			website = new URL(updateDataURL);	//Update URL
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());	//open new Stream/Channel
			FileOutputStream fos = new FileOutputStream("ProjectHomeFlix.jar");	//nea fileoutputstram for ProjectHomeFLix.jar
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
