package com.cloudmanager.gui.controller;

import com.cloudmanager.core.model.ModelFile;
import com.cloudmanager.core.model.ModelFile.Event;
import com.cloudmanager.core.api.service.FileService;
import com.cloudmanager.core.service.local.LocalService;
import com.cloudmanager.gui.util.ResourceManager;
import com.cloudmanager.gui.view.DraggableTableRow;
import com.cloudmanager.gui.view.DraggableTreeCell;
import com.cloudmanager.gui.view.FileTreeItem;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

import java.util.Date;

/**
 * Handles the file tree and the file table.
 * <p>
 * It's job is to load the tree, and update the table when the selected folder changes.
 * <p>
 * It also reloads the tree when the files notify a change
 */
public class FileViewController {
    @FXML
    TreeView<ModelFile> fileTree;

    @FXML
    private TableView<ModelFile> fileTable;

    @FXML
    private TableColumn<ModelFile, ImageView> iconColumn;
    @FXML
    private TableColumn<ModelFile, String> nameColumn;
    @FXML
    private TableColumn<ModelFile, Long> sizeColumn;
    @FXML
    private TableColumn<ModelFile, ModelFile.Type> typeColumn;
    @FXML
    private TableColumn<ModelFile, Date> dateColumn;


    final FileService service;

    private TreeItem<ModelFile> selection;

    /**
     * Constructs the controller from a service
     *
     * @param service The service
     */
    public FileViewController(FileService service) {
        this.service = service;

        // We start the load on a different thread
        new Thread(() -> {
            service.authenticate();
            TreeItem<ModelFile> rootItem = FileTreeItem.getRoot(service, this::processFileEvents);

            // Hide the fake root node on the local service
            if (service.getServiceName().equals(LocalService.SERVICE_NAME)) {
                fileTree.setShowRoot(false); // Hide the fake root node
            }

            // loadTree needs to run on the JFX thread to modify the controls
            Platform.runLater(() -> loadTree(rootItem));
        }).start();
    }

    private void processFileEvents(TreeItem<ModelFile> item, ModelFile.Event event) {
        if (event == Event.FILE_UPDATED) {
            // If we updated the file, we need to find it again
            item = findItem(item.getValue().getPath());
        }

        select(item);
        onSelectionChanged(item);
    }

    private void onSelectionChanged(TreeItem<ModelFile> selection) {
        if (selection != null)
            this.selection = selection;

        service.setCurrentDir(this.selection.getValue());

        this.selection.getChildren(); // Force update the children on the TreeItem
        fileTable.getItems().setAll(this.selection.getValue().getChildren());
    }

    void select(TreeItem<ModelFile> item) {
        item.setExpanded(true);
        fileTree.getSelectionModel().select(item);
    }

    void scrollTo(TreeItem<ModelFile> item) {
        int row = fileTree.getRow(item);
        // Leave a bit of space on the top if possible
        row = Math.max(row - 2, 0);

        fileTree.scrollTo(row);
    }

    TreeItem<ModelFile> findItem(String target) {
        return findItem(fileTree.getRoot(), target);
    }

    private TreeItem<ModelFile> findItem(TreeItem<ModelFile> root, String target) {
        if (target.contains(root.getValue().getPath())) {

            for (TreeItem<ModelFile> child : root.getChildren()) {
                if (target.contains(child.getValue().getPath())) {
                    return findItem(child, target);
                }
            }
        }

        return root;
    }

    private void loadTree(TreeItem<ModelFile> rootItem) {
        fileTree.setRoot(rootItem);

        fileTree.setCellFactory(tree -> new DraggableTreeCell(tree, service));
        fileTable.setRowFactory(table -> new DraggableTableRow(table, service));

        ///////
        // On item selected, fill the table
        fileTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            onSelectionChanged(newVal);
        });

        ///////
        // On double click on item on the table, change directory
        fileTable.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                fileTable.getSelectionModel().getSelectedItem().selectFile();
            }
        });

        ////////
        // Values to show on the table
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
        iconColumn.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(new ImageView(ResourceManager.toFXImage(param.getValue().getIcon()))));

        ////////
        // Select the default directory
        TreeItem<ModelFile> home = findItem(service.getDefaultDir().getPath());

        select(home);
        scrollTo(home);
    }
}
