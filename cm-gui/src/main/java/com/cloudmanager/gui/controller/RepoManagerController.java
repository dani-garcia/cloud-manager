package com.cloudmanager.gui.controller;

import com.cloudmanager.core.config.RepoManager;
import com.cloudmanager.core.model.FileRepo;
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

public class RepoManagerController {
    @FXML
    private Parent root;

    @FXML
    private TableView<FileRepo> repoTable;

    @FXML
    private TableColumn<FileRepo, ImageView> iconColumn;
    @FXML
    private TableColumn<FileRepo, String> serviceNameColumn;
    @FXML
    private TableColumn<FileRepo, String> repoNameColumn;

    @FXML
    private Button newButton;
    @FXML
    private Button removeButton;

    @FXML
    private void initialize() {
        repoTable.getItems().setAll(RepoManager.getInstance().getRepos());
        RepoManager.getInstance().addListener(accounts -> repoTable.getItems().setAll(accounts));

        iconColumn.setCellValueFactory(s -> {
            Image icon = ResourceManager.loadImage(s.getValue().getService().getIcon());
            ImageView view = new ImageView(icon);

            view.fitWidthProperty().bind(iconColumn.widthProperty());
            view.fitHeightProperty().bind(iconColumn.widthProperty());

            return new SimpleObjectProperty<>(view);
        });

        serviceNameColumn.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getService().getServiceDisplayName()));
        repoNameColumn.setCellValueFactory(s -> new SimpleStringProperty(s.getValue().getName()));

        setButtons();
    }

    private void setButtons() {
        newButton.setOnAction(event -> {
            Parent newWindow = ResourceManager.loadFXML("/view/ServiceLogin.fxml");

            Stage stage = new Stage();
            stage.initOwner(root.getScene().getWindow());
            stage.initModality(Modality.WINDOW_MODAL);

            stage.setTitle(ResourceManager.getString("login_title"));
            stage.getIcons().add(ResourceManager.loadImage("/branding/app-icon.png"));

            stage.setScene(new Scene(newWindow));

            stage.show();
        });

        removeButton.setOnAction(event -> {
            FileRepo acc = repoTable.getSelectionModel().getSelectedItem();

            if (acc == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, ResourceManager.getString("repo_not_selected"));
                alert.showAndWait();
                return;
            }

            RepoManager.getInstance().removeRepo(acc);

            // Reload the data
            initialize();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, ResourceManager.getString("repo_delete_success", acc.getName()));
            alert.showAndWait();
        });
    }
}
