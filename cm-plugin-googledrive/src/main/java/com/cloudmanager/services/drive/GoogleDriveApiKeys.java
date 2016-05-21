package com.cloudmanager.services.drive;

import com.cloudmanager.core.util.Util;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;

import java.util.Map;

/**
 * The API keys for the Google Drive service.
 * These are loaded from a properties file that is filtered by Maven at compile time.
 * If for whaatever the reason the file is not filtered, we exit the application with an error.
 */
class GoogleDriveApiKeys {
    static final GoogleClientSecrets SECRETS;

    static {
        // Load the map
        Map<String, String> map = Util.getPropertiesMap(GoogleDriveService.SERVICE_NAME + ".apikey");

        String key = map.get("key");
        String secret = map.get("secret");

        // Check if they are present, and exit if not
        if (key.startsWith("${") || secret.startsWith("${")) {
            System.err.println("Google Drive API Keys not set!");
            System.exit(-7);
        }

        // Create the secret
        SECRETS = new GoogleClientSecrets().setInstalled(new Details().setClientId(key).setClientSecret(secret));
    }
}
