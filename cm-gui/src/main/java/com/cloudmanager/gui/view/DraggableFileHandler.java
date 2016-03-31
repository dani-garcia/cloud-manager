package com.cloudmanager.gui.view;

import com.cloudmanager.core.config.ServiceManager;
import com.cloudmanager.core.model.ModelFile;
import com.cloudmanager.core.services.DownloadManager;
import com.cloudmanager.core.services.FileService;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DraggableFileHandler {
    private static DataFormat draggableFile = new DataFormat("com.cloudmanager.dnd.File");
    private static DataFormat draggableAccountId = new DataFormat("com.cloudmanager.dnd.AccountId");

    private static DraggableFileHandler instance = new DraggableFileHandler();

    public static DraggableFileHandler getInstance() {
        return instance;
    }

    private DraggableFileHandler() { }

    private Map<String, ModelFile> draggedFiles = new HashMap<>();

    void setOnDragEvents(Node node, String accountId, Supplier<ModelFile> file) {
        node.setOnDragDetected(getOnDragDetected(node, accountId, file));
        node.setOnDragOver(getOnDragOver(file));
        node.setOnDragDropped(getOnDragDropped(accountId, file));
    }

    private EventHandler<? super MouseEvent> getOnDragDetected(Node node, String accountId, Supplier<ModelFile> draggedFile) {
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
            content.put(draggableAccountId, accountId);

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
                        && targetFile.get().getType().equals(ModelFile.Type.FOLDER)) {
                    dragEvent.acceptTransferModes(TransferMode.MOVE);
                }
            }
            dragEvent.consume();
        };
    }

    private EventHandler<? super DragEvent> getOnDragDropped(String targetAccountId, Supplier<ModelFile> targetFolder) {
        return dragEvent -> {
            String fileId = (String) dragEvent.getDragboard().getContent(draggableFile);

            if (fileId != null) {
                String draggedAccountId = (String) dragEvent.getDragboard().getContent(draggableAccountId);

                FileService draggedService = ServiceManager.getInstance().getService(draggedAccountId);
                FileService targetService = ServiceManager.getInstance().getService(targetAccountId);

                DownloadManager.get().transferFile(draggedService, draggedFiles.get(fileId), targetService, targetFolder.get());
            }

            draggedFiles.remove(fileId);

            dragEvent.consume();
        };
    }
}
