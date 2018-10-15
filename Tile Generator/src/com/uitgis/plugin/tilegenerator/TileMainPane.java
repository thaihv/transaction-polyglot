package com.uitgis.plugin.tilegenerator;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import com.uitgis.sdk.controls.MapControl;

import framework.i18n.I18N;

public class TileMainPane extends BorderPane {

	public TileMainPane(MapControl mapControl) {
		TabPane tabPane = new TabPane();

		Tab tab = new Tab(I18N.getText("tab.geometry"));
		tab.setGraphic(new ImageView(IconResources.ICON_GEO_PROCESSING));
		tab.setClosable(false);

		tabPane.getTabs().add(tab);

		tab = new Tab(I18N.getText("tab.attribute"));
		tab.setGraphic(new ImageView(IconResources.ICON_ATTR_PROCESSING));
		tab.setClosable(false);

		tabPane.getTabs().add(tab);

		setCenter(tabPane);
	}

}
