package com.cloudmanager.gui.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class MainController {

    @FXML
    private BorderPane leftColumn;
    @FXML
    private BorderPane rightColumn;

    @FXML
    private ColumnViewController leftColumnController;
    @FXML
    private ColumnViewController rightColumnController;

    private StringProperty leftSelection = new SimpleStringProperty();
    private StringProperty rightSelection = new SimpleStringProperty();

    @FXML
    private void initialize() {
        leftColumnController.initialize(leftSelection, rightSelection);
        rightColumnController.initialize(rightSelection, leftSelection);
    }
}
