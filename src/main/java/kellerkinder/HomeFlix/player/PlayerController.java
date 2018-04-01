package kellerkinder.HomeFlix.player;

import java.io.File;

import com.jfoenix.controls.JFXButton;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;

public class PlayerController {

	@FXML
	private MediaView mediaView;

	@FXML
	private HBox controllsHBox;

	@FXML
	private JFXButton playBtn;

	@FXML
	private JFXButton fullscreenBtn;

	private Player player;
	private Media media;
	private MediaPlayer mediaPlayer;

	public void init(String file, Player player) {
		this.player = player;
		
		media = new Media(new File(file).toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		mediaView.setMediaPlayer(mediaPlayer);

		final DoubleProperty width = mediaView.fitWidthProperty();
		final DoubleProperty height = mediaView.fitHeightProperty();

		width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
		height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

		mediaView.setPreserveRatio(true);
		mediaPlayer.play();
	}

	@FXML
	void fullscreenBtnAction(ActionEvent event) {	
		if (player.getStage().isFullScreen()) {
			player.getStage().setFullScreen(false);
		} else {
			player.getStage().setFullScreen(true);
		}
	}

	@FXML
	void playBtnAction(ActionEvent event) {

		if (mediaPlayer.getStatus().equals(Status.PLAYING)) {
			mediaPlayer.pause();
		} else {
			mediaPlayer.play();
		}
	}

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

}
