package com.uitgis.plugin.tilegenerator;

import com.uitgis.maple.application.ContentID;
import com.uitgis.maple.common.util.Util;
import com.uitgis.maple.contents.map.ui.MapTabPane;
import com.uitgis.sdk.controls.MapControl;

import framework.FrameworkManager;
import framework.i18n.I18N;
import framework.ribbon.event.RibbonEventHandler;
import javafx.collections.MapChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class OpenGenerator extends RibbonEventHandler {

	private boolean opened;
	
	private double xOffset = 0;
    private double yOffset = 0;
    
	public String getContentID() {
		return "tilegenerator";
	}

	public void statusChanged(Change<String, Object> arg) {
		
	}

	public void doAction(ActionEvent event) {
		if (opened) {
			return;
		}
		
		MapTabPane mapTabPane = (MapTabPane) FrameworkManager.getUserContent(ContentID.MAP_TAB_KEY);
		MapControl mapControl = mapTabPane.getSelectedMapControl();

		if (mapControl == null) {
			mapControl = Util.newMap();
		}
		

		TileMainPane tileGenPane = new TileMainPane(mapControl);
		Stage d = new Stage();
		
//		d.setScene(new Scene(tileGenPane,400,600));


		
		HBox titleBar = new HBox();
		ImageView image = new ImageView();		
		Label label = new Label(I18N.getText("window.title"));
		label.setTextFill(Color.WHITE);
		
		HBox spacer = new HBox();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		Button closeBtn = new Button();
		closeBtn.setOnAction(e->{d.close();});
		
		titleBar.getChildren().addAll(image, label, spacer, closeBtn);
		
		Separator sep = new Separator(Orientation.HORIZONTAL);
		sep.setMinHeight(1);
		sep.setMaxHeight(1);
		
		HBox sepCon = new HBox(sep);
		HBox.setHgrow(sep, Priority.ALWAYS);
		
		
		VBox box = new VBox();

		HBox contents = new HBox(tileGenPane);

		box.getChildren().addAll(titleBar, sepCon, contents);
		VBox.setVgrow(contents, Priority.ALWAYS);
		
		titleBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
		titleBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                d.setX(event.getScreenX() - xOffset);
                d.setY(event.getScreenY() - yOffset);
            }
        });		
	
		box.setStyle("-fx-border-color:#cccccc;");	
		sepCon.setPadding(new Insets(0, 5, 0, 5));
		contents.setPadding(new Insets(15));
		box.setPadding(new Insets(0));		
		titleBar.setPadding(new Insets(5));
		titleBar.setAlignment(Pos.CENTER);
		titleBar.setSpacing(5);		
	
		image.setImage(IconResources.ICON_TILEGENERATOR);
	
		
		titleBar.setId("tileskin-titlebar");
		closeBtn.setId("tileskin-closebutton");
		contents.setId("tileskin-contents");
		sep.setId("tileskin-underline");
		
		Scene scene = new Scene(box, 400, 600);
		String css = this.getClass().getResource("/styles/tilemap.css").toExternalForm(); 
		scene.getStylesheets().add(css);
		
		d.setScene(scene);
		d.initStyle(StageStyle.UNDECORATED);
		d.show();
		

	}

}
