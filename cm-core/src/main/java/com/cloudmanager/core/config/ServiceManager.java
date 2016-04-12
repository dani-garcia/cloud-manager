package com.cloudmanager.core.config;

import com.cloudmanager.core.model.ServiceAccount;
import com.cloudmanager.core.services.FileService;
import com.cloudmanager.core.services.local.LocalService;

import java.util.List;
import java.util.stream.Collectors;

public class ServiceManager {
    private static ServiceManager instance = new ServiceManager();

    public static ServiceManager getInstance() {return instance;}

    private FileService localService = new LocalService();

    private ServiceManager() { }

    public FileService getService(String id) {
        if (LocalService.SERVICE_NAME.equals(id))
            return localService;

        ServiceAccount account = AccountManager.getInstance().getAccount(id);
        if (account == null)
            return null;

        return account.getService();
    }

    public List<FileService> getServices() {
        List<FileService> serviceList = AccountManager.getInstance().getAccounts()
                .stream()
                .map(ServiceAccount::getService)
                .collect(Collectors.toList());

        serviceList.add(localService);

        return serviceList;
    }
}
