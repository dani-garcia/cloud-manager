package com.cloudmanager.gui.controller;

import com.cloudmanager.core.config.Config;
import com.cloudmanager.core.config.Config.Setting;
import com.cloudmanager.core.config.ConfigManager;
import com.cloudmanager.core.config.RepoManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

/**
 * Controls the main window and sets the initial service selections
 */
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

        // Restore last services
        leftColumnController.select(RepoManager.getInstance().getRepo(conf.getSetting(Setting.leftPanel)));
        rightColumnController.select(RepoManager.getInstance().getRepo(conf.getSetting(Setting.rightPanel)));

        // Save the changes on the config
        leftSelection.addListener((obs, oldVal, newVal) -> conf.putSetting(Setting.leftPanel, newVal));
        rightSelection.addListener((obs, oldVal, newVal) -> conf.putSetting(Setting.rightPanel, newVal));
    }
}
