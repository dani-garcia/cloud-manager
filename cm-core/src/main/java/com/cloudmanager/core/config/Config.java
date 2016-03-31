package com.cloudmanager.core.config;

import com.cloudmanager.core.model.ServiceAccount;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Config {
    /**
     * Application language
     */
    @JsonProperty
    private Locale locale = Locale.getDefault();

    /**
     * Accounts list
     */
    @JsonProperty
    private List<ServiceAccount> accounts = new ArrayList<>();

    // Getters and setters

    public Locale getLocale() {
        return locale;
    }

    public List<ServiceAccount> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    List<ServiceAccount> _getAccounts() {
        return accounts;
    }
}
