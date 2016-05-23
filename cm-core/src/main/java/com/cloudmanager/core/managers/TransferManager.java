package com.cloudmanager.core.managers;

import com.cloudmanager.core.api.service.FileService;
import com.cloudmanager.core.model.FileTransfer;
import com.cloudmanager.core.model.ModelFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Manages the transfers in progress
 */
public class TransferManager {
    private static final TransferManager instance = new TransferManager();

    /**
     * Returns the trasfer service instance.
     *
     * @return The trasfer service instance
     */
    public static TransferManager get() {
        return instance;
    }

    private TransferManager() { }

    private BiConsumer<ModelFile, Double> listener;

    private List<Thread> transfersInProgress = Collections.synchronizedList(new ArrayList<>());

    /**
     * Transfer a file. If both services are the same, the file is moved,
     * whereas if the services are different, the files are copied.
     *
     * @param origin       The service of the original file
     * @param file         The file to transfer
     * @param target       The service of the target file
     * @param targetFolder The target file
     */
    public void transferFile(FileService origin, ModelFile file, FileService target, ModelFile targetFolder) {
        // TODO Error si el archivo ya existe
        // TODO Si son el mismo servicio, comprobar que no intentamos mover un archivo dentro de si mismo o similar
        // TODO Si estamos moviendo una carpeta, copiar recursivamente su contenido

        // If we don't get a target, assume the current directory
        if (targetFolder == null)
            targetFolder = target.getCurrentDir();

        target.setCurrentDir(targetFolder);

        // If both are the same services, we move the file
        if (origin.getInstanceId().equals(target.getInstanceId())) {
            moveOnSameService(origin, file, targetFolder);

        } else {
            transferToAnotherService(origin, file, target, targetFolder);
        }
    }

    private void moveOnSameService(FileService service, ModelFile file, ModelFile targetFolder) {
        service.moveFile(file, targetFolder);

        // TODO En ciertas ocasiones no recarga bien los ficheros
        // EJ Tenemos la carpeta aa/bb y aa/cc
        // Si arrastramos un fichero de una a otra, aparecerá en los dos lados hasta que se recargue
        file.refreshFile();
        targetFolder.refreshChildren();
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

    /**
     * Returns a list of the transfers in progress.
     *
     * @return List of the transfers in progress
     */
    public int getTransfersInProgress() {
        return transfersInProgress.size();
    }

    /**
     * Add a listener for all the downloads. For each transfer in progress, this listener will be called for every 1% transfered.
     * The parameters are the file being transfered and the progress of the transfer (0-100)
     *
     * @param otherListener
     */
    public void addProgressListener(BiConsumer<ModelFile, Double> otherListener) {
        if (listener == null)
            listener = otherListener;
        else
            listener = listener.andThen(otherListener);
    }
}
