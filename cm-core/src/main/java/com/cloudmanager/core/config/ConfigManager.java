package com.cloudmanager.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;

public class ConfigManager {
    private static final File DEFAULT_FOLDER = new File("config");
    private static final File DEFAULT_FILE = new File(DEFAULT_FOLDER, "config.json");

    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private static Config instance;
    private static File configFile;

    private ConfigManager() {}

    public static Config getConfig() {
        if (instance == null) {
            load(DEFAULT_FILE);
        }

        return instance;
    }

    public static Config load(File file) {
        if (instance != null)
            throw new IllegalStateException("Config already set");

        try {
            instance = MAPPER.readValue(file, Config.class);
        } catch (Exception e) {
            // If we couldn't load the configuration, we create a new one
            instance = new Config();
        }

        // We set the config location, to be able to save the file later
        configFile = file;

        return instance;
    }

    public static boolean save() {
        try {
            // Maje sure the config is loaded before saving
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
