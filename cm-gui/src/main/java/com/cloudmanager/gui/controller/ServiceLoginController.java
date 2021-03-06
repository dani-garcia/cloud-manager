package com.cloudmanager.gui.controller;

import com.cloudmanager.core.api.login.LoginField;
import com.cloudmanager.core.api.login.LoginProcedure;
import com.cloudmanager.core.api.login.LoginProcedure.Status;
import com.cloudmanager.core.api.service.ServiceFactory;
import com.cloudmanager.core.managers.ServiceFactoryLocator;
import com.cloudmanager.core.managers.ServiceManager;
import com.cloudmanager.core.model.FileServiceSettings;
import com.cloudmanager.gui.util.ResourceManager;
import com.cloudmanager.gui.view.ServiceFactoryListCell;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Controls the login window. It's job is to generate a login form based on
 * the fields from the {@link LoginProcedure} of the selected service,
 * and save the login details once the login is complete.
 */
public class ServiceLoginController {
    @FXML
    private Parent root;

    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<ServiceFactory> serviceSelector;
    @FXML
    private GridPane loginForm;

    private LoginProcedure login;

    @FXML
    private void initialize() {
        serviceSelector.setCellFactory(val -> new ServiceFactoryListCell());
        serviceSelector.setButtonCell(new ServiceFactoryListCell());
        serviceSelector.getItems().setAll(ServiceFactoryLocator.findAll());

        serviceSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> loadWindow(newValue));

        serviceSelector.disableProperty().bind(nameField.textProperty().isEmpty());
    }

    private void loadWindow(ServiceFactory factory) {
        // Once a service is selected, don't allow the name to change
        nameField.setDisable(true);

        // If there is a login in process, cancel it
        if (login != null)
            login.cancel();

        login = factory.startLoginProcedure();
        login.preLogin(nameField.getText());

        // Add the fields
        addFieldGrid(login);

        // Set the completion listener
        login.addLoginCompleteListener(this::loginCompleted);

        // Set the closing listener
        root.getScene().getWindow().setOnCloseRequest(event -> {
            if (login != null)
                login.cancel();
        });
    }

    private void addFieldGrid(LoginProcedure login) {
        loginForm.getChildren().clear();
        int row = 0;

        // For each field on the login procedure, we add the corresponding element to the form
        for (LoginField field : login.getFields()) {
            switch (field.getType()) {
                case INPUT:
                    TextField inputTextField = new TextField();
                    inputTextField.textProperty().addListener((obs, oldVal, newVal) -> field.setValue(newVal));

                    loginForm.add(new Label(ResourceManager.getString(field.getName())), 0, row);
                    loginForm.add(inputTextField, 1, row);
                    break;

                case OUTPUT:
                    TextField readOnlyText = new TextField(field.getValue());
                    readOnlyText.setEditable(false);

                    Button copyButton = new Button("", new ImageView("icons/floppy.png"));
                    copyButton.setOnAction(event -> {
                        final Clipboard clipboard = Clipboard.getSystemClipboard();
                        final ClipboardContent content = new ClipboardContent();
                        content.putString(field.getValue());
                        clipboard.setContent(content);
                    });

                    loginForm.add(new Label(ResourceManager.getString(field.getName())), 0, row);
                    loginForm.add(new BorderPane(readOnlyText, null, copyButton, null, null), 1, row);
                    break;

                case PLAIN_TEXT:
                    Label label = new Label(ResourceManager.getString(field.getValue()));
                    label.setWrapText(true);
                    loginForm.add(label, 0, row, 2, 1);
                    break;
            }
            row++;
        }

        // If the login is automatic, there is no need for a button
        if (login.isPostLoginManual()) {
            Button loginButton = new Button(ResourceManager.getString("add"));
            loginButton.setOnAction(e -> login.postLogin());
            loginForm.add(loginButton, 0, row);
        }
    }

    // This is used as the completion listener
    private void loginCompleted(Status status, FileServiceSettings settings) {
        Platform.runLater(() -> {

            switch (status) {
                case OK:
                    // Add the settings
                    ServiceManager.getInstance().addServiceSettings(settings);

                    new Alert(Alert.AlertType.INFORMATION, ResourceManager.getString("service_added"))
                            .show();
                    break;

                case DENIED_PERMISSION:
                    new Alert(Alert.AlertType.INFORMATION, ResourceManager.getString("error_denied_permission"))
                            .showAndWait();
                    break;
                case OTHER_ERR:
                    new Alert(Alert.AlertType.INFORMATION, ResourceManager.getString("error_adding_service"))
                            .showAndWait();
                    break;
            }

            ((Stage) root.getScene().getWindow()).close();
        });
    }
}
