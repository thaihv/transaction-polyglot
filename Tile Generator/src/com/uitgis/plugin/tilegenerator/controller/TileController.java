package com.uitgis.plugin.tilegenerator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.uitgis.maple.common.util.Noti;
import com.uitgis.plugin.tilegenerator.model.WizardData;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class TileController {

    private Logger log = LoggerFactory.getLogger(TileController.class);

    @FXML
    TextField tfOriginX, tfOriginY;

    @FXML
    ToggleGroup tglOriginPoint;
    
    @FXML 
    RadioButton rbExtentPoint, rbDefinedPoint;
    
    @Inject
    WizardData model;

    @FXML
    public void initialize() {
    	
    	model.originXProperty().bindBidirectional(model.leftExtentProperty());
    	model.originYProperty().bindBidirectional(model.bottomExtentProperty());
    	
    	tfOriginX.textProperty().bindBidirectional(model.originXProperty());
    	tfOriginY.textProperty().bindBidirectional(model.originYProperty());
    	
    	
    	tfOriginX.disableProperty().bind(rbExtentPoint.selectedProperty());
    	tfOriginY.disableProperty().bind(rbExtentPoint.selectedProperty());

    }

    @Validate
    public boolean validate() throws Exception {
        if( tfOriginX.getText() == null || tfOriginX.getText().isEmpty() ) {
        	Noti.showAlert("Missing Field", "X Coordinate Origin field is required.");
            return false;
        }

        if( tfOriginY.getText() == null || tfOriginY.getText().isEmpty() ) {
        	Noti.showAlert("Missing Field", "Y Coordinate Origin field is required.");
            return false;
        }


        return true;
    }

    @Submit
    public void submit() throws Exception {
        if( log.isDebugEnabled() ) {
            log.debug("[SUBMIT] the user has completed step Tile Configuration");
        }
    }
}


