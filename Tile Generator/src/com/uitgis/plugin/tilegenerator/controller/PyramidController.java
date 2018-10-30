package com.uitgis.plugin.tilegenerator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.uitgis.plugin.tilegenerator.model.TileScale;
import com.uitgis.plugin.tilegenerator.model.WizardData;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;

public class PyramidController {

	private Logger log = LoggerFactory.getLogger(PyramidController.class);

	@FXML
	TextField tfMultipleNum;
	@FXML
	Button btnCalcScale;
	@FXML
	ToggleGroup tglLevelOrder;
	@FXML
	Spinner<Integer> spinNumLevels;
	@FXML
	RadioButton rbAsc, rbDesc;
	@FXML
	TableView<TileScale> tblScale;
	@FXML
	TableColumn<TileScale, Boolean> colActive;
	@FXML
	TableColumn<TileScale, Number> colLevel;
	@FXML
	TableColumn<TileScale, Double> colScale;
	@Inject
	WizardData model;

	@FXML
	public void initialize() {

		btnCalcScale.disableProperty().bind(model.getListTileScale().get(0).scaleProperty().lessThanOrEqualTo(0));
		spinNumLevels.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99));
		spinNumLevels.valueProperty().addListener((obs, oldValue, newValue) -> {
			int numLevels = spinNumLevels.getValue();
			int currentNum = model.getListTileScale().size();
			if (currentNum < numLevels) {
				int gap = numLevels - currentNum;
				for (int i = 0; i < gap; i++) {
					model.getListTileScale().addAll(new TileScale(true, currentNum + i, 0));
				}

				if (rbDesc.isSelected()) {
					for (int i = 0, size = model.getListTileScale().size(); i < size; i++) {
						model.getListTileScale().get(i).setLevel(size - i - 1);
					}
				}
			} else if (currentNum > numLevels) {
				int gap = currentNum - numLevels;
				for (int i = 0; i < gap; i++) {
					model.getListTileScale().remove(--currentNum);
				}
				if (rbDesc.isSelected()) {
					for (int i = 0, size = model.getListTileScale().size(); i < size; i++) {
						model.getListTileScale().get(i).setLevel(size - i - 1);
					}
				}
			}

		});
		

		tblScale.setItems(model.getListTileScale());
		colLevel.setCellValueFactory(cd -> cd.getValue().levelProperty());
		colScale.setCellValueFactory(cd -> cd.getValue().scaleProperty().asObject());
		colScale.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
		

		colActive.setCellValueFactory(cd -> cd.getValue().activeProperty());
		final Callback<TableColumn<TileScale, Boolean>, TableCell<TileScale, Boolean>> cellFactory = CheckBoxTableCell.forTableColumn(colActive);
		colActive.setCellFactory(new Callback<TableColumn<TileScale, Boolean>, TableCell<TileScale, Boolean>>() {
			@Override
			public TableCell<TileScale, Boolean> call(TableColumn<TileScale, Boolean> column) {
				TableCell<TileScale, Boolean> cell = cellFactory.call(column);
				cell.setAlignment(Pos.CENTER);
				return cell;
			}
		});

		tglLevelOrder.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle oldVal, Toggle newVal) {

				int selected = tglLevelOrder.getToggles().indexOf(tglLevelOrder.getSelectedToggle());

				if (selected == 0) {
					for (int i = 0, size = model.getListTileScale().size(); i < size; i++) {
						model.getListTileScale().get(i).setLevel(i);
					}

				} else {
					for (int i = 0, size = model.getListTileScale().size(); i < size; i++) {
						model.getListTileScale().get(i).setLevel(size - i - 1);
					}

				}

			}
		});

	}

	@Validate
	public boolean validate() throws Exception {
		
		boolean scaleDone = true;
		for (int i = 0, size = model.getListTileScale().size(); i < size; i++) {
			if (model.getListTileScale().get(i).scaleProperty().lessThanOrEqualTo(0).getValue())
				scaleDone = false;
		}
        if( !scaleDone ) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Step 3");
            alert.setHeaderText( "Missing Field" );
            alert.setContentText( "All values in Scale field are required! Input value for first level and calculate it." );
            alert.showAndWait();
            return false;
        }
		return true;
	}

	@Submit
	public void submit() throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("[SUBMIT] the user has completed step Pyramid Configuration");
		}
	}

	@FXML
	public void calcScales() {

		int multiple = Integer.parseInt(tfMultipleNum.getText().trim());
		double scale = model.getListTileScale().get(0).getScale();
		int level = model.getListTileScale().size();
		for (int i = 1; i < level; i++) {
			scale = scale / multiple;
			model.getListTileScale().get(i).setScale(scale);
		}

	}

}
