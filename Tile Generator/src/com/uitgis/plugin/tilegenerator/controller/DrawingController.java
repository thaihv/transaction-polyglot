package com.uitgis.plugin.tilegenerator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.uitgis.plugin.tilegenerator.model.WizardData;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class DrawingController {

    private Logger log = LoggerFactory.getLogger(DrawingController.class);

    @FXML
    CheckBox ckbTransparent, ckbImprvLblQuality, ckbElimiateLblOverlaps;

    @Inject
    WizardData model;

    @FXML
    public void initialize() {
    	ckbTransparent.selectedProperty().bindBidirectional(model.transparentBackgroundProperty());
    	ckbImprvLblQuality.selectedProperty().bindBidirectional(model.improveLabelQualityProperty());
    	ckbElimiateLblOverlaps.selectedProperty().bindBidirectional(model.eliminateLabelQualityProperty());


    }

    @Validate
    public boolean validate() throws Exception {
        return true;
    }

    @Submit
    public void submit() throws Exception {
        if( log.isDebugEnabled() ) {
            log.debug("[SUBMIT] the user has completed step Drawing Configuration");
        }
    }
}


