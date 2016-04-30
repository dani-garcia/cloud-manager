package com.cloudmanager.gui.controller.fileview;

import com.cloudmanager.core.model.ModelFile;
import com.cloudmanager.core.services.FileService;
import javafx.scene.control.TreeItem;

public class LocalViewController extends AbstractFileViewController {

    public LocalViewController(FileService service) {
        super(service);
    }

    @Override
    protected void loadComponents(TreeItem<ModelFile> rootItem) {
        fileTree.setShowRoot(false); // Hide the fake root node

        // Select the Home directory by default
        // And scroll to it
        scrollHome();
    }

    private void scrollHome() {
        final String home = System.getProperty("user.home");
        TreeItem<ModelFile> homeItem = findItem(home);

        select(homeItem);
        scrollTo(homeItem);
    }
}
