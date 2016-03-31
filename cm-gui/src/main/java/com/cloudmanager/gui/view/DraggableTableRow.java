package com.cloudmanager.gui.view;

import com.cloudmanager.core.model.ModelFile;
import com.cloudmanager.core.services.FileService;
import javafx.beans.binding.Bindings;
import javafx.scene.control.*;

public class DraggableTableRow extends TableRow<ModelFile> {

    public DraggableTableRow(TableView<ModelFile> table, FileService service) {
        DraggableFileHandler.getInstance().setOnDragEvents(this, service.getAccountId(), this::getItem);

        createContextMenu(service);
    }

    private void createContextMenu(FileService service) {
        MenuItem removeMenuItem = new MenuItem("Remove");
        removeMenuItem.setOnAction(e -> {
            service.deleteFile(getItem());
            getItem().refreshFile();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(getItem() + " deleted.");
            alert.show();
        });

        ContextMenu contextMenu = new ContextMenu(removeMenuItem);

        // Set context menu on row, but use a binding to make it only show for non-empty rows:
        contextMenuProperty().bind(
                Bindings.when(emptyProperty())
                        .then((ContextMenu) null)
                        .otherwise(contextMenu)
        );
    }
}
