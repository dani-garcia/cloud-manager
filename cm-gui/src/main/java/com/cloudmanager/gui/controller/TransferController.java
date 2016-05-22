package com.cloudmanager.gui.controller;

import com.cloudmanager.core.model.ModelFile;
import com.cloudmanager.core.service.TransferService;
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

/**
 * Handles the in-progress transfer bar a the bottom of the window.
 */
public class TransferController {
    @FXML
    private Parent root;
    @FXML
    private TitledPane downloadPane;
    @FXML
    private VBox downloadsBox;


    private Map<ModelFile, TransferBox> transfersMap = new HashMap<>();

    public TransferController() {}

    @FXML
    public void initialize() {
        // The sixe of the bar depends on the size of the children and the number of trasfers
        // The title of the bar also depends on the number of transfers
        IntegerBinding sizeBinding = Bindings.size(downloadsBox.getChildren());

        downloadsBox.prefHeightProperty().bind(Bindings.multiply(60, sizeBinding));
        downloadPane.textProperty().bind(Bindings.format(ResourceManager.getString("transfers"), sizeBinding));

        // Listen to the transfers progress
        TransferService.get().addProgressListener((f, p) -> Platform.runLater(() -> {
            TransferBox transfer = transfersMap.get(f);

            // If it's not on the map, we create it and add it
            if (transfer == null) {
                transfer = new TransferBox(f);
                transfersMap.put(f, transfer);
                downloadsBox.getChildren().add(transfer);
            }

            // Update the progress
            transfer.getProgressBar().setProgress(p / 100d);

            // If it finished, remove it and alert the user
            if (p >= 100) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(ResourceManager.getString("transfer_complete", f.getName()));
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.show();

                downloadsBox.getChildren().remove(transfer);
                transfersMap.remove(f);
            }
        }));
    }

    private class TransferBox extends VBox {
        private ProgressBar progressBar = new ProgressBar();

        TransferBox(ModelFile file) {
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
