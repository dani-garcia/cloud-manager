package com.cloudmanager.core.config;

import com.cloudmanager.core.model.FileServiceSettings;

import java.util.List;
import java.util.function.Consumer;

/**
 * Manages the service instances loaded from the configuration
 */
public class ServiceManager {
    private static ServiceManager instance = new ServiceManager();

    /**
     * Returns the manager instance
     *
     * @return The manager instance
     */
    public static ServiceManager getInstance() {
        return instance;
    }

    private Consumer<List<FileServiceSettings>> serviceSettingsListeer;

    private ServiceManager() { }

    /**
     * Adds the listener to be notified of changes.
     * <p>
     * When a service instance is added or deleted, the listener will
     * be called with the list of all the service instances after the change
     *
     * @param listener The function to call when the service instances change
     */
    public void addListener(Consumer<List<FileServiceSettings>> listener) {
        if (serviceSettingsListeer == null)
            serviceSettingsListeer = listener;
        else
            serviceSettingsListeer = serviceSettingsListeer.andThen(listener);
    }

    /**
     * Adds a service to the configuration. This will trigger the listeners.
     *
     * @param serviceSettings The service to add
     */
    public void addServiceSettings(FileServiceSettings serviceSettings) {
        Config conf = ConfigManager.getConfig();
        conf._getServiceSettings().add(serviceSettings);

        // Notify listeners and save
        serviceSettingsListeer.accept(getServiceSettings());
        ConfigManager.save();
    }

    /**
     * Removes a service from the configuration. This will trigger the listeners.
     *
     * @param serviceSettings The service to remove
     */
    public void removeServiceSettings(FileServiceSettings serviceSettings) {
        Config conf = ConfigManager.getConfig();
        conf._getServiceSettings().remove(serviceSettings);

        // Notify listeners and save
        serviceSettingsListeer.accept(getServiceSettings());
        ConfigManager.save();
    }

    /**
     * Returns the service settings with the given ID
     *
     * @param id The ID of the service settings to find
     * @return The service settings with the given ID or null if not found
     */
    public FileServiceSettings getServiceSettings(String id) {
        Config conf = ConfigManager.getConfig();

        for (FileServiceSettings settings : conf.getServiceSettings()) {
            if (settings.getId().equals(id))
                return settings;
        }
        return null;
    }

    /**
     * Returns the list with all the service settings
     *
     * @return The service settings list
     */
    public List<FileServiceSettings> getServiceSettings() {
        return ConfigManager.getConfig().getServiceSettings();
    }

}
