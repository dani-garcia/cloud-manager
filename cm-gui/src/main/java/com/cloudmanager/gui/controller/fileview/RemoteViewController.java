package com.cloudmanager.gui.controller.fileview;

import com.cloudmanager.core.model.ModelFile;
import com.cloudmanager.core.services.FileService;
import javafx.scene.control.TreeItem;

public class RemoteViewController extends AbstractFileViewController {

    public RemoteViewController(FileService service) {
        super(service);
    }

    @Override
    protected void loadComponents(TreeItem<ModelFile> rootItem) {
        // Select the root item
        fileTree.getSelectionModel().select(rootItem);
        fileTree.scrollTo(fileTree.getRow(rootItem));
    }
}
