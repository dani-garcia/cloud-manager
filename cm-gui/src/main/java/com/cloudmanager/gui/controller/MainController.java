package com.cloudmanager.gui.controller;

import com.cloudmanager.core.config.Config;
import com.cloudmanager.core.config.ConfigManager;
import com.cloudmanager.core.config.RepoManager;
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
    private RepoViewController leftColumnController;
    @FXML
    private RepoViewController rightColumnController;

    private StringProperty leftSelection = new SimpleStringProperty();
    private StringProperty rightSelection = new SimpleStringProperty();

    @FXML
    private void initialize() {
        // Initialize the columns
        leftColumnController.initialize(leftSelection, rightSelection);
        rightColumnController.initialize(rightSelection, leftSelection);

        final Config conf = ConfigManager.getConfig();
        final String leftPanel = "leftPanel";
        final String rightPanel = "rightPanel";

        // Save the changes on the config
        leftSelection.addListener((obs, oldVal, newVal) -> conf.putSetting(leftPanel, newVal));
        rightSelection.addListener((obs, oldVal, newVal) -> conf.putSetting(rightPanel, newVal));

        // Restore last services
        leftColumnController.select(RepoManager.getInstance().getRepo(conf.getSetting(leftPanel)));
        rightColumnController.select(RepoManager.getInstance().getRepo(conf.getSetting(rightPanel)));
    }
}
