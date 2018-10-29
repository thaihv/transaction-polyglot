package com.uitgis.plugin.tilegenerator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.uitgis.plugin.tilegenerator.model.WizardData;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

public class ConfirmController {

	private Logger log = LoggerFactory.getLogger(ConfirmController.class);

	@FXML
	TextField tfThreadNum;

	@Inject
	WizardData model;

	@FXML
	public void initialize() {
		tfThreadNum.textProperty().bindBidirectional(model.threadNumProperty(), new NumberStringConverter());
	}

	@Submit
	public void submit() throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("[SUBMIT] the user has completed step Thread Setting ");
		}
	}

}
