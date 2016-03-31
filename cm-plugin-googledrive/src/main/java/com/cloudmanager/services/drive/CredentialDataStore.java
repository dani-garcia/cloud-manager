package com.cloudmanager.services.drive;

import com.cloudmanager.core.config.ConfigManager;
import com.cloudmanager.core.model.ServiceAccount;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

class CredentialDataStore implements DataStore<StoredCredential> {

    private final ServiceAccount account;

    CredentialDataStore(ServiceAccount account) {
        this.account = account;
    }

    @Override
    public StoredCredential get(String key) throws IOException {
        StoredCredential cred = new StoredCredential();

        cred.setAccessToken(account.getAuth().get("accessToken"));
        cred.setExpirationTimeMilliseconds(Long.parseLong(account.getAuth().get("expirationTimeMilliseconds")));
        cred.setRefreshToken(account.getAuth().get("refreshToken"));

        return cred;
    }

    @Override
    public DataStore<StoredCredential> set(String key, StoredCredential cred) throws IOException {

        account.getAuth().put("accessToken", cred.getAccessToken());
        account.getAuth().put("expirationTimeMilliseconds", cred.getExpirationTimeMilliseconds() + "");
        account.getAuth().put("refreshToken", cred.getRefreshToken());

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
