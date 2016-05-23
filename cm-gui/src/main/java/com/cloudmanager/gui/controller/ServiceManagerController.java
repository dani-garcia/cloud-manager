package com.cloudmanager.gui.controller;

import com.cloudmanager.core.managers.ServiceManager;
import com.cloudmanager.core.model.FileServiceSettings;
import com.cloudmanager.gui.util.ResourceManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * Handles the service list window. It shows the current list and enables the new and delete buttons.
 */
public class ServiceManagerController {
    @FXML
    private Parent root;

    @FXML
    private TableView<FileServiceSettings> serviceTable;

    @FXML
    private TableColumn<FileServiceSettings, ImageView> iconColumn;
    @FXML
    private TableColumn<FileServiceSettings, String> serviceNameColumn;
    @FXML
    private TableColumn<FileServiceSettings, String> nameColumn;

    @FXML
    private Button newButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button closeButton;

    @FXML
    private void initialize() {
        // Add all the services
        serviceTable.getItems().setAll(ServiceManager.getInstance().getServiceSettings());
        ServiceManager.getInstance().addListener(services -> serviceTable.getItems().setAll(services));

        // Set the icon column
        iconColumn.setCellValueFactory(s -> {
            Image icon = ResourceManager.loadImage(s.getValue().getService().getIcon());
            ImageView view = new ImageView(icon);

            view.fitWidthProperty().bind(iconColumn.widthProperty());
            view.fitHeightProperty().bind(iconColumn.widthProperty());

            return new SimpleObjectProperty<>(view);
        });

        // Set the other two columns
        serviceNameColumn.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getService().getServiceDisplayName()));
        nameColumn.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getName()));

        // Set the buttons
        setButtons();
    }

    private void setButtons() {
        closeButton.setOnAction(event -> {
            Window window = root.getScene().getWindow();
            window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
        });

        newButton.setOnAction(event -> {
            Parent newWindow = ResourceManager.loadFXML("/view/ServiceLogin.fxml");

            Stage stage = new Stage();
            stage.initOwner(root.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);

            stage.setTitle(ResourceManager.getString("add_service"));
            stage.getIcons().add(ResourceManager.loadImage("/branding/app-icon.png"));

            stage.setScene(new Scene(newWindow));

            stage.show();
        });

        removeButton.setOnAction(event -> {
            FileServiceSettings acc = serviceTable.getSelectionModel().getSelectedItem();

            // Alert if nothing is selected
            if (acc == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, ResourceManager.getString("service_not_selected"));
                alert.showAndWait();
                return;
            }

            ServiceManager.getInstance().removeServiceSettings(acc);

            // Reload the data
            initialize();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, ResourceManager.getString("service_delete_success", acc.getName()));
            alert.showAndWait();
        });
    }
}
