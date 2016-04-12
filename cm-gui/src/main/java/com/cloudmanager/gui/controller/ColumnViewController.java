package com.cloudmanager.gui.controller;

import com.cloudmanager.core.config.AccountManager;
import com.cloudmanager.core.config.ServiceManager;
import com.cloudmanager.core.services.FileService;
import com.cloudmanager.gui.controller.fileview.AbstractFileViewController;
import com.cloudmanager.gui.util.ResourceManager;
import com.cloudmanager.gui.view.ServiceListCell;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

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
        // We set an account change listener. When the accounts change, we reload the tabs
        AccountManager.getInstance().addListener(__ -> loadSelection());

        // Set properties
        this.ownSelectionProperty = own;
        this.otherSelectionValue = other;

        loadSelection();

        // TODO
        reloadButton.setOnAction(event -> select(null));
    }

    public void select(FileService service) {
        serviceSelector.getSelectionModel().select(service);
    }

    private final ChangeListener<? super FileService> selectionChanged = (obs, oldVal, newVal) -> loadService(newVal);

    private void loadSelection() {
        // Disable the listener temporarily
        serviceSelector.getSelectionModel().selectedItemProperty().removeListener(selectionChanged);

        // Get the current selection, if there is one, to restore it later
        FileService selection = serviceSelector.getSelectionModel().selectedItemProperty().get();
        String selectionId = selection != null ? selection.getAccountId() : null;

        // Set the new values
        serviceSelector.setCellFactory((val) -> new ServiceListCell(this.otherSelectionValue));
        serviceSelector.setButtonCell(new ServiceListCell(this.otherSelectionValue));
        serviceSelector.getItems().setAll(ServiceManager.getInstance().getServices());

        // Get the new selection
        FileService newSelection = ServiceManager.getInstance().getService(selectionId);

        if (newSelection == null) {
            // Clear the selector and the panel
            serviceSelector.valueProperty().setValue(null);
            loadService(null);
        } else {
            select(newSelection);
        }

        // Restore the listener
        serviceSelector.getSelectionModel().selectedItemProperty().addListener(selectionChanged);
    }

    private void loadService(FileService service) {
        try {
            // Load the fxml
            FXMLLoader loader = ResourceManager.getFXMLLoader("/view/FileTreeView.fxml");

            if (service != null) {
                loader.setController(AbstractFileViewController.getController(service));
                this.ownSelectionProperty.set(service.getAccountId());

            } else {
                this.ownSelectionProperty.set(null);
            }

            // Set it in the center
            SplitPane pane = loader.load();
            columnViewBorderPane.setCenter(pane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
