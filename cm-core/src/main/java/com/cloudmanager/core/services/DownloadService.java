package com.cloudmanager.core.services;

import com.cloudmanager.core.model.ModelFile;
import com.cloudmanager.core.transfers.FileTransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class DownloadService {
    private static final DownloadService instance = new DownloadService();

    public static DownloadService get() {
        return instance;
    }

    private DownloadService() { }

    private BiConsumer<ModelFile, Double> listener;

    private List<Thread> transfersInProgress = Collections.synchronizedList(new ArrayList<>());

    public void transferFile(FileService origin, ModelFile file, FileService target, ModelFile targetFolder) {
        // TODO Error si el archivo ya existe
        // TODO Si son el mismo servicio, comprobar que no intentamos mover un archivo dentro de si mismo o similar
        // TODO Si estamos moviendo una carpeta, copiar recursivamente su contenido

        // If we don't get a target, assume the current directory
        if (targetFolder == null)
            targetFolder = target.getCurrentDir();

        target.setCurrentDir(targetFolder);

        if (origin.getAccountId().equals(target.getAccountId())) {
            moveOnSameService(origin, file, targetFolder);

        } else {
            transferToAnotherService(origin, file, target, targetFolder);
        }
    }

    private void moveOnSameService(FileService service, ModelFile file, ModelFile targetFolder) {
        service.moveFile(file, targetFolder);

        ModelFile ancestor = file.getCommonAncestor(targetFolder);
        ancestor.selectFile();

        targetFolder.refreshChildren();
        file.refreshFile();
        targetFolder.selectFile();
    }

    private void transferToAnotherService(FileService origin, ModelFile file, FileService target, ModelFile targetFolder) {
        Thread thread = new Thread(() -> {
            // Create the file trasfer, add the listener and notify the start of the download
            FileTransfer transfer = origin.sendFile(file);
            transfer.addProgressListener(listener);
            listener.accept(file, 0d);

            // Start receiving the file in the background
            target.receiveFile(transfer);

            // Refresh the target directory once complete
            targetFolder.refreshChildren();

            // Remove the thread from the transfer in progress
            transfersInProgress.remove(Thread.currentThread());
        });

        // Add the transfer in progress
        transfersInProgress.add(thread);

        thread.setDaemon(true);
        thread.start();
    }

    public int getTransfersInProgress() {
        return transfersInProgress.size();
    }

    public void addProgressListener(BiConsumer<ModelFile, Double> otherListener) {
        if (listener == null)
            listener = otherListener;
        else
            listener = listener.andThen(otherListener);
    }
}
