package com.cloudmanager.core.model;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Represents a file or a folder, whether local or remote
 */
public class ModelFile implements Comparable<ModelFile> {
    private static final ImageIcon FILE_ICON = new ImageIcon(ModelFile.class.getResource("/icons/document.png"));
    private static final ImageIcon FOLDER_ICON = new ImageIcon(ModelFile.class.getResource("/icons/folder.png"));

    /**
     * Type of the file, it can be either a file or a folder
     */
    public enum Type implements Comparable<Type> {
        FOLDER,
        FILE, // The order is important, we want folders to show before files, so we put them last
    }

    /**
     * Events that can be triggered:
     * <p>
     * {@link #CHILDREN_UPDATED}: The children of the file have been updated and need to be reloaded
     * <p>
     * {@link #FILE_UPDATED}: The file has been modified and needs to be reloaded
     * <p>
     * {@link #FILE_SELECTED}: The file has been selected, and the interface needs updating
     */
    public enum Event {
        CHILDREN_UPDATED,
        FILE_UPDATED,
        FILE_SELECTED
    }

    private final String id, name, path;
    private final Type type;
    private final Long size;
    private final Date lastModified;
    private final ImageIcon icon;

    private final ModelFile parent;
    private final List<ModelFile> children = new ArrayList<>();
    private boolean childrenSet = false;

    private BiConsumer<ModelFile, Event> listener;

    /**
     * Constructs the file with the following parameters
     *
     * @param id           The file's unique identifier, it can be the complete path or any other identifier
     * @param name         The name
     * @param path         The path
     * @param type         The type
     * @param size         The size
     * @param lastModified The last modified date
     * @param parent       The parent file
     */
    public ModelFile(String id, String name, String path, Type type, Long size, Date lastModified, ModelFile parent) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.type = type;
        this.size = size;
        this.lastModified = lastModified;
        this.parent = parent;

        this.icon = type.equals(Type.FILE) ? FILE_ICON : FOLDER_ICON;
    }

    /**
     * Constructs the file with the following parameters. Used for the local service
     *
     * @param id           The file's unique identifier, it can be the complete path or any other identifier
     * @param name         The name
     * @param path         The path
     * @param type         The type
     * @param size         The size
     * @param lastModified The last modified date
     * @param parent       The parent file
     * @param localFile    The local file, used to extract the icon
     */
    public ModelFile(String id, String name, String path, Type type, Long size, Date lastModified, ModelFile parent, File localFile) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.type = type;
        this.size = size;
        this.lastModified = lastModified;
        this.parent = parent;

        this.icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(localFile);
    }

    //// Getters & Setters

    /**
     * Returns the file id.
     *
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the file name.
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the file path.
     *
     * @return The path
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the file type.
     *
     * @return The type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the file size.
     *
     * @return The size
     */
    public Long getSize() {
        return size;
    }

    /**
     * Returns the file last modified date.
     *
     * @return The last modified
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * Returns the file parent.
     *
     * @return The parent
     */
    public ModelFile getParent() {
        return parent;
    }

    /**
     * Returns the file icon.
     *
     * @return The icon
     */
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * Returns the file's children.
     *
     * @return The file's children
     */
    public List<ModelFile> getChildren() {
        return children;
    }

    /**
     * Returns if the children ara already set.
     *
     * @return True if the children are set, false otherwise
     */
    public boolean areChildrenSet() {
        return childrenSet;
    }

    /**
     * Sets the children.
     *
     * @param collection The new children
     */
    public void setChildren(Collection<ModelFile> collection) {
        if (collection == null) return;

        children.clear();
        children.addAll(collection);
        childrenSet = true;
    }

    /**
     * Returns the distance from the root file.
     *
     * @return the file depth
     */
    public int getFileDepth() {
        ModelFile parent = this.parent;
        int counter = 1;

        while (parent != null) {
            counter++;
            parent = parent.parent;
        }

        return counter;
    }

    /**
     * Returns the common ancestor between the current file and the parameter.
     *
     * @param thatFile The other file
     * @return The common ancestor
     */
    public ModelFile getCommonAncestor(ModelFile thatFile) {
        ModelFile thisFile = this;

        // Put both files at the same depth
        while (thisFile.getFileDepth() > thatFile.getFileDepth()) {
            thisFile = thisFile.getParent();
        }

        while (thatFile.getFileDepth() > thisFile.getFileDepth()) {
            thatFile = thatFile.getParent();
        }

        // Find a common ancestor
        while (thisFile != null && !thisFile.equals(thatFile)) {
            thisFile = thisFile.getParent();
            thatFile = thatFile.getParent();
        }

        return thisFile;
    }

    /**
     * Returns true if the object is a file.
     *
     * @return the boolean
     */
    public boolean isFile() {
        return type == Type.FILE;
    }

    /**
     * Returns true if the object is a folder.
     *
     * @return the boolean
     */
    public boolean isFolder() {
        return type == Type.FOLDER;
    }

    /**
     * Notifies any listeners to select the file.
     */
    public void selectFile() {
        notify(Event.FILE_SELECTED);
    }

    /**
     * Notifies any listeners to refresh the children.
     */
    public void refreshChildren() {
        notify(Event.CHILDREN_UPDATED);
    }

    /**
     * Notifies any listeners to refresh the file.
     */
    public void refreshFile() {
        notify(Event.FILE_UPDATED);
    }

    /**
     * Add file listener. The listener will be called any time an event is produced for this file
     *
     * @param otherListener The listener to add
     */
    public void addListener(BiConsumer<ModelFile, Event> otherListener) {
        if (listener == null)
            listener = otherListener;
        else
            listener = listener.andThen(otherListener);
    }

    private void notify(Event event) {
        if (listener != null)
            listener.accept(this, event);
    }

    @Override
    public String toString() {
        return "{ id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", type=" + type +
                ", size=" + size +
                ", lastModified=" + lastModified +
                '}';
    }

    @Override
    public int compareTo(ModelFile that) {
        // Compare type
        int typeComp = this.getType().compareTo(that.getType());

        if (typeComp == 0) // If same type, compare name
            return this.getName().toLowerCase().compareTo(that.getName().toLowerCase());

        return typeComp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModelFile modelFile = (ModelFile) o;

        return id.equals(modelFile.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
