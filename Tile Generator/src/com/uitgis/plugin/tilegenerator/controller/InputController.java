package com.uitgis.plugin.tilegenerator.controller;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.uitgis.plugin.tilegenerator.model.WizardData;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class InputController {

	private Logger log = LoggerFactory.getLogger(InputController.class);

	@FXML
	TextField tfGdxFile, tfLeft, tfTop, tfBottom, tfRight;

	@FXML
	Button btnGdxBrowse;

	@FXML
	RadioButton rbSelectMap, rbSelectGDX, rbFullExtent, rbCurrExtent, rbUsrDefineExtent;

	@FXML
	ComboBox<String> cmbMap;

	@Inject
	WizardData model;

	@FXML
	public void initialize() {

		tfLeft.textProperty().bindBidirectional(model.field1Property());
		tfTop.textProperty().bindBidirectional(model.field2Property());
		tfBottom.textProperty().bindBidirectional(model.field3Property());
		tfRight.textProperty().bindBidirectional(model.field3Property());

		cmbMap.disableProperty().bind(rbSelectGDX.selectedProperty());
		tfGdxFile.disableProperty().bind(rbSelectMap.selectedProperty());
		btnGdxBrowse.disableProperty().bind(rbSelectMap.selectedProperty());
		btnGdxBrowse.setOnAction(event -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open GDX file");
			fileChooser.getExtensionFilters()
					.add(new FileChooser.ExtensionFilter("Geography Markup Language", "*.xml"));

			File file = fileChooser.showOpenDialog(btnGdxBrowse.getScene().getWindow());

			if (file != null) {
				tfGdxFile.setText(file.getPath());
			}
		});

	}

	@Validate
	public boolean validate() throws Exception {

		if (tfLeft.getText() == null || tfLeft.getText().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Left Extent");
			alert.setHeaderText("Missing Field");
			alert.setContentText("Left Extent is required.");
			alert.showAndWait();

			return false;
		}

		if (tfTop.getText() == null || tfTop.getText().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Top Extent");
			alert.setHeaderText("Missing Field");
			alert.setContentText("Top Extent is required.");
			alert.showAndWait();
			return false;
		}

		if (tfBottom.getText() == null || tfBottom.getText().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Bottom Extent");
			alert.setHeaderText("Missing Field");
			alert.setContentText("Bottom Extent is required.");
			alert.showAndWait();
			return false;
		}
		if (tfRight.getText() == null || tfRight.getText().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Right Extent");
			alert.setHeaderText("Missing Field");
			alert.setContentText("Right Extent is required.");
			alert.showAndWait();
			return false;
		}
		return true;
	}

	@Submit
	public void submit() throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("[SUBMIT] the user has completed step Input Configuration");
		}
	}
}
