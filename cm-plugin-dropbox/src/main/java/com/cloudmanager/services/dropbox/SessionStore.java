package com.cloudmanager.services.dropbox;

import com.dropbox.core.DbxSessionStore;

/**
 * Stores the Cross-site request forgery token used by Dropbox authentication protocol
 */
class SessionStore implements DbxSessionStore {

    private String value;

    @Override
    public String get() {
        return value;
    }

    @Override
    public void set(String value) {
        this.value = value;
    }

    @Override
    public void clear() {
        this.value = null;
    }
}
