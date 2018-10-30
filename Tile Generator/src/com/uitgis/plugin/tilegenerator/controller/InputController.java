package com.uitgis.plugin.tilegenerator.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.uitgis.maple.application.ContentID;
import com.uitgis.maple.contents.map.ui.MapTabPane;
import com.uitgis.maple.contents.toolbox.ui.ToolboxHelper;
import com.uitgis.plugin.tilegenerator.model.WizardData;
import com.uitgis.sdk.controls.MapControl;
import com.uitgis.sdk.gdx.GDX;
import com.uitgis.sdk.gdx.GDXHelper;
import com.uitgis.sdk.layer.ILayer;
import com.vividsolutions.jts.geom.Envelope;

import framework.FrameworkManager;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
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
	ToggleGroup tglGroupExtent, tglGroupMap;

	@FXML
	ComboBox<String> cmbMap;

	@Inject
	WizardData model;
	
    public ObservableList<String> choiceMapItems = FXCollections.observableArrayList();
	private MapTabPane mapTab = (MapTabPane)FrameworkManager.getUserContent(ContentID.MAP_TAB_KEY);
	private ArrayList<MapControl> maps = mapTab.getAllMapControls();
    private MapControl mc = ToolboxHelper.getCurrentMapControl();

    
	@FXML
	public void initialize() {

		if (!mc.gdxEmpty())
		{
			model.setGDX(mc.getGDX());
			setTileEnvelope(model.getGDX().getEnvelope());
		}
	
		choiceMapItems.addAll(maps.stream().map(c-> c.getMapTitle()).collect(Collectors.toList()));

		tfLeft.textProperty().bindBidirectional(model.leftExtentProperty());
		tfTop.textProperty().bindBidirectional(model.topExtentProperty());
		tfBottom.textProperty().bindBidirectional(model.bottomExtentProperty());
		tfRight.textProperty().bindBidirectional(model.rightExtentProperty());
		
		tfLeft.disableProperty().bind(Bindings.or(rbFullExtent.selectedProperty(),rbCurrExtent.selectedProperty()));
		tfTop.disableProperty().bind(Bindings.or(rbFullExtent.selectedProperty(),rbCurrExtent.selectedProperty()));
		tfBottom.disableProperty().bind(Bindings.or(rbFullExtent.selectedProperty(),rbCurrExtent.selectedProperty()));
		tfRight.disableProperty().bind(Bindings.or(rbFullExtent.selectedProperty(),rbCurrExtent.selectedProperty()));		
		
		

		cmbMap.disableProperty().bind(rbSelectGDX.selectedProperty());
		cmbMap.getItems().addAll(choiceMapItems);
		cmbMap.getSelectionModel().select(mc.getMapTitle());
		
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
				try {
					model.setGDX(GDXHelper.loadGDX(file));
					if(!model.getGDX().isEmpty()) {
						if (rbFullExtent.isSelected()) {
							setTileEnvelope(calcfullExtentFromGDX(model.getGDX()));
						}else {
							if (rbCurrExtent.isSelected())
								setTileEnvelope(model.getGDX().getEnvelope());
						}
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		tglGroupExtent.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle oldVal, Toggle newVal) {           

				int selected = tglGroupExtent.getToggles().indexOf(tglGroupExtent.getSelectedToggle());
				
				if (selected == 0){ // Full extent
					if(model.getGDX() != null) {
						setTileEnvelope(calcfullExtentFromGDX(model.getGDX()));
					}				
					
				}else {
					if (selected == 1) { // Current Extent
						if(model.getGDX() != null) {
							setTileEnvelope(model.getGDX().getEnvelope());
						}
					}
						
				}
				
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
	
	@FXML
	public void selectMapAction(ActionEvent event) {

		Optional<MapControl> mc = maps.stream().filter(p -> p.getMapTitle().equals(cmbMap.getValue())).findFirst();
		
		if (mc.isPresent()) {
			if (!mc.get().gdxEmpty()) {
				model.setGDX(mc.get().getGDX());
				if (rbFullExtent.isSelected()) {
					setTileEnvelope(calcfullExtentFromGDX(model.getGDX()));
				}else {
					if (rbCurrExtent.isSelected())
						setTileEnvelope(model.getGDX().getEnvelope());
				}
			}
		}			

	}

	private void setTileEnvelope(Envelope ev) {
		if (ev != null) {
			this.model.leftExtentProperty().set(Double.toString(ev.getMinX()));
			this.model.rightExtentProperty().set(Double.toString(ev.getMaxX()));
			this.model.topExtentProperty().set(Double.toString(ev.getMaxY()));
			this.model.bottomExtentProperty().set(Double.toString(ev.getMinY()));
		}
	}
	public Envelope calcfullExtentFromGDX(GDX gDX) {
		Envelope envelope = new Envelope();
		for(int i = 0 ; i < gDX.getLayerCount(); i++) {
			ILayer layer = gDX.getLayer(i);
			envelope.expandToInclude(layer.getDataEnvelope());
		}
		return envelope;

	}	
}
