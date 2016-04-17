package com.cloudmanager.gui.controller;

import com.cloudmanager.core.services.FileService;
import com.cloudmanager.core.services.factories.ServiceFactory;
import com.cloudmanager.core.services.factories.ServiceFactoryLocator;
import com.cloudmanager.core.services.login.LoginField;
import com.cloudmanager.core.services.login.LoginProcedure;
import com.cloudmanager.gui.util.ResourceManager;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AccountLoginController {
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
        serviceSelector.getItems().setAll(ServiceFactoryLocator.listAll());
        serviceSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> loadWindow(newValue));

        serviceSelector.disableProperty().bind(nameField.textProperty().isEmpty()); // TODO Comprobar que nombre ya existe?
    }

    private void loadWindow(ServiceFactory factory) {
        nameField.setDisable(true);

        // If there is a login in process, cancel it
        if (login != null)
            login.cancel();

        FileService service = factory.create();
        login = service.startLoginProcedure();
        login.preLogin(nameField.getText());

        // Add the fields
        loginForm.getChildren().clear();
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
        int row = 0;

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

        if (login.isPostLoginManual()) {
            Button loginButton = new Button(ResourceManager.getString("login"));
            loginButton.setOnAction(e -> login.postLogin());
            loginForm.add(loginButton, 0, row);
        }
    }

    private void loginCompleted(boolean success) {
        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, ResourceManager.getString("account_added"));
            alert.show();

            ((Stage) root.getScene().getWindow()).close();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, ResourceManager.getString("error_adding_account"));
            alert.showAndWait();
        }
    }
}
