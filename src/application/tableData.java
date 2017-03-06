package application;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.ImageView;

public class tableData {
	private final IntegerProperty year = new SimpleIntegerProperty();
	private final IntegerProperty season = new SimpleIntegerProperty();
	private final IntegerProperty episode = new SimpleIntegerProperty();
	private final DoubleProperty rating = new SimpleDoubleProperty();
	private final StringProperty resolution = new SimpleStringProperty();
	private final StringProperty titel = new SimpleStringProperty();
	private final StringProperty streamUrl = new SimpleStringProperty();
	private final SimpleObjectProperty<ImageView> image = new SimpleObjectProperty<>();
	private final BooleanProperty cached = new SimpleBooleanProperty();
	
	//tableData is the data-type of tree-table-view
	public tableData (final int year, final int season, final int episode, final double rating, final String resolution, final String titel, final String streamUrl, final ImageView image, final boolean cached) {
		this.year.set(year);
		this.season.set(season);
		this.episode.set(episode);
		this.rating.set(rating);
		this.resolution.set(resolution);
		this.titel.set(titel);
		this.streamUrl.set(streamUrl);
		this.image.set(image);
		this.cached.set(cached);
	}

	public IntegerProperty yearProperty(){
		return year;
	}
	
	public IntegerProperty seasonProperty(){
		return season;
	}
	
	public IntegerProperty episodeProperty(){
		return episode;
	}
	
	public DoubleProperty ratingProperty(){
		return rating;
	}
	
	public StringProperty resolutionProperty(){
		return resolution;
	}
	
	public StringProperty titelProperty(){
		return titel;
	}
	
	public StringProperty streamUrlProperty(){
		return streamUrl;
	}
	
	public SimpleObjectProperty<ImageView> imageProperty(){
		return image;
	}
	
	public BooleanProperty cachedProperty(){
		return cached;
	}
	
	
	public final int getYear() {
		return yearProperty().get();
	}

	public final int getSeason() {
		return seasonProperty().get();
	}
	
	public final int getEpisode() {
		return episodeProperty().get();
	}
	
	public final double getRating() {
		return ratingProperty().get();
	}

	public final String getResolution() {
		return resolutionProperty().get();
	}
	
	public final String getTitel() {
		return titelProperty().get();
	}
	
	public final String getStreamUrl() {
		return streamUrlProperty().get();
	}
	
	public final ImageView getImage() {
		return imageProperty().get();
	}
	
	public final boolean getCached(){
		return cachedProperty().get();
	}


	public final void setYear(int year) {
		yearProperty().set(year);
	}

	public final void setSeason(int season) {
		seasonProperty().set(season);
	}
	
	public final void setEpisode(int season) {
		episodeProperty().set(season);
	}
	
	public final void setRating(int rating) {
		ratingProperty().set(rating);
	}
	
	public final void setResolution(String resolution) {
		resolutionProperty().set(resolution);
	}
	
	public final void setTitel(String titel) {
		titelProperty().set(titel);
	}

	public final void setStreamUrl(String streamUrl) {
		streamUrlProperty().set(streamUrl);
	}
	
	public final void setImage(ImageView image) {
		imageProperty().set(image);
	}
	
	public final void setCached(boolean cached){
		cachedProperty().set(cached);
	}
}
