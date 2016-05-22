package com.cloudmanager.gui;

import com.cloudmanager.core.api.service.FileService;
import com.cloudmanager.core.config.ConfigManager;
import com.cloudmanager.core.service.TransferService;
import com.cloudmanager.gui.util.ResourceManager;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * The main application class.
 */
public class MainApp extends Application {

    private static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) { launch(MainApp.class, args); }

    @Override
    public void start(Stage primaryStage) {
        // Init the configuration
        initConfig();

        // Set the primary stage
        MainApp.primaryStage = primaryStage;
        MainApp.primaryStage.setTitle(FileService.APP_NAME);
        MainApp.primaryStage.getIcons().add(ResourceManager.loadImage("/branding/app-icon.png"));

        // Prepare the main layout
        initRootLayout();
    }

    private void initConfig() {
        // If we can't load/save the config, show an error and exit
        if (!ConfigManager.save()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ResourceManager.getString("save_config_error"));

            alert.showAndWait();
            System.exit(0);
        }
    }

    private void initRootLayout() {
        // Load root layout from fxml file.
        Parent root = ResourceManager.loadFXML("/view/RootLayout.fxml");

        // Show the scene containing the root layout.
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        // Show stage
        primaryStage.show();

        // If there are downloads pending, alert the user
        primaryStage.setOnCloseRequest(e -> {
            if (TransferService.get().getTransfersInProgress() > 0) {

                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle(ResourceManager.getString("unfinished_transfers"));
                alert.setHeaderText(ResourceManager.getString("unfinished_transfers"));
                alert.setContentText(ResourceManager.getString("unfinished_transfers_really_close"));

                alert.showAndWait().ifPresent(type -> {
                    if (type == ButtonType.CANCEL || type == ButtonType.CLOSE) {
                        e.consume(); // If the user cancels, don't close the application
                    }
                });
            }
        });

        // Make sure to save everything before closing
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Saving settings on close");
            ConfigManager.save();
        }));
    }
}
