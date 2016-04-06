package com.cloudmanager.gui.controller;

import com.cloudmanager.core.config.ServiceManager;
import com.cloudmanager.core.services.FileService;
import com.cloudmanager.gui.controller.fileview.AbstractFileViewController;
import com.cloudmanager.gui.util.ResourceManager;
import com.cloudmanager.gui.view.FileListCell;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.List;

public class ColumnViewController {
    @FXML
    private BorderPane columnViewBorderPane;
    @FXML
    private ComboBox<FileService> serviceSelector;
    @FXML
    private Button reloadButton;

    private StringProperty ownSelectionProperty;
    private ObservableStringValue otherSelectionValue;


    public void initialize(StringProperty own, ObservableStringValue other) {
        ServiceManager serviceManager = ServiceManager.getInstance();

        // We set an account change listener. When the accounts change, we reload the tabs
        serviceManager.addListener(this::loadSelection);

        // Set properties
        this.ownSelectionProperty = own;
        this.otherSelectionValue = other;

        loadSelection(serviceManager.getServices());
    }

    public void select(FileService service) {
        serviceSelector.getSelectionModel().select(service);
    }

    private void loadSelection(List<FileService> accounts) {
        serviceSelector.setCellFactory((val) -> new FileListCell(this.otherSelectionValue));
        serviceSelector.setButtonCell(new FileListCell(this.otherSelectionValue));

        serviceSelector.getItems().setAll(accounts);

        serviceSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> loadService(newValue));
    }

    private void loadService(FileService service) {
        this.ownSelectionProperty.set(service.getAccountId());

        try {
            // Load the fxml
            FXMLLoader loader = ResourceManager.getFXMLLoader("/view/FileTreeView.fxml");
            loader.setController(AbstractFileViewController.getController(service));

            // Set it in the center
            SplitPane pane = loader.load();
            columnViewBorderPane.setCenter(pane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
