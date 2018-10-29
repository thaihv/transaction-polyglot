package com.uitgis.plugin.tilegenerator.controller;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.uitgis.plugin.tilegenerator.model.WizardData;

import framework.i18n.I18N;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;

public class OutputController {

    private Logger log = LoggerFactory.getLogger(OutputController.class);

    @FXML
    TextField tfLocation, tfField4;

    @FXML
    RadioButton rbTileFile, rbTileGSS;
    
    @FXML
    Button btnBuildAsFile;
    
	@FXML
	ComboBox<String> cmbTileFormat;
  
    @FXML
    HBox hbxTileGSS, hbxLocation, hbxExpression;
    
    @Inject
    WizardData model;

    public ObservableList<String> choiceTileFormats = FXCollections.observableArrayList("PNG","JPEG");
    
    @FXML
    public void initialize() {
//        tfField4.textProperty().bindBidirectional(model.field4Property());
    	
    	
    	hbxExpression.disableProperty().bind(rbTileGSS.selectedProperty());
    	hbxLocation.disableProperty().bind(rbTileGSS.selectedProperty());
    	hbxTileGSS.disableProperty().bind(rbTileFile.selectedProperty());
    	
    	cmbTileFormat.getItems().addAll(choiceTileFormats);
    	cmbTileFormat.getSelectionModel().selectFirst();
		
    	btnBuildAsFile.setOnAction(event -> {
			DirectoryChooser dirChooser = new DirectoryChooser();
			dirChooser.setTitle(I18N.getText("SearchFolder"));
			
			File file = dirChooser.showDialog(btnBuildAsFile.getScene().getWindow());
			if(file != null) {
				tfLocation.setText(file.getAbsolutePath());
			}
		});
    }
    
    @Submit
    public void submit() throws Exception {

        if( log.isDebugEnabled() ) {
            log.debug("[SUBMIT] the user has completed step Output Configuration");
        }
    }
}
