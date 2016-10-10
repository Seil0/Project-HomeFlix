package application;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class streamUiData {
	
	private IntegerProperty year = new SimpleIntegerProperty();
	private IntegerProperty season = new SimpleIntegerProperty();
	private IntegerProperty episode = new SimpleIntegerProperty();
	private DoubleProperty rating = new SimpleDoubleProperty();
	private StringProperty resolution = new SimpleStringProperty();
	private StringProperty titel = new SimpleStringProperty();
	private StringProperty streamUrl = new SimpleStringProperty();
	
	//uiData ist der Typ der Daten in der TreeTabelView
	public streamUiData (final int year, final int season, final int episode, final double rating, final String resolution, final String titel, final String streamUrl) {
		this.year.set(year);
		this.season.set(season);
		this.episode.set(episode);
		this.rating.set(rating);
		this.resolution.set(resolution);
		this.titel.set(titel);
		this.streamUrl.set(streamUrl);
	}

	public int getYear() {
		return year.get();
	}

	public int getSeason() {
		return season.get();
	}
	
	public int getEpisode() {
		return episode.get();
	}
	
	public double getRating() {
		return rating.get();
	}

	public String getResolution() {
		return resolution.get();
	}
	
	public String getTitel() {
		return titel.get();
	}
	
	public String getStreamUrl() {
		return streamUrl.get();
	}


	public void setYear(int year) {
		this.year.set(year);
	}

	public void setSeason(int season) {
		this.season.set(season);
	}
	
	public void setEpisode(int season) {
		this.episode.set(season);
	}
	
	public void setRating(int rating) {
		this.rating.set(rating);
	}
	
	public void setResolution(String resolution) {
		this.resolution.set(resolution);
	}
	
	public void setTitel(String titel) {
		this.titel.set(titel);
	}

	public void setStreamUrl(StringProperty streamUrl) {
		this.streamUrl = streamUrl;
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
}
