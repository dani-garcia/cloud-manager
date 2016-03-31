package com.cloudmanager.gui.controller;

import com.cloudmanager.gui.MainApp;
import com.cloudmanager.gui.util.ResourceManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MenuController {

    @FXML
    private MenuItem accountManagerItem;

    @FXML
    private MenuItem fullScreenItem;

    @FXML
    private MenuItem exitItem;

    @FXML
    private MenuItem aboutItem;

    @FXML
    private void initialize() {
        exitItem.setOnAction(event -> Platform.exit());

        aboutItem.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Daniel García García - UO231763\nUniversidad de Oviedo 2016");
            alert.showAndWait();
        });

        fullScreenItem.setOnAction(event -> {
            MainApp.getPrimaryStage().setFullScreen(true);
        });

        accountManagerItem.setOnAction(event -> {
            Parent root = ResourceManager.loadFXML("/view/AccountManager.fxml");

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);

            stage.initOwner(MainApp.getPrimaryStage());

            stage.setTitle(ResourceManager.getString("account_manager"));
            stage.getIcons().add(ResourceManager.loadImage("/branding/app-icon.png"));

            stage.setScene(new Scene(root));

            stage.show();
        });
    }
}
