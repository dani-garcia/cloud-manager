package com.cloudmanager.gui;

import com.cloudmanager.core.config.ConfigManager;
import com.cloudmanager.core.services.DownloadService;
import com.cloudmanager.core.services.FileService;
import com.cloudmanager.gui.util.ResourceManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Locale;

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

        MainApp.primaryStage = primaryStage;
        MainApp.primaryStage.setTitle(FileService.APP_NAME);
        MainApp.primaryStage.getIcons().add(ResourceManager.loadImage("/branding/app-icon.png"));

        initRootLayout();
    }

    private void initConfig() {
        // If we can't load/save the config, show an error and exit
        if (!ConfigManager.save()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ResourceManager.getString("save_config_error"));

            alert.showAndWait();
            System.exit(0);
        }

        // Set the language from the config
        Locale.setDefault(ConfigManager.getConfig().getLocale());
    }

    private void initRootLayout() {
        // Load root layout from fxml file.
        Parent root = ResourceManager.loadFXML("/view/RootLayout.fxml");

        // Show the scene containing the root layout.
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        // Show stage
        primaryStage.show();

        // TODO Temporary
        DownloadService.get().addProgressListener((f, p) -> {
            if (p < 100) {
                System.out.println(p);
                return;
            }

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Moved '" + f.getName());
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.show();
            });
        });
    }
}
