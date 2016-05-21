package com.cloudmanager.gui.controller;

import com.cloudmanager.core.config.RepoManager;
import com.cloudmanager.core.model.FileRepo;
import com.cloudmanager.gui.util.ResourceManager;
import com.cloudmanager.gui.view.RepoListCell;
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
 * Handles the selection of a repository from the dropdown.
 * <p>
 * When the selection changes, it loads the new repository in the panel.
 */
public class RepoViewController {
    @FXML
    private BorderPane columnViewBorderPane;
    @FXML
    private ComboBox<FileRepo> repoSelector;

    private StringProperty ownSelectionProperty;
    private ObservableStringValue otherSelectionValue;

    private final ChangeListener<? super FileRepo> selectionChanged = (obs, oldVal, newVal) -> loadService(newVal);

    /**
     * Initializes the controller. Both parameters are used to control that the repository
     * selected in one side cannot be selected in the other at the same time.
     *
     * @param own   The selection of this panel
     * @param other The selection of the other panel
     */
    public void initialize(StringProperty own, ObservableStringValue other) {
        // We set a repo change listener. When the repos change, we reload the tabs
        RepoManager.getInstance().addListener(__ -> loadSelection());

        // Set properties
        this.ownSelectionProperty = own;
        this.otherSelectionValue = other;

        loadSelection();
    }

    /**
     * Selects the given repository
     *
     * @param repo The repository to select
     */
    public void select(FileRepo repo) {
        repoSelector.getSelectionModel().select(repo);
    }

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


                loader.setController(new FileViewController(repo.getService()));
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
}
