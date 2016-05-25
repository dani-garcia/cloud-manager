package com.cloudmanager.gui.view;

import com.cloudmanager.core.api.service.FileService;
import com.cloudmanager.core.model.ModelFile;
import com.cloudmanager.gui.util.ResourceManager;
import javafx.beans.binding.Bindings;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.util.Optional;

/**
 * Represents a cell in the file tree view.
 */
public class DraggableTreeCell extends TreeCell<ModelFile> {

    /**
     * Construct a cell from the tree and the service
     *
     * @param tree    The parent tree
     * @param service The file service
     */
    public DraggableTreeCell(final TreeView<ModelFile> tree, FileService service) {
        DraggableFileHandler.getInstance().setOnDragEvents(this, service.getInstanceId(), this::getItem);

        // Create the context menu
        createContextMenu(service);
    }

    @Override
    protected void updateItem(ModelFile file, boolean empty) {
        super.updateItem(file, empty);

        if (!empty) {
            setText(file.getName());
            setGraphic(getTreeItem().getGraphic());

        } else {
            setText(null);
            setGraphic(null);
        }
    }

    private void createContextMenu(FileService service) {
        /******************
         * Refresh Folder
         ******************/
        MenuItem refresh = new MenuItem(ResourceManager.getString("refresh_folder"));
        refresh.setOnAction(e -> getItem().refreshFile());


        /******************
         * Create Folder
         ******************/
        MenuItem create = new MenuItem(ResourceManager.getString("create_folder"));
        create.setOnAction(e -> {

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle(ResourceManager.getString("create_folder"));
            dialog.setHeaderText(ResourceManager.getString("input_name"));

            Optional<String> choice = dialog.showAndWait();
            if (!choice.isPresent()) {
                new Alert(AlertType.INFORMATION, ResourceManager.getString("name_cant_be_empty")).show();
                return;
            }

            boolean result = service.createFolder(getItem(), choice.get());

            String msg = result ? "folder_created_success" : "folder_created_error";
            new Alert(AlertType.INFORMATION, ResourceManager.getString(msg)).show();

            getItem().refreshFile();
        });

        /******************
         * Rename Folder
         ******************/
        MenuItem rename = new MenuItem(ResourceManager.getString("rename"));
        rename.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle(ResourceManager.getString("rename"));
            dialog.setHeaderText(ResourceManager.getString("input_name"));

            Optional<String> choice = dialog.showAndWait();
            if (!choice.isPresent()) {
                new Alert(AlertType.INFORMATION, ResourceManager.getString("name_cant_be_empty")).show();
                return;
            }

            boolean result = service.renameFile(getItem(), choice.get());

            String msg = result ? "rename_success" : "rename_error";
            new Alert(AlertType.INFORMATION, ResourceManager.getString(msg)).show();

            getItem().refreshFile();
        });


        /******************
         * Remove Folder
         ******************/
        MenuItem remove = new MenuItem(ResourceManager.getString("remove"));
        remove.setOnAction(e -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle(ResourceManager.getString("remove"));
            alert.setContentText(ResourceManager.getString("remove_confirm"));

            Optional<ButtonType> choice = alert.showAndWait();
            if (choice.isPresent() && choice.get() == ButtonType.OK) {
                boolean result = service.deleteFile(getItem());

                String msg = result ? "remove_success" : "remove_error";
                new Alert(AlertType.INFORMATION, ResourceManager.getString(msg)).show();

                getItem().refreshFile();
            }
        });

        ContextMenu contextMenu = new ContextMenu(refresh, create, rename, remove);

        // Set context menu on row, but use a binding to make it only show for non-empty rows:
        contextMenuProperty().bind(
                Bindings.when(emptyProperty())
                        .then((ContextMenu) null)
                        .otherwise(contextMenu)
        );
    }
}
