package com.cloudmanager.gui.controller.fileview;

import com.cloudmanager.core.model.ModelFile;
import com.cloudmanager.core.services.FileService;
import com.cloudmanager.core.util.Util;
import javafx.scene.control.TreeItem;

public class LocalViewController extends AbstractFileViewController {

    public LocalViewController(FileService service) {
        super(service);
    }

    @Override
    protected void loadComponents(TreeItem<ModelFile> rootItem) {
        fileTree.setShowRoot(false); // Hide the fake root node

        /////////
        // Select the Home directory by default
        // And scroll to it
        scrollHome(rootItem);
    }

    private void scrollHome(TreeItem<ModelFile> rootItem) {
        final String home = System.getProperty("user.home");
        TreeItem<ModelFile> homeItem = findFolder(rootItem, home);

        scrollTo(homeItem);
    }

    private TreeItem<ModelFile> findFolder(TreeItem<ModelFile> root, String folder) {
        return findFolder(root, Util.splitDir(folder), 0);
    }


    private TreeItem<ModelFile> findFolder(TreeItem<ModelFile> root, String[] dirs, int depth) {
        if (depth == dirs.length)
            return root;

        for (TreeItem<ModelFile> child : root.getChildren()) {
            String actual = Util.splitDir(child.getValue().getPath())[depth];
            String desired = dirs[depth];

            if (actual.equals(desired)) {
                return findFolder(child, dirs, depth + 1);
            }
        }

        return root;
    }
}
