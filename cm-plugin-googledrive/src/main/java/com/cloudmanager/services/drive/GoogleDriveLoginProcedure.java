package com.cloudmanager.services.drive;

import com.cloudmanager.core.model.ServiceAccount;
import com.cloudmanager.core.services.login.AbstractOauthLoginProcedure;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.DriveScopes;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class GoogleDriveLoginProcedure extends AbstractOauthLoginProcedure {
    private GoogleAuthorizationCodeFlow flow;
    private VerificationCodeReceiver server;

    private GoogleClientSecrets secrets;
    private GoogleDriveService service;

    GoogleDriveLoginProcedure(GoogleClientSecrets secrets, GoogleDriveService service) {
        this.secrets = secrets;
        this.service = service;
    }

    @Override
    protected void setUp() {
        HttpTransport httpTransport;

        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            onComplete.accept(false, null);
            return;
        }

        Map<String, String> authMap = new HashMap<>();

        // set up authorization code flow
        flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JacksonFactory.getDefaultInstance(), secrets,
                Collections.singleton(DriveScopes.DRIVE))
                .setCredentialDataStore(new CredentialDataStore(authMap))
                .build();

        server = new LocalServerReceiver();

        try {
            String redirectUri = server.getRedirectUri();
            AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri);

            url.setValue(authorizationUrl.build());

            startCodeReceiverThread(redirectUri, authMap);

        } catch (IOException e) {
            e.printStackTrace();
            onComplete.accept(false, null);
            cancel();
        }
    }

    private void startCodeReceiverThread(String redirectUri, Map<String, String> authMap) {
        Thread thread = new Thread(() -> {
            try {
                String code = server.waitForCode();

                TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();

                // store credential
                flow.createAndStoreCredential(response, "user");

                // Finish login
                ServiceAccount account = new ServiceAccount(accountName, GoogleDriveService.SERVICE_NAME, service);
                account.getAuth().putAll(authMap);
                service.setAccount(account);
                service.authenticate();

                onComplete.accept(true, account);

            } catch (IOException e) {
                e.printStackTrace();
                onComplete.accept(false, null);
            } finally {
                cancel();
            }
        });

        thread.setDaemon(true); // Kill the thread on application exit
        thread.start();
    }

    @Override
    public void cancel() {
        if (server == null)
            return;

        try {
            server.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
