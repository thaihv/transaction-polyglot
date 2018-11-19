package com.uitgis.plugin.tilegenerator.model;

import java.io.File;

import com.uitgis.sdk.gdx.GDX;
import com.vividsolutions.jts.geom.Envelope;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class WizardData {

	private final StringProperty leftExtent = new SimpleStringProperty();
	private final StringProperty rightExtent = new SimpleStringProperty();
	private final StringProperty topExtent = new SimpleStringProperty();
	private final StringProperty bottomExtent = new SimpleStringProperty();

	private final StringProperty originX = new SimpleStringProperty();
	private final StringProperty originY = new SimpleStringProperty();

	private final StringProperty destinationFolder = new SimpleStringProperty(
			new File(System.getProperty("user.home")).getPath());
	private final StringProperty pathExpression = new SimpleStringProperty("/{$L}/Y{$Y}/X{$X}");
	private BooleanProperty googleXYZ = new SimpleBooleanProperty(false);

	private final StringProperty tileName = new SimpleStringProperty();
	private IntegerProperty tileMapType = new SimpleIntegerProperty(0);

	private BooleanProperty transparentBackground = new SimpleBooleanProperty(false);
	private BooleanProperty improveLabelQuality = new SimpleBooleanProperty(true);
	private BooleanProperty antialiasing = new SimpleBooleanProperty(false);
	private BooleanProperty eliminateLabelQuality = new SimpleBooleanProperty(true);
	private ObjectProperty<Color> colorBackground = new SimpleObjectProperty<>(Color.WHITE);

	
	private BooleanProperty overWriteAllowed = new SimpleBooleanProperty(false);
	private BooleanProperty generateEmptyTile = new SimpleBooleanProperty(false);

	private IntegerProperty orderLevel = new SimpleIntegerProperty(0);
	private IntegerProperty tileWidth = new SimpleIntegerProperty(512);
	private IntegerProperty tileHeight = new SimpleIntegerProperty(512);
	private IntegerProperty tileFormat = new SimpleIntegerProperty(0);

	private Envelope targetEnvelope = new Envelope();

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
		tileName.set("");
		destinationFolder.set(new File(System.getProperty("user.home")).getPath());
		pathExpression.set("/{$L}/Y{$Y}/X{$X}");
		listTileScale.clear();
		listTileScale.add(new TileScale(true, 0, 0));
		
		googleXYZ.set(false);
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

	public Envelope getTargetEnvelope() {
		return targetEnvelope;
	}

	public void setTargetEnvelope(Envelope targetEnvelope) {
		this.targetEnvelope = targetEnvelope;
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

	public StringProperty pathExpressionProperty() {
		return this.pathExpression;
	}

	public String getPathExpression() {
		return this.pathExpressionProperty().get();
	}

	public void setPathExpression(final String pathExpression) {
		this.pathExpressionProperty().set(pathExpression);
	}

	public StringProperty tileNameProperty() {
		return this.tileName;
	}

	public String getTileName() {
		return this.tileNameProperty().get();
	}

	public void setTileName(final String tileName) {
		this.tileNameProperty().set(tileName);
	}

	public IntegerProperty tileMapTypeProperty() {
		return this.tileMapType;
	}

	public int getTileMapType() {
		return this.tileMapTypeProperty().get();
	}

	public void setTileMapType(final int tileMapType) {
		this.tileMapTypeProperty().set(tileMapType);
	}

	public BooleanProperty transparentBackgroundProperty() {
		return this.transparentBackground;
	}

	public boolean isTransparentBackground() {
		return this.transparentBackgroundProperty().get();
	}

	public void setTransparentBackground(final boolean transparentBackground) {
		this.transparentBackgroundProperty().set(transparentBackground);
	}

	public BooleanProperty improveLabelQualityProperty() {
		return this.improveLabelQuality;
	}

	public boolean isImproveLabelQuality() {
		return this.improveLabelQualityProperty().get();
	}

	public void setImproveLabelQuality(final boolean improveLabelQuality) {
		this.improveLabelQualityProperty().set(improveLabelQuality);
	}

	public BooleanProperty eliminateLabelQualityProperty() {
		return this.eliminateLabelQuality;
	}

	public boolean isEliminateLabelQuality() {
		return this.eliminateLabelQualityProperty().get();
	}

	public void setEliminateLabelQuality(final boolean eliminateLabelQuality) {
		this.eliminateLabelQualityProperty().set(eliminateLabelQuality);
	}

	public BooleanProperty overWriteAllowedProperty() {
		return this.overWriteAllowed;
	}

	public boolean isOverWriteAllowed() {
		return this.overWriteAllowedProperty().get();
	}

	public void setOverWriteAllowed(final boolean overWriteAllowed) {
		this.overWriteAllowedProperty().set(overWriteAllowed);
	}

	public BooleanProperty generateEmptyTileProperty() {
		return this.generateEmptyTile;
	}

	public boolean isGenerateEmptyTile() {
		return this.generateEmptyTileProperty().get();
	}

	public void setGenerateEmptyTile(final boolean generateEmptyTile) {
		this.generateEmptyTileProperty().set(generateEmptyTile);
	}

	public IntegerProperty tileFormatProperty() {
		return this.tileFormat;
	}

	public int getTileFormat() {
		return this.tileFormatProperty().get();
	}

	public void setTileFormat(final int tileFormat) {
		this.tileFormatProperty().set(tileFormat);
	}

	public ObjectProperty<Color> colorBackgroundProperty() {
		return this.colorBackground;
	}
	

	public Color getColorBackground() {
		return this.colorBackgroundProperty().get();
	}
	

	public void setColorBackground(final Color colorBackground) {
		this.colorBackgroundProperty().set(colorBackground);
	}

	public BooleanProperty antialiasingProperty() {
		return this.antialiasing;
	}
	

	public boolean isAntialiasing() {
		return this.antialiasingProperty().get();
	}
	

	public void setAntialiasing(final boolean antialiasing) {
		this.antialiasingProperty().set(antialiasing);
	}

	public BooleanProperty googleXYZProperty() {
		return this.googleXYZ;
	}
	

	public boolean isGoogleXYZ() {
		return this.googleXYZProperty().get();
	}
	

	public void setGoogleXYZ(final boolean googleXYZ) {
		this.googleXYZProperty().set(googleXYZ);
	}
	
	
	

}
