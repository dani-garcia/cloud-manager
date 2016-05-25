package com.cloudmanager.gui.view;

import com.cloudmanager.core.api.service.FileService;
import com.cloudmanager.core.model.ModelFile;
import com.cloudmanager.gui.util.ResourceManager;
import javafx.beans.binding.Bindings;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;

import java.util.Optional;

/**
 * Represents a cell in the file table view.
 */
public class DraggableTableRow extends TableRow<ModelFile> {

    /**
     * Construct a cell from the table and the service
     *
     * @param table   The parent table
     * @param service The file service
     */
    public DraggableTableRow(TableView<ModelFile> table, FileService service) {
        DraggableFileHandler.getInstance().setOnDragEvents(this, service.getInstanceId(), this::getItem);

        // Create the context menu
        createContextMenu(service);


        ///////
        // On double click on item on the row, change directory
        setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)
                    && e.getClickCount() == 2
                    && !isEmpty()) {

                table.getSelectionModel().getSelectedItem().selectFile();
            }
        });
    }

    private void createContextMenu(FileService service) {
        /******************
         * Refresh Folder
         ******************/
        MenuItem refresh = new MenuItem(ResourceManager.getString("refresh_folder"));
        refresh.setOnAction(e -> {
            if (isEmpty()) {
                service.getCurrentDir().refreshFile();
            } else {
                getItem().refreshFile();
            }
        });

        // Second copy
        MenuItem refresh2 = new MenuItem(ResourceManager.getString("refresh_folder"));
        refresh2.setOnAction(refresh.getOnAction());

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

            ModelFile location = isEmpty() ? service.getCurrentDir() : getItem();
            boolean result = service.createFolder(location, choice.get());

            String msg = result ? "folder_created_success" : "folder_created_error";
            new Alert(AlertType.INFORMATION, ResourceManager.getString(msg)).show();

            location.refreshFile();
        });

        /******************
         * Rename
         ******************/
        MenuItem rename = new MenuItem(ResourceManager.getString("rename"));
        rename.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle(ResourceManager.getString("rename"));
            dialog.setHeaderText(ResourceManager.getString("input_name"));

            Optional<String> choice = dialog.showAndWait();
            if (!choice.isPresent()) {
                new Alert(AlertType.INFORMATION, ResourceManager.getString("name_cant_be_empty")).show();

            } else {
                boolean result = service.renameFile(getItem(), choice.get());

                String msg = result ? "rename_success" : "rename_error";
                new Alert(AlertType.INFORMATION, ResourceManager.getString(msg)).show();

                getItem().refreshFile();
            }
        });

        /******************
         * Remove
         ******************/
        MenuItem remove = new MenuItem(ResourceManager.getString("remove"));
        remove.setOnAction(e -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle(ResourceManager.getString("remove"));
            alert.setContentText(ResourceManager.getString("remove_confirm"));

            alert.showAndWait().ifPresent(b -> {
                if (b == ButtonType.OK) {
                    boolean result = service.deleteFile(getItem());

                    String msg = result ? "remove_success" : "remove_error";
                    new Alert(AlertType.INFORMATION, ResourceManager.getString(msg)).show();

                    getItem().refreshFile();
                }
            });
        });

        ContextMenu emptyRowContextMenu = new ContextMenu(refresh, create);
        ContextMenu itemContextMenu = new ContextMenu(refresh2, rename, remove);

        // Set context menu on row, but use a binding to make it only show for non-empty rows:
        contextMenuProperty().bind(
                Bindings.when(emptyProperty())
                        .then(emptyRowContextMenu)
                        .otherwise(itemContextMenu));
    }
}
