package com.cloudmanager.services.dropbox;

import com.cloudmanager.core.util.Util;
import com.dropbox.core.DbxAppInfo;

import java.util.Map;

/**
 * The API keys for the Dropbox service.
 * These are loaded from a properties file that is filtered by Maven at compile time.
 * If for whaatever the reason the file is not filtered, we exit the application with an error.
 */
class DropboxApiKeys {
    static final DbxAppInfo APP_INFO;

    static {
        // Load the map
        Map<String, String> map = Util.getPropertiesMap(DropboxService.SERVICE_NAME + ".apikey");

        String key = map.get("key");
        String secret = map.get("secret");

        // Check if they are present, and exit if not
        if (key.startsWith("${") || secret.startsWith("${")) {
            System.err.println("Dropbox API Keys not set!");
            System.exit(-7);
        }

        // Create the secret
        APP_INFO = new DbxAppInfo(key, secret);
    }
}
