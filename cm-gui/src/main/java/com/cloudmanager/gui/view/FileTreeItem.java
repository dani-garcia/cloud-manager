package com.cloudmanager.gui.view;

import com.cloudmanager.core.model.ModelFile;
import com.cloudmanager.core.services.FileService;
import com.cloudmanager.gui.util.ResourceManager;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

import java.util.function.BiConsumer;

public class FileTreeItem extends TreeItem<ModelFile> {

    private FileService fs;

    private BiConsumer<TreeItem, ModelFile.Event> treeItemListener;
    private boolean forceRefresh = false;
    private boolean childrenSet = false;

    public static FileTreeItem getRoot(FileService fs, BiConsumer<TreeItem, ModelFile.Event> treeItemListener) {
        FileTreeItem root = new FileTreeItem(fs.getRootFile(), fs, treeItemListener);
        root.setExpanded(true);

        return root;
    }

    private FileTreeItem(ModelFile f, FileService fs, BiConsumer<TreeItem, ModelFile.Event> treeItemListener) {
        super(f);
        this.fs = fs;
        this.treeItemListener = treeItemListener;

        // We set the file icon
        setGraphic(new ImageView(ResourceManager.toFXImage(f.getIcon())));

        // And add the treeItemListener
        addFileListener(f);
    }

    @Override
    public boolean isLeaf() {
        // It's a leaf if it doesn't have children directories
        return getChildren().filtered(f -> f.getValue().getType().equals(ModelFile.Type.FOLDER)).isEmpty();
    }

    @Override
    public ObservableList<TreeItem<ModelFile>> getChildren() {
        // If we haven't added the children to the model
        if (!getValue().areChildrenSet() || forceRefresh) {
            // We get them from the service and add them to the model
            getValue().setChildren(fs.getChildren(this.getValue()));

            // Reload the tree items
            forceRefresh = false;
            childrenSet = false;
        }

        // If we haven't added the children to the tree yet
        if (!childrenSet) {
            childrenSet = true;

            super.getChildren().clear();

            // We take the children from the model and add it to the tree
            getValue().getChildren().stream()
                    .filter(f -> f.getType().equals(ModelFile.Type.FOLDER)) // Only show folders in the tree
                    .map(f -> new FileTreeItem(f, fs, treeItemListener))
                    .forEach(super.getChildren()::add);
        }

        return super.getChildren();
    }

    private void addFileListener(ModelFile file) {
        file.addListener((m, e) -> {
                    switch (e) {
                        case FILE_UPDATED:
                            file.getParent().refreshChildren();
                            break;

                        case CHILDREN_UPDATED:
                            // Refresh children
                            forceRefresh = true;
                            // Fall through

                        case FILE_SELECTED:
                            // Update the interface
                            Platform.runLater(() -> treeItemListener.accept(this, e));
                            break;
                    }
                }
        );
    }
}
