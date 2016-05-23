package com.cloudmanager.core.config;

import com.cloudmanager.core.model.FileServiceSettings;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

/**
 * A Config object represents the state of the configuration of the application and is used for the
 * serialization/deserialization of it.
 */
public class Config {
    /**
     * Enumeration with the global globalSettings keys
     */
    public enum Setting {
        leftPanel, rightPanel
    }

    @JsonProperty
    private Locale locale = Locale.getDefault();
    @JsonProperty
    private List<FileServiceSettings> serviceSettings = new ArrayList<>();
    @JsonProperty
    private Map<Setting, String> globalSettings = new HashMap<>();

    /**
     * Returns the application locale, by default this is the system locale
     *
     * @return The application locale
     */
    Locale getLocale() {
        return locale;
    }

    /**
     * Get the list of service globalSettings saved
     *
     * @return List of service globalSettings
     */
    public List<FileServiceSettings> getServiceSettings() {
        return serviceSettings;
    }

    /**
     * Get a global application setting
     *
     * @param key The setting key
     * @return The setting value
     */
    public String getGlobalSetting(Setting key) {
        return globalSettings.get(key);
    }

    /**
     * Set a global application setting
     *
     * @param key   The setting key
     * @param value The setting value
     */
    public void putGlobalSetting(Setting key, String value) {
        globalSettings.put(key, value);
        ConfigManager.save();
    }
}
