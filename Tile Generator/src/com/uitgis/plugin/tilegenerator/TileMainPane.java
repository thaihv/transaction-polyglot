package com.uitgis.plugin.tilegenerator;

import java.io.IOException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.uitgis.plugin.tilegenerator.controller.WizardModule;
import com.uitgis.sdk.controls.MapControl;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class TileMainPane extends BorderPane {

	public TileMainPane(MapControl mapControl) {
		
		final Injector injector = Guice.createInjector( new WizardModule() );

		Parent p;
		try {
			p = FXMLLoader.load( TileMainPane.class.getResource("/fxml/Wizard.fxml"),
											  null,
											  new JavaFXBuilderFactory(),
											  (clazz) -> injector.getInstance(clazz));
			setCenter(p);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	
		
		
	}

}
