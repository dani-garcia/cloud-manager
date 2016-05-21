package com.cloudmanager.core.config;

import com.cloudmanager.core.model.FileRepo;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

/**
 * A Config object represents the state of the configuration of the application and is used for the
 * serialization/deserialization of it.
 */
public class Config {
    /**
     * Enumeration with the global settings keys
     */
    public enum Setting {
        leftPanel, rightPanel
    }

    @JsonProperty
    private Locale locale = Locale.getDefault();
    @JsonProperty
    private List<FileRepo> repos = new ArrayList<>();
    @JsonProperty
    private Map<Setting, String> settings = new HashMap<>();

    /**
     * Returns the application locale, by default this is the system locale
     *
     * @return The application locale
     */
    Locale getLocale() {
        return locale;
    }

    /**
     * Get the list of repositories saved
     *
     * @return List of repositories
     */
    List<FileRepo> getRepos() {
        return Collections.unmodifiableList(repos);
    }

    List<FileRepo> _getRepos() {
        return repos;
    }

    /**
     * Get a global application setting
     *
     * @param key The setting key
     * @return The setting value
     */
    public String getSetting(Setting key) {
        return settings.get(key);
    }

    /**
     * Set a global application setting
     *
     * @param key   The setting key
     * @param value The setting value
     */
    public void putSetting(Setting key, String value) {
        settings.put(key, value);
        ConfigManager.save();
    }
}
