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
 */

package kellerkinder.HomeFlix.datatypes;

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

public class FilmTabelDataType {
	private final IntegerProperty season = new SimpleIntegerProperty();
	private final IntegerProperty episode = new SimpleIntegerProperty();
	private final DoubleProperty rating = new SimpleDoubleProperty();
	private final StringProperty title = new SimpleStringProperty();
	private final StringProperty streamUrl = new SimpleStringProperty();
	private final SimpleObjectProperty<ImageView> image = new SimpleObjectProperty<>();
	private final BooleanProperty cached = new SimpleBooleanProperty();
	
	/** TODO rating boolean
	 * tableData is the data-type of tree-table-view
	 * @param season season if it's a series
	 * @param episode episode if it's a series
	 * @param rating indicator for favorites, used for sorting the items
	 * @param title title of the film
	 * @param streamUrl the concrete path to the file or the URL
	 * @param cached indicator for caching status
	 */
	public FilmTabelDataType(final int season, final int episode, final double rating, final String title,
			final String streamUrl, final ImageView image, final boolean cached) {
		this.season.set(season);
		this.episode.set(episode);
		this.rating.set(rating);
		this.title.set(title);
		this.streamUrl.set(streamUrl);
		this.image.set(image);
		this.cached.set(cached);
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
	
	public StringProperty titleProperty(){
		return title;
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
	

	public final int getSeason() {
		return seasonProperty().get();
	}
	
	public final int getEpisode() {
		return episodeProperty().get();
	}
	
	public final double getRating() {
		return ratingProperty().get();
	}
	
	public final String getTitle() {
		return titleProperty().get();
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



	public final void setSeason(int season) {
		seasonProperty().set(season);
	}
	
	public final void setEpisode(int season) {
		episodeProperty().set(season);
	}
	
	public final void setRating(int rating) {
		ratingProperty().set(rating);
	}
	
	public final void setTitle(String title) {
		titleProperty().set(title);
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
