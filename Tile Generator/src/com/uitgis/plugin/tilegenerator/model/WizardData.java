package com.uitgis.plugin.tilegenerator.model;

import java.io.File;

import com.uitgis.sdk.gdx.GDX;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class WizardData {

	private final StringProperty leftExtent = new SimpleStringProperty();
	private final StringProperty rightExtent = new SimpleStringProperty();
	private final StringProperty topExtent = new SimpleStringProperty();
	private final StringProperty bottomExtent = new SimpleStringProperty();

	private final StringProperty originX = new SimpleStringProperty();
	private final StringProperty originY = new SimpleStringProperty();

	private final StringProperty destinationFolder = new SimpleStringProperty(new File(System.getProperty("user.home")).getPath());

	private IntegerProperty orderLevel = new SimpleIntegerProperty(0);
	private IntegerProperty tileWidth = new SimpleIntegerProperty(512);
	private IntegerProperty tileHeight = new SimpleIntegerProperty(512);
	
	private IntegerProperty threadNum = new SimpleIntegerProperty(1);

	private GDX GDX;

	private final ObservableList<TileScale> listTileScale = FXCollections
			.observableArrayList(new TileScale(true, 0, 0));

	public ObservableList<TileScale> getListTileScale() {
		return listTileScale;
	}

	public void reset() {
		leftExtent.set("");
		rightExtent.set("");
		topExtent.set("");
		bottomExtent.set("");
		threadNum.set(1);
		originX.set("");
		originY.set("");
		destinationFolder.set(new File(System.getProperty("user.home")).getPath());
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

	public GDX getGDX() {
		return GDX;
	}

	public void setGDX(GDX gDX) {
		GDX = gDX;
	}

	public IntegerProperty threadNumProperty() {
		return this.threadNum;
	}

	public int getThreadNum() {
		return this.threadNumProperty().get();
	}

	public void setThreadNum(int threadNum) {
		this.threadNumProperty().set(threadNum);
	}

	public StringProperty originXProperty() {
		return this.originX;
	}

	public String getOriginX() {
		return this.originXProperty().get();
	}

	public void setOriginX(final String originX) {
		this.originXProperty().set(originX);
	}

	public StringProperty originYProperty() {
		return this.originY;
	}

	public String getOriginY() {
		return this.originYProperty().get();
	}

	public void setOriginY(final String originY) {
		this.originYProperty().set(originY);
	}

	public StringProperty destinationFolderProperty() {
		return this.destinationFolder;
	}

	public String getDestinationFolder() {
		return this.destinationFolderProperty().get();
	}

	public void setDestinationFolder(final String destinationFolder) {
		this.destinationFolderProperty().set(destinationFolder);
	}

	public IntegerProperty orderLevelProperty() {
		return this.orderLevel;
	}
	

	public int getOrderLevel() {
		return this.orderLevelProperty().get();
	}
	

	public void setOrderLevel(final int orderLevel) {
		this.orderLevelProperty().set(orderLevel);
	}

	public IntegerProperty tileWidthProperty() {
		return this.tileWidth;
	}
	

	public int getTileWidth() {
		return this.tileWidthProperty().get();
	}
	

	public void setTileWidth(final int tileWidth) {
		this.tileWidthProperty().set(tileWidth);
	}
	

	public IntegerProperty tileHeightProperty() {
		return this.tileHeight;
	}
	

	public int getTileHeight() {
		return this.tileHeightProperty().get();
	}
	

	public void setTileHeight(final int tileHeight) {
		this.tileHeightProperty().set(tileHeight);
	}
	
	

}
