package com.cloudmanager.core.api.service;


import com.cloudmanager.core.model.FileRepo;
import com.cloudmanager.core.model.ModelFile;

/**
 * Abstract implementation of the {@link FileService} interface that includes the repository and the current directory methods.
 * It also includes equals and hashcode based on the repository ID.
 */
public abstract class AbstractFileService implements FileService {

    /**
     * The repository in use
     */
    protected FileRepo repo;
    /**
     * The current directory. Used when receiving files
     */
    private ModelFile currentDir;

    @Override
    public String getRepoId() {
        return repo.getId();
    }

    @Override
    public String getRepoName() {
        return repo.getName();
    }

    public FileService setRepo(FileRepo repo) {
        if (this.repo != null)
            throw new IllegalStateException("Account already set");

        this.repo = repo;
        return this;
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

        return getRepoId() != null ? getRepoId().equals(that.getRepoId()) : that.getRepoId() == null;

    }

    @Override
    public int hashCode() {
        return getRepoId() != null ? getRepoId().hashCode() : 0;
    }
}
