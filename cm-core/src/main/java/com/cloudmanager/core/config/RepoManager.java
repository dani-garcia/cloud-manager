package com.cloudmanager.core.config;

import com.cloudmanager.core.model.FileRepo;

import java.util.List;
import java.util.function.Consumer;

public class RepoManager {
    private static RepoManager instance = new RepoManager();

    public static RepoManager getInstance() {return instance;}

    /**
     * Repos update notification listener
     */
    private Consumer<List<FileRepo>> reposChangeListener;

    private RepoManager() { }

    public void addListener(Consumer<List<FileRepo>> listener) {
        if (reposChangeListener == null)
            reposChangeListener = listener;
        else
            reposChangeListener = reposChangeListener.andThen(listener);
    }

    public void addRepo(FileRepo repo) {
        Config conf = ConfigManager.getConfig();
        conf._getRepos().add(repo);

        // Notify listeners and save
        reposChangeListener.accept(getRepos());
        ConfigManager.save();
    }

    public void removeRepo(FileRepo repo) {
        Config conf = ConfigManager.getConfig();
        conf._getRepos().remove(repo);

        // Notify listeners and save
        reposChangeListener.accept(getRepos());
        ConfigManager.save();
    }

    public FileRepo getRepo(String id) {
        Config conf = ConfigManager.getConfig();

        for (FileRepo account : conf.getRepos()) {
            if (account.getId().equals(id))
                return account;
        }
        return null;
    }

    public List<FileRepo> getRepos() {
        return ConfigManager.getConfig().getRepos();
    }

}
