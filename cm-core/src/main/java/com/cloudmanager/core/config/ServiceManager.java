package com.cloudmanager.core.config;

import com.cloudmanager.core.model.FileRepo;
import com.cloudmanager.core.services.FileService;

import java.util.List;
import java.util.stream.Collectors;

public class ServiceManager {
    private static ServiceManager instance = new ServiceManager();

    public static ServiceManager getInstance() {return instance;}

    private ServiceManager() { }

    public FileService getService(String id) {
        FileRepo account = RepoManager.getInstance().getRepo(id);
        if (account == null)
            return null;

        return account.getService();
    }

    public List<FileService> getServices() {
        return RepoManager.getInstance().getRepos()
                .stream()
                .map(FileRepo::getService)
                .collect(Collectors.toList());
    }
}
