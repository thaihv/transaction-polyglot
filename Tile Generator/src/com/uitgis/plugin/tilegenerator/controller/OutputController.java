package com.uitgis.plugin.tilegenerator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.uitgis.plugin.tilegenerator.model.WizardData;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class OutputController {

    private Logger log = LoggerFactory.getLogger(OutputController.class);

    @FXML
    TextField tfField4;

    @Inject
    WizardData model;

    @FXML
    public void initialize() {
        tfField4.textProperty().bindBidirectional(model.field4Property());
    }
    
    @Submit
    public void submit() throws Exception {

        if( log.isDebugEnabled() ) {
            log.debug("[SUBMIT] the user has completed step 2");
        }
    }
}
