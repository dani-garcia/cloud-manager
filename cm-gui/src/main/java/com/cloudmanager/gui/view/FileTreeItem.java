package com.cloudmanager.gui.view;

import com.cloudmanager.core.api.service.FileService;
import com.cloudmanager.core.model.ModelFile;
import com.cloudmanager.gui.util.ResourceManager;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

import java.util.function.BiConsumer;

/**
 * Represents a file in the tree. The children are obtained lazily.
 */
public class FileTreeItem extends TreeItem<ModelFile> {

    private FileService service;

    private BiConsumer<FileTreeItem, ModelFile.Event> treeItemListener;
    private boolean forceRefresh = false;
    private boolean childrenSet = false;

    /**
     * Returns the root file of the given service as a FileTreeItem
     *
     * @param service          The service
     * @param treeItemListener A change listener
     * @return The FileTreeItem
     */
    public static FileTreeItem getRoot(FileService service, BiConsumer<FileTreeItem, ModelFile.Event> treeItemListener) {
        FileTreeItem root = new FileTreeItem(service.getRootFile(), service, treeItemListener);
        root.setExpanded(true);

        return root;
    }

    private FileTreeItem(ModelFile file, FileService service, BiConsumer<FileTreeItem, ModelFile.Event> treeItemListener) {
        super(file);
        this.service = service;
        this.treeItemListener = treeItemListener;

        // We set the file icon
        setGraphic(new ImageView(ResourceManager.toFXImage(file.getIcon())));

        // And add the treeItemListener
        addFileListener(file);
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
            getValue().setChildren(service.getChildren(this.getValue()));

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
                    .filter(ModelFile::isFolder) // Only show folders in the tree
                    .map(f -> new FileTreeItem(f, service, treeItemListener))
                    .forEach(super.getChildren()::add);
        }

        return super.getChildren();
    }

    private void addFileListener(ModelFile file) {
        file.addListener((m, e) -> {
                    switch (e) {
                        case FILE_UPDATED:
                            file.getParent().refreshChildren();
                            break; // Refresh the parents children to refresh this file

                        case CHILDREN_UPDATED:
                            forceRefresh = true;
                            break; // Refresh children

                        case FILE_SELECTED:
                            break; // Don't do anything, only call the listener
                    }

                    Platform.runLater(() -> treeItemListener.accept(this, e));
                }
        );
    }
}
