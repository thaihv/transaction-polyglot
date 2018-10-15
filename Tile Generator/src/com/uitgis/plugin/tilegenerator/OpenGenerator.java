package com.uitgis.plugin.tilegenerator;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.uitgis.maple.application.ContentID;
import com.uitgis.maple.application.Main;
import com.uitgis.maple.common.ui.MapleDialogSkin;
import com.uitgis.maple.common.util.Util;
import com.uitgis.maple.contents.map.ui.MapTabPane;
import com.uitgis.sdk.controls.MapControl;

import framework.FrameworkManager;
import framework.dialog.DialogManager;
import framework.i18n.I18N;
import framework.ribbon.RibbonMenu;
import framework.ribbon.RibbonMenu.FileMenuSection;
import framework.ribbon.event.RibbonEventHandler;


public class OpenGenerator extends RibbonEventHandler {

	private boolean opened;
	
	
	public String getContentID() {
		return "tilegenerator";
	}

	public void statusChanged(Change<String, Object> arg) {
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doAction(ActionEvent event) {
		if (opened) {
			return;
		}
		
		MapTabPane mapTabPane = (MapTabPane) FrameworkManager.getUserContent(ContentID.MAP_TAB_KEY);
		MapControl mapControl = mapTabPane.getSelectedMapControl();

		if (mapControl == null) {
			mapControl = Util.newMap();
		}
		
		ButtonType closeButtonType = new ButtonType(I18N.getText("Close"), ButtonData.CANCEL_CLOSE);
		ObservableList<ButtonType> buttonTypes = FXCollections.observableArrayList();
		buttonTypes.setAll(closeButtonType);
		
		TileMainPane georizerPane = new TileMainPane(mapControl);
		
		Dialog dialog = DialogManager.getCustomDialog(I18N.getText("window.title"), "", georizerPane, buttonTypes, false);
		dialog.initModality(Modality.NONE);
		
		dialog.setOnShown(e -> {
			opened = true;

			Stage stage = Main.getPrimaryStage();		
			double x = stage.getX() + stage.getWidth() / 2 - dialog.getWidth() / 2;
		    double y = stage.getY() + stage.getHeight() / 2 - dialog.getHeight() / 2;
		    dialog.setX(x);
		    dialog.setY(y);
		    
		    mapTabPane.setHold(true);
		    
		    RibbonMenu menu = FrameworkManager.getRibbon();
		    menu.setMenuEnablement(FileMenuSection.NewSection, false);
		    menu.setMenuEnablement(FileMenuSection.OpenSection, false);
		});
		
		dialog.setOnCloseRequest(e -> {
		    mapTabPane.setHold(false);

		    RibbonMenu menu = FrameworkManager.getRibbon();
		    menu.setMenuEnablement(FileMenuSection.NewSection, true);
		    menu.setMenuEnablement(FileMenuSection.OpenSection, true);
		    
		    
			opened = false;
		});
		
		MapleDialogSkin skin = new MapleDialogSkin(dialog, false, IconResources.ICON_GEORIZER);
		skin.showAndWait();
	}

}
