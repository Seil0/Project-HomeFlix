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
import kellerkinder.HomeFlix.controller.DBController;

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
	private DBController dbController;
	private Media media;
	private MediaPlayer mediaPlayer;
	
	private double currentTime = 0;
	private double futureTime= 0;
	private double duration = 0;
	private boolean mousePressed = false;
	private boolean showControls = true;
	private String file;
	private int nextEp;
	
	private ImageView stop_black = new ImageView(new Image("icons/ic_stop_black_24dp_1x.png"));
	private ImageView play_arrow_black = new ImageView(new Image("icons/ic_play_arrow_black_24dp_1x.png"));
	private ImageView pause_black = new ImageView(new Image("icons/ic_pause_black_24dp_1x.png"));
	private ImageView fullscreen_black = new ImageView(new Image("icons/ic_fullscreen_black_24dp_1x.png"));
	private ImageView fullscreen_exit_black = new ImageView(new Image("icons/ic_fullscreen_exit_black_24dp_1x.png"));

	/**
	 * initialize the new PlayerWindow
	 * @param file 			the file you want to play
	 * @param currentEp		the current episode (needed for autoplay)
	 * @param player		the player object (needed for closing action)
	 * @param dbController	the dbController object
	 */
	public void init(String file, String currentEp, Player player, DBController dbController) {
		this.file = file;
		this.player = player;
		this.dbController = dbController;
		initActions();
		
		if (currentEp.length() > 0) {
			nextEp = Integer.parseInt(currentEp) + 1;
		} else {
			nextEp = 0;
		}
		
		media = new Media(new File(file).toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		mediaView.setPreserveRatio(true);
		mediaView.setMediaPlayer(mediaPlayer);

		final DoubleProperty width = mediaView.fitWidthProperty();
		final DoubleProperty height = mediaView.fitHeightProperty();

		width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
		height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
		
		mediaPlayer.setOnReady(new Runnable() {
	        @Override
	        public void run() {
	        	duration = media.getDuration().toMillis();
	        	
	        	timeSlider.setMax((duration/1000)/60);

	        	mediaPlayer.setStartTime(Duration.millis(dbController.getCurrentTime(file)));
	            mediaPlayer.play();
	        }
	    });
		
		mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
		    @Override
		        public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
		    	
		    	
		    	currentTime = newValue.toMillis();
		    	
		    	if (duration - currentTime < 10000) {
		    		if (nextEp != 0) {
		    			dbController.getNextEpisode(new File(file).getName(), nextEp);
		    			System.out.println("next episode is: " + dbController.getNextEpisode(file, nextEp));
					} else {
						if (duration - currentTime < 100) {
							dbController.setCurrentTime(file, 0);
							mediaPlayer.stop();
							player.getStage().close();
						}
					}
				}

		    	if (!mousePressed) {
		    		timeSlider.setValue((currentTime/1000)/60);
				}
		    }
		});
		
		stopBtn.setGraphic(stop_black);
		playBtn.setGraphic(play_arrow_black);
		fullscreenBtn.setGraphic(fullscreen_exit_black);
	}
	
	/**
	 * initialize some PlayerWindow GUI-Elements actions
	 */
	private void initActions() {
		
		player.getScene().addEventFilter(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
			// hide controls timer init
			final Timer timer = new Timer();
			TimerTask controlAnimationTask = null; // task to execute save operation
			final long delayTime = 5000;

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

		timeSlider.setOnMouseReleased(new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent event) {
		    	mediaPlayer.seek(new Duration(futureTime));
		    	mousePressed = false;
		    } 
		});
		
		timeSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mousePressed = true;
			} 
		});
		
		timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				futureTime = (double) new_val*1000*60;
			}
		});
	}
	
	@FXML
	void stopBtnAction(ActionEvent event) {
		
		dbController.setCurrentTime(file, currentTime);
		
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
