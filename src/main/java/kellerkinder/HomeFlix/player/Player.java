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
package kellerkinder.HomeFlix.player;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import kellerkinder.HomeFlix.application.Main;
import kellerkinder.HomeFlix.controller.DBController;
import kellerkinder.HomeFlix.datatypes.FilmTabelDataType;

public class Player {
	
	private PlayerController playerController;
	private DBController dbController;
	private Stage stage;
	private AnchorPane pane;
	private Scene scene;
	
	/**
	 * generate a new PlayerWindow
	 * @param entry 		the film object
	 * @param dbController	the dbController object
	 */
	public Player(FilmTabelDataType film, DBController dbController) {
		this.dbController = dbController;
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("fxml/PlayerWindow.fxml"));
			pane = (AnchorPane) fxmlLoader.load();
			stage = new Stage();
			scene = new Scene(pane);
			stage.setScene(scene);
			stage.setTitle("HomeFlix");
			stage.getIcons().add(new Image(Main.class.getResourceAsStream("/icons/Homeflix_Icon_64x64.png")));
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					dbController.setCurrentTime(film.getStreamUrl(), playerController.getCurrentTime());
					playerController.getMediaPlayer().stop();
					stage.close();
				}
			});
			
			playerController = fxmlLoader.getController();
			playerController.init(film, this, dbController);
			
			stage.setFullScreen(true);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void playNewFilm(FilmTabelDataType film) {
		playerController.init(film, this, dbController);
	}

	public Stage getStage() {
		return stage;
	}

	public Parent getPane() {
		return pane;
	}
	
	public Scene getScene() {
		return scene;
	}
	
}
