
package com.cloudmanager.services.drive;

import com.cloudmanager.core.config.AccountManager;
import com.cloudmanager.core.model.ServiceAccount;
import com.cloudmanager.core.services.login.LoginField;
import com.cloudmanager.core.services.login.LoginField.FieldType;
import com.cloudmanager.core.services.login.LoginProcedure;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
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
import java.util.*;
import java.util.function.Consumer;

public class GoogleDriveLoginProcedure implements LoginProcedure {
    private List<LoginField> fields = new ArrayList<>();
    private LoginField url, name;

    private GoogleAuthorizationCodeFlow flow;
    private VerificationCodeReceiver receiver;

    private GoogleClientSecrets secrets;
    private GoogleDriveService service;

    private Consumer<Boolean> onComplete;

    GoogleDriveLoginProcedure(GoogleClientSecrets secrets, GoogleDriveService service) {
        this.secrets = secrets;
        this.service = service;
    }

    @Override
    public List<LoginField> getFields() {
        return fields;
    }

    @Override
    public void preLogin() {
        LoginField text = new LoginField(FieldType.PLAIN_TEXT, "", "login_url");
        url = new LoginField(FieldType.OUTPUT, "login_url", "");
        name = new LoginField(FieldType.INPUT, "service_name", "");

        fields.add(text);
        fields.add(url);
        fields.add(name);

        Thread thread = new Thread(this::setUp);
        thread.setDaemon(true); // Kill the thread on application exit
        // TODO This doesn't kill the JVM at exit
        thread.start();
    }

    private boolean setUp() {
        HttpTransport httpTransport;

        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            onComplete.accept(false);
            return false;
        }

        Map<String, String> authMap = new HashMap<>();

        // set up authorization code flow
        flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JacksonFactory.getDefaultInstance(), secrets,
                Collections.singleton(DriveScopes.DRIVE))
                .setCredentialDataStore(new CredentialDataStore(authMap))
                .build();

        receiver = new LocalServerReceiver();

        try {
            String redirectUri = receiver.getRedirectUri();
            AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri);
            url.setValue(authorizationUrl.build());

            String code = receiver.waitForCode();

            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();

            // store credential
            Credential credential = flow.createAndStoreCredential(response, "user");

            // Finish login
            ServiceAccount account = new ServiceAccount(name.getValue(), GoogleDriveService.SERVICE_NAME, service);
            account.getAuth().clear();
            account.getAuth().putAll(authMap);
            service.setAccount(account);

            AccountManager.getInstance().addAccount(account);

            service.authenticate();
            onComplete.accept(true);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            onComplete.accept(false);
            return false;
        } finally {
            try {receiver.stop();} catch (IOException e) {e.printStackTrace();}
        }
    }

    @Override
    public void addLoginCompleteListener(Consumer<Boolean> listener) {
        if (onComplete == null)
            onComplete = listener;
        else
            onComplete = onComplete.andThen(listener);
    }

    @Override
    public boolean isPostLoginManual() {
        return false;
    }

    @Override
    public boolean postLogin() {
        return false;
    }

    @Override
    public void cancel() {
        if (receiver == null)
            return;

        try {
            receiver.stop();
        } catch (IOException ignored) {}
    }
}
