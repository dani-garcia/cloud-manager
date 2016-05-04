package com.cloudmanager.gui.controller;

import com.cloudmanager.core.config.RepoManager;
import com.cloudmanager.core.model.FileRepo;
import com.cloudmanager.core.services.local.LocalService;
import com.cloudmanager.gui.controller.fileview.AbstractFileViewController;
import com.cloudmanager.gui.controller.fileview.LocalViewController;
import com.cloudmanager.gui.controller.fileview.RemoteViewController;
import com.cloudmanager.gui.util.ResourceManager;
import com.cloudmanager.gui.view.RepoListCell;
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

public class RepoViewController {
    @FXML
    private BorderPane columnViewBorderPane;
    @FXML
    private ComboBox<FileRepo> repoSelector;
    @FXML
    private Button reloadButton;

    private StringProperty ownSelectionProperty;
    private ObservableStringValue otherSelectionValue;


    public void initialize(StringProperty own, ObservableStringValue other) {
        // We set a repo change listener. When the repos change, we reload the tabs
        RepoManager.getInstance().addListener(__ -> loadSelection());

        // Set properties
        this.ownSelectionProperty = own;
        this.otherSelectionValue = other;

        loadSelection();

        // TODO Implement the reload button
        reloadButton.setOnAction(event -> select(null));
    }

    public void select(FileRepo repo) {
        repoSelector.getSelectionModel().select(repo);
    }

    private final ChangeListener<? super FileRepo> selectionChanged = (obs, oldVal, newVal) -> loadService(newVal);

    private void loadSelection() {
        // Disable the listener temporarily
        repoSelector.getSelectionModel().selectedItemProperty().removeListener(selectionChanged);

        // Get the current selection, if there is one, to restore it later
        FileRepo selection = repoSelector.getSelectionModel().selectedItemProperty().get();
        String selectionId = selection != null ? selection.getId() : null;

        // Set the new values
        repoSelector.setCellFactory((val) -> new RepoListCell(this.otherSelectionValue));
        repoSelector.setButtonCell(new RepoListCell(this.otherSelectionValue));
        repoSelector.getItems().setAll(RepoManager.getInstance().getRepos());

        // Get the new selection (if exists)
        FileRepo newSelection = RepoManager.getInstance().getRepo(selectionId);

        if (newSelection == null) {
            // Clear the selector and the panel
            repoSelector.valueProperty().setValue(null);
            loadService(null);
        } else {
            select(newSelection);
        }

        // Restore the listener
        repoSelector.getSelectionModel().selectedItemProperty().addListener(selectionChanged);
    }

    private void loadService(FileRepo repo) {
        try {
            // Load the fxml
            FXMLLoader loader = ResourceManager.getFXMLLoader("/view/FileTreeView.fxml");

            if (repo != null) {


                loader.setController(getController(repo));
                this.ownSelectionProperty.set(repo.getId());

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


    private static AbstractFileViewController getController(FileRepo repo) {
        if (repo.getServiceName().equals(LocalService.SERVICE_NAME))
            return new LocalViewController(repo.getService());
        else
            return new RemoteViewController(repo.getService());
    }
}
