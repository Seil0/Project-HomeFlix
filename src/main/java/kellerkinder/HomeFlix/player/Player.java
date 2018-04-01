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

public class Player {
	
	private PlayerController playerController;
	private Stage stage;
	private AnchorPane pane;
	
	public Player(String file) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("fxml/PlayerWindow.fxml"));
			pane = (AnchorPane) fxmlLoader.load();
			stage = new Stage();
			stage.setScene(new Scene(pane));
			stage.setTitle("HomeFlix");
			stage.getIcons().add(new Image(Main.class.getResourceAsStream("/icons/Homeflix_Icon_64x64.png")));
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					playerController.getMediaPlayer().stop();
				}
			});
			
			playerController = fxmlLoader.getController();
			playerController.init(file, this);
			
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
	
}
