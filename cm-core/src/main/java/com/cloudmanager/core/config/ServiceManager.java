package com.cloudmanager.core.config;

import com.cloudmanager.core.model.ServiceAccount;
import com.cloudmanager.core.services.FileService;
import com.cloudmanager.core.services.local.LocalService;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ServiceManager {
    private static ServiceManager instance = new ServiceManager();

    public static ServiceManager getInstance() {return instance;}

    /**
     * Accounts update notification listener
     */
    private Consumer<List<FileService>> serviceChangeListener;
    private FileService localService = new LocalService();

    private ServiceManager() { }

    public void addListener(Consumer<List<FileService>> listener) {
        if (serviceChangeListener == null)
            serviceChangeListener = listener;
        else
            serviceChangeListener = serviceChangeListener.andThen(listener);
    }

    public void addAccount(ServiceAccount account) {
        Config conf = ConfigManager.getConfig();
        conf._getAccounts().add(account);

        // Notify listeners and save
        serviceChangeListener.accept(getServices());
        ConfigManager.save();
    }

    public void removeServiceAccount(ServiceAccount account) {
        Config conf = ConfigManager.getConfig();
        conf._getAccounts().remove(account);

        // Notify listeners and save
        serviceChangeListener.accept(getServices());
        ConfigManager.save();
    }

    private ServiceAccount getAccount(String id) {
        Config conf = ConfigManager.getConfig();

        for (ServiceAccount account : conf.getAccounts()) {
            if (account.getId().equals(id))
                return account;
        }
        return null;
    }

    public FileService getService(String id) {
        if (id.equals(LocalService.SERVICE_NAME))
            return localService;

        ServiceAccount account = getAccount(id);
        if (account == null)
            return null;

        return account.getService();
    }

    private List<ServiceAccount> getAccounts() {
        return ConfigManager.getConfig().getAccounts();
    }

    public List<FileService> getServices() {
        List<FileService> serviceList = getAccounts()
                .stream()
                .map(ServiceAccount::getService)
                .collect(Collectors.toList());

        serviceList.add(localService);

        return serviceList;
    }
}
