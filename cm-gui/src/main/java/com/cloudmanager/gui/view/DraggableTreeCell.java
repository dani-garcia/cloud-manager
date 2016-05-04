package com.cloudmanager.gui.view;

import com.cloudmanager.core.model.ModelFile;
import com.cloudmanager.core.services.FileService;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;

public class DraggableTreeCell extends TreeCell<ModelFile> {

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

    public DraggableTreeCell(final TreeView<ModelFile> tree, FileService service) {
        DraggableFileHandler.getInstance().setOnDragEvents(this, service.getRepoId(), this::getItem);

        createContextMenu();
    }

    private void createContextMenu() {
        MenuItem refreshChildren = new MenuItem("Refresh Children");
        refreshChildren.setOnAction(e -> {
            getItem().refreshChildren();
        });

        MenuItem refreshFolder = new MenuItem("Refresh file");
        refreshFolder.setOnAction(e -> {
            getItem().refreshFile();
        });

        ContextMenu contextMenu = new ContextMenu(refreshFolder, refreshChildren);

        // Set context menu on row, but use a binding to make it only show for non-empty rows:
        contextMenuProperty().bind(
                Bindings.when(emptyProperty())
                        .then((ContextMenu) null)
                        .otherwise(contextMenu)
        );
    }
}
