package com.uitgis.plugin.tilegenerator;

import com.uitgis.maple.common.ui.MapleDialogSkin;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;

public class TileGenDialogSkin extends MapleDialogSkin {

	public TileGenDialogSkin(@SuppressWarnings("rawtypes") Dialog dialog, boolean resize, Image titleImage) {
		
		super(dialog, resize, titleImage);
		
		ButtonBar buttonBar = (ButtonBar)super.getButtonBar();		
		buttonBar.setVisible(false);
	

		
	}
	

}