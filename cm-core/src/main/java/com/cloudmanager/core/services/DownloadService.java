package com.cloudmanager.core.services;

import com.cloudmanager.core.model.ModelFile;
import com.cloudmanager.core.transfers.FileTransfer;

import java.util.function.BiConsumer;

public class DownloadService {
    private static final DownloadService instance = new DownloadService();

    public static DownloadService get() {
        return instance;
    }

    private DownloadService() { }

    private BiConsumer<ModelFile, Double> listener;

    public void transferFile(FileService origin, ModelFile file, FileService target, ModelFile targetFolder) {
        // TODO Error si el archivo ya existe
        // TODO Si son el mismo servicio, comprobar que no intentamos mover un archivo dentro de si mismo o similar
        // TODO Si estamos moviendo ua carpeta, copiar recursivamente su contenido
        // TODO Gestor de transferencias y barras de progreso en segundo plano

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
        new Thread(() -> {
            FileTransfer transfer = origin.sendFile(file);
            transfer.addProgressListener(listener);
            target.receiveFile(transfer);

            targetFolder.refreshChildren();
        }).start();
    }

    public void addProgressListener(BiConsumer<ModelFile, Double> otherListener) {
        if (listener == null)
            listener = otherListener;
        else
            listener = listener.andThen(otherListener);
    }
}
