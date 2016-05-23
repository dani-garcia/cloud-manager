package com.cloudmanager.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Manages the load and save procedure of the application config
 */
public class ConfigManager {
    private static final String FALLBACK_FILE = "/default-config.json";

    private static final File DEFAULT_FOLDER = new File("config");
    private static final File DEFAULT_FILE = new File(DEFAULT_FOLDER, "config.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private static Config instance;
    private static File configFile;

    private ConfigManager() {}

    /**
     * Get the application config. If it isn't loaded, this method will load it first using the default location.
     * <p>
     * To save after modifying, call the method {@link #save()}
     *
     * @return The configuration object for the application
     */
    public static Config getConfig() {
        if (instance == null) {
            load(DEFAULT_FILE);
        }

        return instance;
    }

    /**
     * Get the application config from a different location. This needs to be called before any access to the configuration is done.
     *
     * @param file The config file location
     * @return The configuration object for the application
     * @throws IllegalStateException If the config was already loaded
     */
    public static Config load(File file) {
        if (instance != null)
            throw new IllegalStateException("Config already set");

        try {
            instance = MAPPER.readValue(file, Config.class);
        } catch (Exception e) {
            // If we couldn't load the configuration, we create one using the default
            try (InputStream stream = Config.class.getResourceAsStream(FALLBACK_FILE)) {
                instance = MAPPER.readValue(stream, Config.class);

                System.out.println("Couldn't find the config file, loading the default config");

            } catch (IOException e1) {
                // If we couldn't use the default, start with a blank one
                instance = new Config();

                System.out.println("Couldn't find the config or the default, loading a blank one");
            }
        }

        // We set the config location, to be able to save the file later
        configFile = file;

        // We set the app language
        Locale.setDefault(instance.getLocale());

        return instance;
    }

    /**
     * Saves the current configuration to the file it was loaded from.
     *
     * @return Whether it could save the configuration or not
     */
    public static boolean save() {
        try {
            // Make sure the config is loaded before saving
            getConfig();

            if (!configFile.exists()) {
                // Make sure the parent directory exists
                boolean dirsCreated = configFile.getParentFile().mkdirs();

                // Create the file if it doesn't exist
                boolean fileCreated = configFile.createNewFile();
            }

            MAPPER.writeValue(configFile, instance);

        } catch (Exception e) {
            e.printStackTrace();
            return false; // Couldn't save
        }

        return true;
    }
}
