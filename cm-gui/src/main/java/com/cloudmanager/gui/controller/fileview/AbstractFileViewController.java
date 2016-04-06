package com.cloudmanager.gui.controller.fileview;

import com.cloudmanager.core.model.ModelFile;
import com.cloudmanager.core.services.FileService;
import com.cloudmanager.core.services.local.LocalService;
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

public abstract class AbstractFileViewController {

    public static AbstractFileViewController getController(FileService service) {
        if (service.getServiceName().equals(LocalService.SERVICE_NAME))
            return new LocalViewController(service);
        else
            return new RemoteViewController(service);
    }

    @FXML
    protected TreeView<ModelFile> fileTree;

    @FXML
    protected TableView<ModelFile> fileTable;

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

    AbstractFileViewController(FileService service) {
        this.service = service;

        // We start the load on a different thread
        new Thread(() -> {
            service.login();
            TreeItem<ModelFile> rootItem = FileTreeItem.getRoot(service, this::processFileEvents);

            // loadTree needs to run on the JFX thread to modify the controls
            Platform.runLater(() -> loadTree(rootItem));
        }).start();
    }

    protected abstract void loadComponents(TreeItem<ModelFile> rootItem);

    private void processFileEvents(TreeItem<ModelFile> item, ModelFile.Event event) {
        switch (event) {
            case FILE_UPDATED:
                fileTree.getSelectionModel().select(item);
                break;

            case CHILDREN_UPDATED:
                reloadTableSelection(item);
                break;

            case FILE_SELECTED:
                scrollTo(item);
                break;
        }
    }

    private void reloadTableSelection(TreeItem<ModelFile> selection) {
        if (selection != null)
            this.selection = selection;

        service.setCurrentDir(this.selection.getValue());

        this.selection.getChildren(); // Force update the children on the TreeItem
        fileTable.getItems().setAll(this.selection.getValue().getChildren());
    }

    void scrollTo(TreeItem<ModelFile> item) {
        item.setExpanded(true);
        fileTree.getSelectionModel().select(item);

        int row = fileTree.getRow(item);
        // Leave a bit of space on the top if possible
        row = Math.max(row - 2, 0);

        fileTree.scrollTo(row);
    }

    private void loadTree(TreeItem<ModelFile> rootItem) {
        fileTree.setRoot(rootItem);

        fileTree.setCellFactory(tree -> new DraggableTreeCell(tree, service));
        fileTable.setRowFactory(table -> new DraggableTableRow(table, service));

        ///////
        // On item selected, fill the table
        fileTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            reloadTableSelection(newVal);
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
        // Load extra components of the children classes
        loadComponents(rootItem);
    }
}
