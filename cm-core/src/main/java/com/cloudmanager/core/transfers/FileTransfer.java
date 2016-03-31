package com.cloudmanager.core.transfers;

import com.cloudmanager.core.model.ModelFile;

import java.io.InputStream;
import java.util.function.BiConsumer;

public class FileTransfer {

    private final ObservableInputStream<ModelFile> contentStream;
    private final ModelFile file;

    public FileTransfer(InputStream contentStream, ModelFile file) {
        this.contentStream = new ObservableInputStream<>(contentStream, file.getSize(), file);
        this.file = file;
    }

    public void addProgressListener(BiConsumer<ModelFile, Double> progressListener) {
        this.contentStream.addListener(progressListener);
    }

    public InputStream getContentStream() {
        return contentStream;
    }

    public String getTargetFileName() {
        return file.getName();
    }

    public long getSize() {
        return file.getSize();
    }
}
