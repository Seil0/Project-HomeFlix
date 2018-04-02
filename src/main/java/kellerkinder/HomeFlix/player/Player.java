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

public class Player {
	
	private PlayerController playerController;
	private Stage stage;
	private AnchorPane pane;
	private Scene scene;
	
	public Player(String file, String currentEp, DBController dbController) {
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
					dbController.setCurrentTime(file, playerController.getCurrentTime());
					playerController.getMediaPlayer().stop();
					stage.close();
				}
			});
			
			playerController = fxmlLoader.getController();
			playerController.init(file, currentEp, this, dbController);
			
			stage.setFullScreen(true);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
