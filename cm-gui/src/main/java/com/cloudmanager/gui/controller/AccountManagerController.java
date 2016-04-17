package com.cloudmanager.gui.controller;

import com.cloudmanager.core.config.AccountManager;
import com.cloudmanager.core.model.ServiceAccount;
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

public class AccountManagerController {
    @FXML
    private Parent root;

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
        accountTable.getItems().setAll(AccountManager.getInstance().getAccounts());
        AccountManager.getInstance().addListener(accounts -> accountTable.getItems().setAll(accounts));

        iconColumn.setCellValueFactory(s -> {
            Image icon = ResourceManager.loadImage(s.getValue().getService().getIcon());
            ImageView view = new ImageView(icon);

            view.fitWidthProperty().bind(iconColumn.widthProperty());
            view.fitHeightProperty().bind(iconColumn.widthProperty());

            return new SimpleObjectProperty<>(view);
        });

        serviceNameColumn.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getServiceName()));
        accountNameColumn.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getName()));

        setButtons();
    }

    private void setButtons() {
        newButton.setOnAction(event -> {
            Parent newWindow = ResourceManager.loadFXML("/view/accounts/AccountLogin.fxml");

            Stage stage = new Stage();
            stage.initOwner(root.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);

            stage.setTitle(ResourceManager.getString("login_title"));
            stage.getIcons().add(ResourceManager.loadImage("/branding/app-icon.png"));

            stage.setScene(new Scene(newWindow));

            stage.show();
        });

        removeButton.setOnAction(event -> {
            ServiceAccount acc = accountTable.getSelectionModel().getSelectedItem();

            if (acc == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, ResourceManager.getString("account_not_selected"));
                alert.showAndWait();
                return;
            }

            AccountManager.getInstance().removeAccount(acc);

            // Reload the data
            initialize();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, ResourceManager.getString("account_delete_success", acc.getName()));
            alert.showAndWait();
        });
    }
}
