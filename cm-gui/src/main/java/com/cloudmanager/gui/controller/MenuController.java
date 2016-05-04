package com.cloudmanager.gui.controller;

import com.cloudmanager.gui.MainApp;
import com.cloudmanager.gui.util.ResourceManager;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class MenuController {
    @FXML
    private Parent root;

    @FXML
    private MenuItem repoManagerItem;

    @FXML
    private MenuItem fullScreenItem;

    @FXML
    private MenuItem exitItem;

    @FXML
    private MenuItem aboutItem;

    @FXML
    private void initialize() {
        exitItem.setOnAction(event -> {
            Window window = root.getScene().getWindow();
            window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
        });

        aboutItem.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Daniel García García - UO231763\nUniversidad de Oviedo 2016");
            alert.showAndWait();
        });

        fullScreenItem.setOnAction(event -> {
            MainApp.getPrimaryStage().setFullScreen(true);
        });

        repoManagerItem.setOnAction(event -> {
            Parent newWindow = ResourceManager.loadFXML("/view/RepoManager.fxml");

            Stage stage = new Stage();

            stage.initOwner(root.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);

            stage.setTitle(ResourceManager.getString("repo_manager"));
            stage.getIcons().add(ResourceManager.loadImage("/branding/app-icon.png"));

            stage.setScene(new Scene(newWindow));

            stage.show();
        });
    }
}
