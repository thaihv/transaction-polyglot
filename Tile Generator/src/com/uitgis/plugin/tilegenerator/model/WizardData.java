package com.uitgis.plugin.tilegenerator.model;

import com.uitgis.sdk.gdx.GDX;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class WizardData {

    private final StringProperty field1 = new SimpleStringProperty();
    private final StringProperty field2 = new SimpleStringProperty();
    private final StringProperty field3 = new SimpleStringProperty();
    private final StringProperty field4 = new SimpleStringProperty();
    private final StringProperty field5 = new SimpleStringProperty();
    private final StringProperty field6 = new SimpleStringProperty();
    private final StringProperty field7 = new SimpleStringProperty();
    
 


	public static final int MAP_FROM_MAPEDITOR = 0;
	public static final int MAP_FROM_GDX = 1;
	public static final int FULL_EXTENT			= 0;
	public static final int CURRENT_EXTENT		= 1;
	public static final int USER_DEFINED_EXTENT	= 2;
	public static final int ORIGIN_FROM_EXTENT = 0;
	public static final int ORIGIN_FROM_USER = 1;
	
	
	private int mOriginFrom;
	private int mGssIndex = -1;	
	private int mMapEditorIndex = -1;
	private int mMapFrom;
	private int mExtent;
	private int mMultipleNumber = 2;
	private GDX mGdx;
	
	private StringProperty mGdxPath = new SimpleStringProperty();	
	
	private final ObservableList<TileScale> listTileScale = FXCollections.observableArrayList(new TileScale(true, 0, 0));




	public ObservableList<TileScale> getListTileScale() {
		return listTileScale;
	}

	public String getField1() {
        return field1.get();
    }

    public StringProperty field1Property() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1.set(field1);
    }

    public String getField2() {
        return field2.get();
    }

    public StringProperty field2Property() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2.set(field2);
    }

    public String getField3() {
        return field3.get();
    }

    public StringProperty field3Property() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3.set(field3);
    }

    public String getField4() {
        return field4.get();
    }

    public StringProperty field4Property() {
        return field4;
    }

    public void setField4(String field4) {
        this.field4.set(field4);
    }

    public String getField5() {
        return field5.get();
    }

    public StringProperty field5Property() {
        return field5;
    }

    public void setField5(String field5) {
        this.field5.set(field5);
    }

    public String getField6() {
        return field6.get();
    }

    public StringProperty field6Property() {
        return field6;
    }

    public void setField6(String field6) {
        this.field6.set(field6);
    }

    public String getField7() {
        return field7.get();
    }

    public StringProperty field7Property() {
        return field7;
    }

    public void setField7(String field7) {
        this.field7.set(field7);
    }

    public void reset() {
        field1.set("");
        field2.set("");
        field3.set("");
        field4.set("");
        field5.set("");
        field6.set("");
        field7.set("");
        listTileScale.clear();
        listTileScale.add(new TileScale(true, 0, 0));
    }
}
