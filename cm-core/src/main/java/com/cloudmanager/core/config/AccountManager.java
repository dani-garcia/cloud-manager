package com.cloudmanager.core.config;

import com.cloudmanager.core.model.ServiceAccount;

import java.util.List;
import java.util.function.Consumer;

public class AccountManager {
    private static AccountManager instance = new AccountManager();

    public static AccountManager getInstance() {return instance;}

    /**
     * Accounts update notification listener
     */
    private Consumer<List<ServiceAccount>> serviceChangeListener;

    private AccountManager() { }

    public void addListener(Consumer<List<ServiceAccount>> listener) {
        if (serviceChangeListener == null)
            serviceChangeListener = listener;
        else
            serviceChangeListener = serviceChangeListener.andThen(listener);
    }

    public void addAccount(ServiceAccount account) {
        Config conf = ConfigManager.getConfig();
        conf._getAccounts().add(account);

        // Notify listeners and save
        serviceChangeListener.accept(getAccounts());
        ConfigManager.save();
    }

    public void removeAccount(ServiceAccount account) {
        Config conf = ConfigManager.getConfig();
        conf._getAccounts().remove(account);

        // Notify listeners and save
        serviceChangeListener.accept(getAccounts());
        ConfigManager.save();
    }

    ServiceAccount getAccount(String id) {
        Config conf = ConfigManager.getConfig();

        for (ServiceAccount account : conf.getAccounts()) {
            if (account.getId().equals(id))
                return account;
        }
        return null;
    }

    public List<ServiceAccount> getAccounts() {
        return ConfigManager.getConfig().getAccounts();
    }

}
