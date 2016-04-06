package com.cloudmanager.gui.controller;

import com.cloudmanager.core.config.ServiceManager;
import com.cloudmanager.core.services.local.LocalService;
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

        // Select the local service on the left
        leftColumnController.select(ServiceManager.getInstance().getService(LocalService.SERVICE_NAME));
    }
}
