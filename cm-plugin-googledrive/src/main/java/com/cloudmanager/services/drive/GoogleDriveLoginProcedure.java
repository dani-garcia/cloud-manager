package com.cloudmanager.services.drive;

import com.cloudmanager.core.api.login.AbstractOauthLoginProcedure;
import com.cloudmanager.core.model.FileServiceSettings;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.DriveScopes;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the login procedure for the Google Drive service
 */
class GoogleDriveLoginProcedure extends AbstractOauthLoginProcedure {
    private GoogleAuthorizationCodeFlow flow;
    private VerificationCodeReceiver server;

    @Override
    protected void setUp() {
        HttpTransport httpTransport;

        try {
            // Create a secure connection
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            onComplete.accept(Status.OTHER_ERR, null);
            return;
        }

        Map<String, String> authMap = new HashMap<>();

        // set up authorization code flow
        flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JacksonFactory.getDefaultInstance(), GoogleDriveApiKeys.SECRETS,
                Collections.singleton(DriveScopes.DRIVE))
                .setCredentialDataStore(new CredentialDataStore(authMap))
                .build();

        // Create the code receiver server
        server = new LocalServerReceiver();

        try {
            // Set the login url and start the server
            String redirectUri = server.getRedirectUri();
            AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri);

            url.setValue(authorizationUrl.build());

            startCodeReceiverThread(redirectUri, authMap);

        } catch (IOException e) {
            e.printStackTrace();
            onComplete.accept(Status.OTHER_ERR, null);
            cancel();
        }
    }

    private void startCodeReceiverThread(String redirectUri, Map<String, String> authMap) {
        Thread thread = new Thread(() -> {
            try {
                // Wait for a response from the browser
                String code = server.waitForCode();

                // If we get a code, check if it's valid
                TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();

                // store credential
                flow.createAndStoreCredential(response, "user");

                // Finish login
                FileServiceSettings settings = new FileServiceSettings(settingsName, GoogleDriveService.SERVICE_NAME);
                settings.getAuth().putAll(authMap);

                // Call the listeners
                onComplete.accept(Status.OK, settings);

            } catch (IOException e) {
                // If there was any error
                e.printStackTrace();

                if (e.getMessage().contains("(access_denied)")) {
                    onComplete.accept(Status.DENIED_PERMISSION, null);
                } else {
                    onComplete.accept(Status.OTHER_ERR, null);
                }

            } finally {
                // Stop the server at the end
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
