package com.cloudmanager.core.api.service;


import com.cloudmanager.core.model.FileServiceSettings;
import com.cloudmanager.core.model.ModelFile;

/**
 * Abstract implementation of the {@link FileService} interface
 * that includes the service settings and the current directory methods.
 * <p>
 * It also includes equals and hashcode based on the service ID.
 */
public abstract class AbstractFileService implements FileService {

    /**
     * The settings in use
     */
    protected FileServiceSettings settings;
    /**
     * The current directory. Used when receiving files
     */
    private ModelFile currentDir;

    public AbstractFileService(FileServiceSettings settings) {
        this.settings = settings;
    }

    @Override
    public String getInstanceId() {
        return settings.getId();
    }

    @Override
    public ModelFile getCurrentDir() {
        if (currentDir == null)
            currentDir = getRootFile();

        return currentDir;
    }

    @Override
    public void setCurrentDir(ModelFile file) {
        currentDir = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractFileService that = (AbstractFileService) o;

        return getInstanceId() != null ? getInstanceId().equals(that.getInstanceId()) : that.getInstanceId() == null;

    }

    @Override
    public int hashCode() {
        return getInstanceId() != null ? getInstanceId().hashCode() : 0;
    }
}
