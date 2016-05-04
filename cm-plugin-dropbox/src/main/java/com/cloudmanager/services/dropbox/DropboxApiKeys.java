package com.cloudmanager.services.dropbox;

import com.cloudmanager.core.util.Util;
import com.dropbox.core.DbxAppInfo;

import java.util.Map;

class DropboxApiKeys {
    static final DbxAppInfo APP_INFO;

    static {
        Map<String, String> map = Util.getPropertiesMap(DropboxService.SERVICE_NAME + ".apikey");

        String key = map.get("key");
        String secret = map.get("secret");

        // Check if they are present
        if (key.startsWith("${") || secret.startsWith("${")) {
            System.err.println("Dropbox API Keys not set!");
            System.exit(-7);
        }

        APP_INFO = new DbxAppInfo(key, secret);
    }
}
