package com.cloudmanager.services.drive;

import com.cloudmanager.core.util.Util;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;

import java.util.Map;

class GoogleDriveApiKeys {
    static final GoogleClientSecrets SECRETS;

    static {
        Map<String, String> map = Util.getPropertiesMap(GoogleDriveService.SERVICE_NAME + ".apikey");

        String key = map.get("key");
        String secret = map.get("secret");

        // Check if they are present
        if (key.startsWith("${") || secret.startsWith("${")) {
            System.err.println("Google Drive API Keys not set!");
            System.exit(-7);
        }

        SECRETS = new GoogleClientSecrets().setInstalled(new Details().setClientId(key).setClientSecret(secret));
    }
}
