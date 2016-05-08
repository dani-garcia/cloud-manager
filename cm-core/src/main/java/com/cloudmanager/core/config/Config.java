package com.cloudmanager.core.config;

import com.cloudmanager.core.model.FileRepo;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class Config {
    /**
     * Application language
     */
    @JsonProperty
    private Locale locale = Locale.getDefault();

    /**
     * Repos list
     */
    @JsonProperty
    private List<FileRepo> repos = new ArrayList<>();

    @JsonProperty
    private Map<String, String> settings = new HashMap<>();

    // Getters and setters

    Locale getLocale() {
        return locale;
    }

    List<FileRepo> getRepos() {
        return Collections.unmodifiableList(repos);
    }

    List<FileRepo> _getRepos() {
        return repos;
    }

    public String getSetting(String key) {
        return settings.get(key);
    }

    public void putSetting(String key, String value) {
        settings.put(key, value);
        ConfigManager.save();
    }
}
