package com.cloudmanager.services.drive;

import com.cloudmanager.core.util.Util;

import java.util.Map;

class GoogleDriveApiKeys {
    static final String KEY;
    static final String SECRET;

    static {
        Map<String, String> map = Util.getPropertiesMap(GoogleDriveApiKeys.class, GoogleDriveService.SERVICE_NAME + ".apikey");

        KEY = map.get("key");
        SECRET = map.get("secret");

        // Check if they are present
        if (KEY.startsWith("${") || SECRET.startsWith("${")) {
            System.err.println("Google Drive API Keys not set!");
            System.exit(-7);
        }
    }
}
