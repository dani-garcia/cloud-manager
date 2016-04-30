package com.cloudmanager.services.dropbox;

import com.cloudmanager.core.util.Util;

import java.util.Map;

class DropboxApiKeys {
    static final String KEY;
    static final String SECRET;

    static {
        Map<String, String> map = Util.getPropertiesMap(DropboxApiKeys.class, DropboxService.SERVICE_NAME + ".apikey");

        KEY = map.get("key");
        SECRET = map.get("secret");

        // Check if they are present
        if (KEY.startsWith("${") || SECRET.startsWith("${")) {
            System.err.println("Dropbox API Keys not set!");
            System.exit(-7);
        }
    }
}
