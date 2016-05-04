package com.cloudmanager.core.services;


import com.cloudmanager.core.model.FileRepo;
import com.cloudmanager.core.model.ModelFile;

public abstract class AbstractFileService implements FileService {

    private FileRepo repo;
    private ModelFile currentDir;

    @Override
    public String getRepoId() {
        return getRepo().getId();
    }

    protected FileRepo getRepo() {
        return repo;
    }

    public FileService setRepo(FileRepo repo) {
        if (this.repo != null)
            throw new IllegalStateException("Account already set");

        this.repo = repo;
        return this;
    }

    @Override
    public String getRepoName() {
        return getRepo().getName();
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
