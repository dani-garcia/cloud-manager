package com.cloudmanager.core.model;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;

public class ModelFile implements Comparable<ModelFile> {
    private static final ImageIcon FILE_ICON = new ImageIcon(ModelFile.class.getResource("/icons/document.png"));
    private static final ImageIcon FOLDER_ICON = new ImageIcon(ModelFile.class.getResource("/icons/folder.png"));

    public enum Type implements Comparable<Type> {
        FOLDER,
        FILE, // The order is important, we want folders to show before files, so we put them last
    }

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

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public Type getType() {
        return type;
    }

    public Long getSize() {
        return size;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public ModelFile getParent() {
        return parent;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    // Children
    public List<ModelFile> getChildren() {
        return children;
    }

    public boolean areChildrenSet() {
        return childrenSet;
    }

    public void setChildren(Collection<ModelFile> collection) {
        if (collection == null) return;

        children.clear();
        children.addAll(collection);
        childrenSet = true;
    }

    public int getFileDepth() {
        ModelFile parent = this.parent;
        int counter = 1;

        while (parent != null) {
            counter++;
            parent = parent.parent;
        }

        return counter;
    }

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

    public boolean isFile() {
        return type == Type.FILE;
    }

    public boolean isFolder() {
        return type == Type.FOLDER;
    }

    public void selectFile() {
        notify(Event.FILE_SELECTED);
    }

    public void refreshChildren() {
        notify(Event.CHILDREN_UPDATED);
    }

    public void refreshFile() {
        notify(Event.FILE_UPDATED);
    }

    // Refresh listener
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
