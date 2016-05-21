package com.cloudmanager.core.config;

import com.cloudmanager.core.model.FileRepo;

import java.util.List;
import java.util.function.Consumer;

/**
 * Manages the repositories loaded from the configuration
 */
public class RepoManager {
    private static RepoManager instance = new RepoManager();

    /**
     * Returns the manager instance
     *
     * @return The manager instance
     */
    public static RepoManager getInstance() {return instance;}

    private Consumer<List<FileRepo>> reposChangeListener;

    private RepoManager() { }

    /**
     * Adds the listener to be otified of changes.
     * <p>
     * When a repository is added or deleted, the listener will be called with the list of all the repositories after the change
     *
     * @param listener The function to call when the repositories change
     */
    public void addListener(Consumer<List<FileRepo>> listener) {
        if (reposChangeListener == null)
            reposChangeListener = listener;
        else
            reposChangeListener = reposChangeListener.andThen(listener);
    }

    /**
     * Adds a repository to the configuration. This will trigger the listeners.
     *
     * @param repo The repository to add
     */
    public void addRepo(FileRepo repo) {
        Config conf = ConfigManager.getConfig();
        conf._getRepos().add(repo);

        // Notify listeners and save
        reposChangeListener.accept(getRepos());
        ConfigManager.save();
    }

    /**
     * Removes a repository from the configuration. This will trigger the listeners.
     *
     * @param repo The repository to remove
     */
    public void removeRepo(FileRepo repo) {
        Config conf = ConfigManager.getConfig();
        conf._getRepos().remove(repo);

        // Notify listeners and save
        reposChangeListener.accept(getRepos());
        ConfigManager.save();
    }

    /**
     * Returns the repository with the given ID
     *
     * @param id The ID of the repository to find
     * @return The repository with the given ID or null if not found
     */
    public FileRepo getRepo(String id) {
        Config conf = ConfigManager.getConfig();

        for (FileRepo account : conf.getRepos()) {
            if (account.getId().equals(id))
                return account;
        }
        return null;
    }

    /**
     * Returns the list with all the repositories
     *
     * @return The repositories list
     */
    public List<FileRepo> getRepos() {
        return ConfigManager.getConfig().getRepos();
    }

}
