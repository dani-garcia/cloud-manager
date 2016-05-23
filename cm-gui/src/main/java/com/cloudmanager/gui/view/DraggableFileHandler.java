package com.cloudmanager.gui.view;

import com.cloudmanager.core.api.service.FileService;
import com.cloudmanager.core.config.ServiceManager;
import com.cloudmanager.core.model.ModelFile;
import com.cloudmanager.core.service.TransferService;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Handles the files being dragged
 */
public class DraggableFileHandler {
    private static DataFormat draggableFile = new DataFormat("com.cloudmanager.dnd.File");
    private static DataFormat draggableServiceId = new DataFormat("com.cloudmanager.dnd.ServiceId");

    private static DraggableFileHandler instance = new DraggableFileHandler();

    /**
     * Returns the Handle's instance
     *
     * @return The instance
     */
    public static DraggableFileHandler getInstance() {
        return instance;
    }

    private DraggableFileHandler() { }

    private Map<String, ModelFile> draggedFiles = new HashMap<>();

    /**
     * Add the onDrag events to the given node and for the given service.
     *
     * @param node      The node to add the drag events to
     * @param serviceId The service being represented in the node
     * @param file      Supplies the file being dragged / dropped
     */
    void setOnDragEvents(Node node, String serviceId, Supplier<ModelFile> file) {
        node.setOnDragDetected(getOnDragDetected(node, serviceId, file));
        node.setOnDragOver(getOnDragOver(file));
        node.setOnDragDropped(getOnDragDropped(serviceId, file));
    }

    private EventHandler<? super MouseEvent> getOnDragDetected(Node node, String serviceId, Supplier<ModelFile> draggedFile) {
        return event -> {
            ModelFile file = draggedFile.get();
            if (file == null || !file.isFile()) {
                return;
            }

            // Store a reference to the file for later
            draggedFiles.put(file.getId(), file);

            // Save the data on the clipboard
            Dragboard dragBoard = node.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            content.put(draggableFile, file.getId());
            content.put(draggableServiceId, serviceId);

            dragBoard.setContent(content);

            event.consume();
        };
    }

    private EventHandler<? super DragEvent> getOnDragOver(Supplier<ModelFile> targetFile) {
        return dragEvent -> {
            String fileId = (String) dragEvent.getDragboard().getContent(draggableFile);

            if (fileId != null) {
                // If the file is dropped on an empty space on the table
                if (targetFile.get() == null) {
                    if (dragEvent.getSource() instanceof DraggableTableRow) { // TODO Cambiar esto
                        dragEvent.acceptTransferModes(TransferMode.MOVE);
                    }

                    // If it's dropped on a folder (and not on itself)
                } else if (!targetFile.get().equals(draggedFiles.get(fileId))
                        && targetFile.get().isFolder()) {
                    dragEvent.acceptTransferModes(TransferMode.MOVE);
                }
            }
            dragEvent.consume();
        };
    }

    private EventHandler<? super DragEvent> getOnDragDropped(String targetServiceId, Supplier<ModelFile> targetFolder) {
        return dragEvent -> {
            String fileId = (String) dragEvent.getDragboard().getContent(draggableFile);

            if (fileId != null) {
                // Get both services and initiate a transfer

                String draggedServiceId = (String) dragEvent.getDragboard().getContent(draggableServiceId);

                FileService draggedService = ServiceManager.getInstance().getServiceSettings(draggedServiceId).getService();
                FileService targetService = ServiceManager.getInstance().getServiceSettings(targetServiceId).getService();

                TransferService.get().transferFile(draggedService, draggedFiles.get(fileId), targetService, targetFolder.get());
            }

            draggedFiles.remove(fileId);

            dragEvent.consume();
        };
    }
}
