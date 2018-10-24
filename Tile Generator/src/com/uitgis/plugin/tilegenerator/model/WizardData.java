package com.uitgis.plugin.tilegenerator.model;

import com.vividsolutions.jts.geom.Envelope;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class WizardData {


    private final StringProperty leftExtent = new SimpleStringProperty();
    private final StringProperty rightExtent = new SimpleStringProperty();
    private final StringProperty topExtent = new SimpleStringProperty();
    private final StringProperty bottomExtent = new SimpleStringProperty();

    
 
	
	private final ObservableList<TileScale> listTileScale = FXCollections.observableArrayList(new TileScale(true, 0, 0));

	private Envelope envelope = new Envelope();
	


	public Envelope getEnvelope() {
		return envelope;
	}

	public void setEnvelope(Envelope envelope) {
		this.envelope = envelope;
	}

	public ObservableList<TileScale> getListTileScale() {
		return listTileScale;
	}



    public void reset() {
    	leftExtent.set("");
    	rightExtent.set("");
    	topExtent.set("");
    	bottomExtent.set("");
        listTileScale.clear();
        listTileScale.add(new TileScale(true, 0, 0));
    }

	public StringProperty leftExtentProperty() {
		return this.leftExtent;
	}
	

	public String getLeftExtent() {
		return this.leftExtentProperty().get();
	}
	

	public void setLeftExtent(final String leftExtent) {
		this.leftExtentProperty().set(leftExtent);
	}
	

	public StringProperty rightExtentProperty() {
		return this.rightExtent;
	}
	

	public String getRightExtent() {
		return this.rightExtentProperty().get();
	}
	

	public void setRightExtent(final String rightExtent) {
		this.rightExtentProperty().set(rightExtent);
	}
	

	public StringProperty topExtentProperty() {
		return this.topExtent;
	}
	

	public String getTopExtent() {
		return this.topExtentProperty().get();
	}
	

	public void setTopExtent(final String topExtent) {
		this.topExtentProperty().set(topExtent);
	}
	

	public StringProperty bottomExtentProperty() {
		return this.bottomExtent;
	}
	

	public String getBottomExtent() {
		return this.bottomExtentProperty().get();
	}
	

	public void setBottomExtent(final String bottomExtent) {
		this.bottomExtentProperty().set(bottomExtent);
	}
	
}
