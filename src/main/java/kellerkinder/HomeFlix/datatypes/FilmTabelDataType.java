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
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.ImageView;

public class FilmTabelDataType {
	private final StringProperty streamUrl = new SimpleStringProperty();
	private final StringProperty title = new SimpleStringProperty();
	private final StringProperty season = new SimpleStringProperty();
	private final StringProperty episode = new SimpleStringProperty();
	private final BooleanProperty favorite = new SimpleBooleanProperty();
	private final BooleanProperty cached = new SimpleBooleanProperty();
	private final SimpleObjectProperty<ImageView> image = new SimpleObjectProperty<>();

	
	/**
	 * tableData is the data-type of tree-table-view
	 * @param streamUrl the concrete path to the file or the URL
	 * @param title title of the film
	 * @param season season if it's a series
	 * @param episode episode if it's a series
	 * @param favorite indicator for favorites, used for sorting the items
	 * @param cached indicator for caching status
	 * @param image favorite icon
	 */
	public FilmTabelDataType(final String streamUrl, final String title, final String season, final String episode,
			final boolean favorite, final boolean cached, final ImageView image) {
		this.streamUrl.set(streamUrl);
		this.title.set(title);
		this.season.set(season);
		this.episode.set(episode);
		this.favorite.set(favorite);
		this.cached.set(cached);
		this.image.set(image);
	}
	
	public StringProperty streamUrlProperty(){
		return streamUrl;
	}
	
	public StringProperty titleProperty(){
		return title;
	}
	
	public StringProperty seasonProperty(){
		return season;
	}
	
	public StringProperty episodeProperty(){
		return episode;
	}
	
	public BooleanProperty favoriteProperty(){
		return favorite;
	}
	
	public BooleanProperty cachedProperty(){
		return cached;
	}
	
	public SimpleObjectProperty<ImageView> imageProperty(){
		return image;
	}
	
	
	public final String getStreamUrl() {
		return streamUrlProperty().get();
	}
	
	public final String getTitle() {
		return titleProperty().get();
	}

	public final String getSeason() {
		return seasonProperty().get();
	}
	
	public final String getEpisode() {
		return episodeProperty().get();
	}
	
	public final boolean getFavorite() {
		return favoriteProperty().get();
	}
	
	public final boolean getCached(){
		return cachedProperty().get();
	}
	
	public final ImageView getImage() {
		return imageProperty().get();
	}
	
	
	public final void setStreamUrl(String streamUrl) {
		streamUrlProperty().set(streamUrl);
	}

	public final void setTitle(String title) {
		titleProperty().set(title);
	}

	public final void setSeason(String season) {
		seasonProperty().set(season);
	}
	
	public final void setEpisode(String season) {
		episodeProperty().set(season);
	}
	
	public final void setFavorite(boolean favorite) {
		favoriteProperty().set(favorite);
	}
	
	public final void setCached(boolean cached){
		cachedProperty().set(cached);
	}
	
	public final void setImage(ImageView image) {
		imageProperty().set(image);
	}
	

}
