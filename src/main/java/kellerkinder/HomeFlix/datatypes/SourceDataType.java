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

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SourceDataType {
	private final StringProperty path = new SimpleStringProperty();
	private final StringProperty mode = new SimpleStringProperty();
	
	/**
	 * data type for the source table
	 * @param path of the source
	 * @param mode of the source, stream or local
	 */
	public SourceDataType (final String path, final String mode) {
		this.path.set(path);
		this.mode.set(mode);
	}
	
	public StringProperty pathProperty(){
		return path;
	}
	
	public StringProperty modeProperty(){
		return mode;
	}

	public final String getPath() {
		return pathProperty().get();
	}

	public final String getMode() {
		return modeProperty().get();
	}
	
	public final void setPath(String path) {
		pathProperty().set(path);
	}
	
	public final void setMode(String mode) {
		modeProperty().set(mode);
	}
}
