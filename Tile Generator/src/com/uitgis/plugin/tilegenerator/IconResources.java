package com.uitgis.plugin.tilegenerator;

import java.net.URL;

import javafx.scene.image.Image;

public class IconResources {

	public static final Image ICON_GEORIZER;
	
	public static final Image ICON_TILEGENERATOR;
	
	public static final Image ICON_REFRESH;

	public static final Image ICON_CHECK_ALL;
	
	public static final Image ICON_CHECK_NONE;
	
	public static final Image ICON_SET_BOUNDS;
	
	public static final Image ICON_JOIN_INNER;
	
	public static final Image ICON_JOIN_OUTER_LEFT;
	
	public static final Image ICON_JOIN_OUTER_RIGHT;
	
	public static final Image ICON_JOIN_OUTER_FULL;
	
	public static final Image ICON_ADD;
	
	public static final Image ICON_REMOVE;
	
	public static final Image ICON_UP;
	
	public static final Image ICON_DOWN;
	
	public static final Image ICON_EDIT;
	
	public static final Image ICON_PRIMARY;
	
	public static final Image ICON_SECONDARY;
	
	public static final Image ICON_VALID;
	
	public static final Image ICON_WARNING;
	
	public static final Image ICON_ERROR;
	
	public static final Image ICON_VALID_GRAY;
	
	public static final Image ICON_GEO_PROCESSING;
	
	public static final Image ICON_ATTR_PROCESSING;
	
	static {
		ClassLoader loader = TileGenerator.class.getClassLoader();
		
	
		
		URL url = loader.getResource("icons/tilegenerator.png");
		ICON_TILEGENERATOR = new Image(url.toString());
		
		url = loader.getResource("georize.gif");
		ICON_GEORIZER = new Image(url.toString());			
		
		url = loader.getResource("refresh.png");
		ICON_REFRESH = new Image(url.toString());
		
		url = loader.getResource("checkAll.png");
		ICON_CHECK_ALL = new Image(url.toString());
		
		url = loader.getResource("checkNone.png");
		ICON_CHECK_NONE = new Image(url.toString());
		
		url = loader.getResource("setBounds.gif");
		ICON_SET_BOUNDS = new Image(url.toString());
		
		url = loader.getResource("joinInner.png");
		ICON_JOIN_INNER = new Image(url.toString());
		
		url = loader.getResource("joinOuterLeft.png");
		ICON_JOIN_OUTER_LEFT = new Image(url.toString());

		url = loader.getResource("joinOuterRight.png");
		ICON_JOIN_OUTER_RIGHT = new Image(url.toString());

		url = loader.getResource("joinOuterFull.png");
		ICON_JOIN_OUTER_FULL = new Image(url.toString());
		
		url = loader.getResource("add.png");
		ICON_ADD = new Image(url.toString());
		
		url = loader.getResource("remove.png");
		ICON_REMOVE = new Image(url.toString());
		
		url = loader.getResource("up.png");
		ICON_UP = new Image(url.toString());
		
		url = loader.getResource("down.png");
		ICON_DOWN = new Image(url.toString());
		
		url = loader.getResource("edit.png");
		ICON_EDIT = new Image(url.toString());
		
		url = loader.getResource("primary.png");
		ICON_PRIMARY = new Image(url.toString());
		
		url = loader.getResource("secondary.png");
		ICON_SECONDARY = new Image(url.toString());
		
		url = loader.getResource("valid.png");
		ICON_VALID = new Image(url.toString());

		url = loader.getResource("warning.png");
		ICON_WARNING = new Image(url.toString());
		
		url = loader.getResource("error.png");
		ICON_ERROR = new Image(url.toString());

		url = loader.getResource("valid_gray.png");
		ICON_VALID_GRAY = new Image(url.toString());
		
		url = loader.getResource("geoProcessing.png");
		ICON_GEO_PROCESSING = new Image(url.toString());
		
		url = loader.getResource("attrProcessing.png");
		ICON_ATTR_PROCESSING = new Image(url.toString());
	}
	
}
