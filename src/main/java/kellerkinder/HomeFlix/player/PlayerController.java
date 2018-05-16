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

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import kellerkinder.HomeFlix.application.MainWindowController;
import kellerkinder.HomeFlix.datatypes.FilmTabelDataType;

public class PlayerController {

	@FXML
	private MediaView mediaView;

	@FXML
	private VBox bottomVBox;

	@FXML
	private HBox controlsHBox;

	@FXML
	private JFXSlider timeSlider;

	@FXML
	private JFXButton stopBtn;
	@FXML
	private JFXButton playBtn;
	@FXML
	private JFXButton fullscreenBtn;

	private Player player;
	private MainWindowController mainWCon;
	private Media media;
	private MediaPlayer mediaPlayer;

	private FilmTabelDataType film;
	private double currentTime = 0;
	private double seekTime = 0;
	private double startTime = 0;
	private double duration = 0;
	private boolean mousePressed = false;
	private boolean showControls = true;
	private boolean autoplay;

	private ImageView stop_black = new ImageView(new Image("icons/ic_stop_black_24dp_1x.png"));
	private ImageView play_arrow_black = new ImageView(new Image("icons/ic_play_arrow_black_24dp_1x.png"));
	private ImageView pause_black = new ImageView(new Image("icons/ic_pause_black_24dp_1x.png"));
	private ImageView fullscreen_black = new ImageView(new Image("icons/ic_fullscreen_black_24dp_1x.png"));
	private ImageView fullscreen_exit_black = new ImageView(new Image("icons/ic_fullscreen_exit_black_24dp_1x.png"));

	/** FIXME double set currentTime()
	 * initialize the new PlayerWindow
	 * @param entry			the film object
	 * @param player		the player object (needed for closing action)
	 * @param dbController	the dbController object
	 */
	public void init(MainWindowController mainWCon, Player player, FilmTabelDataType film) {
		this.mainWCon = mainWCon;
		this.player = player;
		this.film = film;
		startTime = mainWCon.getDbController().getCurrentTime(film.getStreamUrl());
		autoplay = mainWCon.isAutoplay();
		initActions();

		if (film.getStreamUrl().startsWith("http")) {
			media = new Media(film.getStreamUrl());
		} else {
			media = new Media(new File(film.getStreamUrl()).toURI().toString());
		}
		startTime = mainWCon.getDbController().getCurrentTime(film.getStreamUrl());
		autoplay = mainWCon.isAutoplay();

		mediaPlayer = new MediaPlayer(media);
		mediaView.setPreserveRatio(true);
		mediaView.setMediaPlayer(mediaPlayer);

		final DoubleProperty width = mediaView.fitWidthProperty();
		final DoubleProperty height = mediaView.fitHeightProperty();

		width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
		height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

		// start the media if the player is ready
		mediaPlayer.setOnReady(new Runnable() {
			@Override
			public void run() {
				duration = media.getDuration().toMillis();

				timeSlider.setMax((duration / 1000) / 60);

				mediaPlayer.play();
				mediaPlayer.seek(Duration.millis(startTime));
			}
		});

		// every time the play time changes execute this
		mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
			@Override
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
				currentTime = newValue.toMillis(); // set the current time
				int episode = !film.getEpisode().isEmpty() ? Integer.parseInt(film.getEpisode()) : 0;
				int season = !film.getSeason().isEmpty() ? Integer.parseInt(film.getSeason()) : 0;
				
				// if we are end time -10 seconds, do autoplay, if activated
				if ((duration - currentTime) < 10000 && episode != 0 && autoplay) {
					autoplay = false;
					mainWCon.getDbController().setCurrentTime(film.getStreamUrl(), 0); // reset old video start time
					FilmTabelDataType nextFilm = mainWCon.getDbController().getNextEpisode(film.getTitle(), episode, season);
					if (nextFilm != null) {
						mediaPlayer.stop();
						init(mainWCon, player, nextFilm);
						autoplay = true;
					}
				} else if ((duration - currentTime) < 120) {
					// if we are -20ms stop the media
					mediaPlayer.stop();
					mainWCon.getDbController().setCurrentTime(film.getStreamUrl(), 0); // reset old video start time
				}

				if (!mousePressed) {
					timeSlider.setValue((currentTime / 1000) / 60);
				}
			}
		});

		// set the control elements to the correct value
		stopBtn.setGraphic(stop_black);
		playBtn.setGraphic(pause_black);
		fullscreenBtn.setGraphic(fullscreen_exit_black);
		timeSlider.setValue(0);
	}

	/**
	 * initialize some PlayerWindow GUI-Elements actions
	 */
	private void initActions() {

		player.getScene().addEventFilter(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
			// hide controls timer initialization
			final Timer timer = new Timer();
			TimerTask controlAnimationTask = null; // task to execute save operation
			final long delayTime = 2000; // hide the controls after 2 seconds

			@Override
			public void handle(MouseEvent mouseEvent) {

				// show controls
				if (!showControls) {
					player.getScene().setCursor(Cursor.DEFAULT);
					bottomVBox.setVisible(true);
				}

				// hide controls
				if (controlAnimationTask != null)
					controlAnimationTask.cancel();

				controlAnimationTask = new TimerTask() {
					@Override
					public void run() {
						bottomVBox.setVisible(false);
						player.getScene().setCursor(Cursor.NONE);
						showControls = false;
					}
				};
				timer.schedule(controlAnimationTask, delayTime);
			}
		});

		// if the mouse on the timeSlider is released seek to the new position
		timeSlider.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mediaPlayer.seek(new Duration(seekTime));
				mousePressed = false;
			}
		});

		timeSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mousePressed = true;
			}
		});

		// get the new seek time
		timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				seekTime = (double) new_val * 1000 * 60;
			}
		});
	}

	@FXML
	void stopBtnAction(ActionEvent event) {
		mainWCon.getDbController().setCurrentTime(film.getStreamUrl(), currentTime);
		mediaPlayer.stop();
		player.getStage().close();
	}

	@FXML
	void fullscreenBtnAction(ActionEvent event) {
		if (player.getStage().isFullScreen()) {
			player.getStage().setFullScreen(false);
			fullscreenBtn.setGraphic(fullscreen_black);
		} else {
			player.getStage().setFullScreen(true);
			fullscreenBtn.setGraphic(fullscreen_exit_black);
		}
	}

	@FXML
	void playBtnAction(ActionEvent event) {

		if (mediaPlayer.getStatus().equals(Status.PLAYING)) {
			mediaPlayer.pause();
			playBtn.setGraphic(play_arrow_black);
		} else {
			mediaPlayer.play();
			playBtn.setGraphic(pause_black);
		}
	}

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	public double getCurrentTime() {
		return currentTime;
	}

}
