package application;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class uiData {

	private DoubleProperty filmBewertung = new SimpleDoubleProperty();
	private StringProperty filmName = new SimpleStringProperty();
	private StringProperty dataName = new SimpleStringProperty();
	
	//uiData ist der Typ der Daten in der TreeTabelView
	public uiData (final Double filmBewertung, final String filmName, final String dataName) {
		this.filmBewertung.set(filmBewertung);
		this.filmName.set(filmName);
		this.dataName.set(dataName);
	}

	public Double getFilmBewertung() {
		return filmBewertung.get();
	}

	public String getFilmName() {
		return filmName.get();
	}

	public String getDataName() {
		return dataName.get();
	}


	public void setFilmBewertung(Double filmBewertung) {
		this.filmBewertung.set(filmBewertung);
	}

	public void setFilmName(String filmName) {
		this.filmName.set(filmName);
	}

	public void setDataName(StringProperty dataName) {
		this.dataName = dataName;
	}

	public DoubleProperty FilmBewertungProperty(){
		return filmBewertung;
	}
	
	public StringProperty FilmNameProperty(){
		return filmName;
	}
	
	public StringProperty DataNameProperty(){
		return dataName;
	}
}
