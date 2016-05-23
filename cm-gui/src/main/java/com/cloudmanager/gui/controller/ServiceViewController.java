package com.cloudmanager.gui.controller;

import com.cloudmanager.core.managers.ServiceManager;
import com.cloudmanager.core.model.FileServiceSettings;
import com.cloudmanager.gui.util.ResourceManager;
import com.cloudmanager.gui.view.ServiceListCell;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

/**
 * Handles the selection of a service from the dropdown.
 * <p>
 * When the selection changes, it loads the new service in the panel.
 */
public class ServiceViewController {
    @FXML
    private BorderPane columnViewBorderPane;
    @FXML
    private ComboBox<FileServiceSettings> serviceSelector;

    private StringProperty ownSelectionProperty;
    private ObservableStringValue otherSelectionValue;

    private final ChangeListener<? super FileServiceSettings> selectionChanged = (obs, oldVal, newVal) -> loadService(newVal);

    /**
     * Initializes the controller. Both parameters are used to control that the service
     * selected in one side cannot be selected in the other at the same time.
     *
     * @param own   The selection of this panel
     * @param other The selection of the other panel
     */
    public void initialize(StringProperty own, ObservableStringValue other) {
        // We set a settings change listener. When the services change, we reload the tabs
        ServiceManager.getInstance().addListener(__ -> loadSelection());

        // Set properties
        this.ownSelectionProperty = own;
        this.otherSelectionValue = other;

        loadSelection();
    }

    /**
     * Selects the given service
     *
     * @param service The service to select
     */
    public void select(FileServiceSettings service) {
        serviceSelector.getSelectionModel().select(service);
    }

    private void loadSelection() {
        // Disable the listener temporarily
        serviceSelector.getSelectionModel().selectedItemProperty().removeListener(selectionChanged);

        // Get the current selection, if there is one, to restore it later
        FileServiceSettings selection = serviceSelector.getSelectionModel().selectedItemProperty().get();
        String selectionId = selection != null ? selection.getId() : null;

        // Set the new values
        serviceSelector.setCellFactory((val) -> new ServiceListCell(this.otherSelectionValue));
        serviceSelector.setButtonCell(new ServiceListCell(this.otherSelectionValue));
        serviceSelector.getItems().setAll(ServiceManager.getInstance().getServiceSettings());

        // Get the new selection (if exists)
        FileServiceSettings newSelection = ServiceManager.getInstance().getServiceSettings(selectionId);

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

    private void loadService(FileServiceSettings service) {
        try {
            // Load the fxml
            FXMLLoader loader = ResourceManager.getFXMLLoader("/view/FileTreeView.fxml");

            if (service != null) {
                loader.setController(new FileViewController(service.getService()));
                this.ownSelectionProperty.set(service.getId());

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
