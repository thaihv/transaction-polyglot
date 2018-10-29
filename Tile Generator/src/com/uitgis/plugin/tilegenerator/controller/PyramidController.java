package com.uitgis.plugin.tilegenerator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.uitgis.plugin.tilegenerator.model.TileScale;
import com.uitgis.plugin.tilegenerator.model.WizardData;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;

public class PyramidController {

    private Logger log = LoggerFactory.getLogger(PyramidController.class);

    @FXML
    TextField tfField5, tfField6, tfField7;
    @FXML
    Spinner<Integer> spinNumLevels;
    @FXML
    TableView<TileScale> tblScale;
    @FXML
    TableColumn<TileScale,Boolean> colActive;
    @FXML
    TableColumn<TileScale,Number> colLevel ;   
    @FXML
    TableColumn<TileScale,Double> colScale ;
    @Inject
    WizardData model;
    

    
    
	@FXML
    public void initialize() {
    	   			
    	spinNumLevels.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99));
    	spinNumLevels.valueProperty().addListener((obs, oldValue, newValue) -> {
    		if (newValue > 0 && oldValue > 0)
    		{
	    		if (newValue > oldValue)
	        		for (int i = oldValue; i < newValue; i ++)
	        			model.getListTileScale().addAll(new TileScale(true, i, 0));
	    		else
	        		for (int i = oldValue; i > newValue; i--)
	        			model.getListTileScale().remove(i-1);
	    		System.out.println("OLD: " + oldValue + "NEW: " + newValue);
	    		System.out.println(model.getListTileScale());
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
                return cell ;
            }
        });



        
    }

    @Validate
    public boolean validate() throws Exception {
//        if( tfField5.getText() == null || tfField5.getText().isEmpty() ) {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Step 3");
//            alert.setHeaderText( "Missing Field" );
//            alert.setContentText( "Field 5 is required." );
//            alert.showAndWait();
//            return false;
//        }
//
//        if( tfField6.getText() == null || tfField6.getText().isEmpty() ) {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Step 3");
//            alert.setHeaderText( "Missing Field" );
//            alert.setContentText( "Field 6 is required." );
//            alert.showAndWait();
//            return false;
//        }
//
//        if( tfField7.getText() == null || tfField7.getText().isEmpty() ) {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Step 3");
//            alert.setHeaderText( "Missing Field" );
//            alert.setContentText( "Field 7 is required." );
//            alert.showAndWait();
//            return false;
//        }
        return true;
    }

    @Submit
    public void submit() throws Exception {
        if( log.isDebugEnabled() ) {
            log.debug("[SUBMIT] the user has completed step Pyramid Configuration");
        }
    }
}


