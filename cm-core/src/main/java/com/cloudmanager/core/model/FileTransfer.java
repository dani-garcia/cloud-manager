package com.cloudmanager.core.model;

import com.cloudmanager.core.util.ObservableInputStream;

import java.io.InputStream;
import java.util.function.BiConsumer;

/**
 * Represents a file transfer between services. It contains the stream of data to transfer and allows to listen for trasfer progress.
 */
public class FileTransfer {

    private final ObservableInputStream<ModelFile> contentStream;
    private final Long fileSize;
    private final String fileName;

    /**
     * Constructs a FileTrasfer from a stream of data and a ModelFile
     *
     * @param contentStream Data to transfer
     * @param file          The file being transfered
     */
    public FileTransfer(InputStream contentStream, ModelFile file) {
        this.fileName = file.getName();
        this.fileSize = file.getSize();

        this.contentStream = new ObservableInputStream<>(contentStream, fileSize, file);
    }

    /**
     * Adds a progress listener. This listener will be notified for every 1% of the trasfer.
     * <p>
     * The parameters are the file being transfered and the progress of the transfer (0-100)
     *
     * @param progressListener The listener to notify
     */
    public void addProgressListener(BiConsumer<ModelFile, Double> progressListener) {
        this.contentStream.addListener(progressListener);
    }

    /**
     * Returns the content stream.
     *
     * @return The content stream
     */
    public InputStream getContentStream() {
        return contentStream;
    }

    /**
     * Returns the target file name.
     *
     * @return The target file name
     */
    public String getTargetFileName() {
        return fileName;
    }

    /**
     * Returns the file size.
     *
     * @return The file size
     */
    public long getSize() {
        return fileSize;
    }
}
