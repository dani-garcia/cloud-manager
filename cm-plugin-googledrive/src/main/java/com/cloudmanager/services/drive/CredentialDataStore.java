package com.cloudmanager.services.drive;

import com.cloudmanager.core.config.ConfigManager;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Stores the authentication credentials to a Google Drive repository
 */
class CredentialDataStore implements DataStore<StoredCredential> {

    private Map<String, String> map;

    /**
     * Constructs A CredentialDataStore from an authertication map
     *
     * @param map The authentication map
     */
    CredentialDataStore(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public StoredCredential get(String key) throws IOException {
        StoredCredential cred = new StoredCredential();

        cred.setAccessToken(map.get("accessToken"));
        cred.setExpirationTimeMilliseconds(Long.parseLong(map.get("expirationTimeMilliseconds")));
        cred.setRefreshToken(map.get("refreshToken"));

        return cred;
    }

    @Override
    public DataStore<StoredCredential> set(String key, StoredCredential cred) throws IOException {

        map.put("accessToken", cred.getAccessToken());
        map.put("expirationTimeMilliseconds", cred.getExpirationTimeMilliseconds() + "");
        map.put("refreshToken", cred.getRefreshToken());

        ConfigManager.save();
        return this;
    }

    /* These methods are not needed for what we use this class for */

    @Override
    public String getId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(String s) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataStoreFactory getDataStoreFactory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsValue(StoredCredential cred) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<StoredCredential> values() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataStore<StoredCredential> clear() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public DataStore<StoredCredential> delete(String s) throws IOException {
        throw new UnsupportedOperationException();
    }
}
