package com.cloudmanager.gui.view;

import com.cloudmanager.core.api.service.FileService;
import com.cloudmanager.core.managers.ServiceManager;
import com.cloudmanager.core.managers.TransferManager;
import com.cloudmanager.core.model.ModelFile;
import com.cloudmanager.gui.util.ResourceManager;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
        node.setOnDragOver(getOnDragOver(serviceId, file));
        node.setOnDragDropped(getOnDragDropped(serviceId, file));
    }

    private EventHandler<? super MouseEvent> getOnDragDetected(Node node, String serviceId, Supplier<ModelFile> draggedFile) {
        return event -> {
            ModelFile file = draggedFile.get();
            if (file == null) {
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

    private EventHandler<? super DragEvent> getOnDragOver(String targetServiceId, Supplier<ModelFile> targetFile) {
        return dragEvent -> {
            String fileId = (String) dragEvent.getDragboard().getContent(draggableFile);
            String draggedServiceId = (String) dragEvent.getDragboard().getContent(draggableServiceId);

            if (fileId != null) {
                ModelFile draggedFile = draggedFiles.get(fileId);

                // TODO Suppport folder transfers
                if (draggedFile.isFolder() && !draggedServiceId.equals(targetServiceId)) {
                    // Currently we don't support folder transfers
                    dragEvent.consume();
                    return;
                }

                // If the file is dropped on an empty space on the table
                if (targetFile.get() == null) {
                    if (dragEvent.getSource() instanceof DraggableTableRow) {
                        dragEvent.acceptTransferModes(TransferMode.MOVE);
                    }

                    // If it's dropped on a folder (and not on itself)
                } else if (!targetFile.get().equals(draggedFile)
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
            String draggedServiceId = (String) dragEvent.getDragboard().getContent(draggableServiceId);

            if (fileId != null) {
                // Get both services and initiate a transfer
                ModelFile draggedFile = draggedFiles.get(fileId);

                FileService draggedService = ServiceManager.getInstance().getServiceSettings(draggedServiceId).getService();
                FileService targetService = ServiceManager.getInstance().getServiceSettings(targetServiceId).getService();

                boolean result = TransferManager.get().transferFile(draggedService, draggedFile, targetService, targetFolder.get());

                if (!result) {
                    new Alert(AlertType.WARNING, ResourceManager.getString("error_moving_file")).show();
                }
            }

            draggedFiles.remove(fileId);

            dragEvent.consume();
        };
    }
}
