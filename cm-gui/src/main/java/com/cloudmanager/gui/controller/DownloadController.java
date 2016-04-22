package com.cloudmanager.gui.controller;

import com.cloudmanager.core.model.ModelFile;
import com.cloudmanager.core.services.DownloadService;
import com.cloudmanager.gui.util.ResourceManager;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.util.HashMap;
import java.util.Map;

public class DownloadController {
    @FXML
    private Parent root;
    @FXML
    private TitledPane downloadPane;
    @FXML
    private VBox downloadsBox;


    private Map<ModelFile, DownloadBox> downloadsMap = new HashMap<>();

    public DownloadController() {}

    @FXML
    public void initialize() {
        IntegerBinding sizeBinding = Bindings.size(downloadsBox.getChildren());

        downloadsBox.prefHeightProperty().bind(Bindings.multiply(60, sizeBinding));
        downloadPane.textProperty().bind(Bindings.format(ResourceManager.getString("transfers"), sizeBinding));

        DownloadService.get().addProgressListener((f, p) -> Platform.runLater(() -> {
            DownloadBox download = downloadsMap.get(f);

            if (download == null) {
                download = new DownloadBox(f);
                downloadsMap.put(f, download);
                downloadsBox.getChildren().add(download);
            }

            download.getProgressBar().setProgress(p / 100d);

            if (p >= 100) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(ResourceManager.getString("transfer_complete", f.getName()));
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.show();

                download.getChildren().remove(download);
                downloadsMap.remove(f);
            }
        }));
    }

    private class DownloadBox extends VBox {
        private ProgressBar progressBar = new ProgressBar();

        DownloadBox(ModelFile file) {
            progressBar.setMaxWidth(Double.MAX_VALUE);

            setPrefHeight(50);
            setStyle("-fx-background-color: lightgrey");
            setPadding(new Insets(5));

            getChildren().add(new Label(file.getName()));
            getChildren().add(progressBar);
        }

        ProgressBar getProgressBar() {
            return progressBar;
        }
    }
}
