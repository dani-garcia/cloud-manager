package com.cloudmanager.gui.controller;

import com.cloudmanager.core.config.ConfigManager;
import com.cloudmanager.core.config.ServiceManager;
import com.cloudmanager.core.model.ServiceAccount;
import com.cloudmanager.gui.util.ResourceManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AccountManagerController {
    @FXML
    public TableView<ServiceAccount> accountTable;

    @FXML
    public TableColumn<ServiceAccount, ImageView> iconColumn;
    @FXML
    public TableColumn<ServiceAccount, String> serviceNameColumn;
    @FXML
    public TableColumn<ServiceAccount, String> accountNameColumn;

    @FXML
    public Button newButton;
    @FXML
    public Button removeButton;

    @FXML
    private void initialize() {
        accountTable.getItems().setAll(ConfigManager.getConfig().getAccounts());

        iconColumn.setCellValueFactory(s -> {
            Image icon = ResourceManager.loadImage(s.getValue().getService().getIcon());
            ImageView view = new ImageView(icon);

            view.fitWidthProperty().bind(iconColumn.widthProperty());
            view.fitHeightProperty().bind(iconColumn.widthProperty());

            return new SimpleObjectProperty<>(view);
        });

        serviceNameColumn.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getServiceName()));
        accountNameColumn.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getName()));


        newButton.setOnAction(event -> {

        });

        removeButton.setOnAction(event -> {
            ServiceAccount acc = accountTable.getSelectionModel().getSelectedItem();

            if (acc == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, ResourceManager.getString("account_not_selected"));
                alert.showAndWait();
                return;
            }

            ServiceManager.getInstance().removeServiceAccount(acc);

            // Reload the data
            initialize();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, ResourceManager.getString("account_delete_success", acc.getName()));
            alert.showAndWait();
        });
    }
}
