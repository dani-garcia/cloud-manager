package com.cloudmanager.services.dropbox;

import com.cloudmanager.core.config.AccountManager;
import com.cloudmanager.core.model.ServiceAccount;
import com.cloudmanager.core.services.login.AbstractOauthLoginProcedure;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.DbxWebAuth.*;
import javafx.application.Platform;

import java.io.IOException;
import java.util.Map;

class DropboxLoginProcedure extends AbstractOauthLoginProcedure {
    private DbxWebAuth webAuth;
    private CodeReceiverServer server;

    private DropboxService service;

    DropboxLoginProcedure(DropboxService service) {
        this.service = service;
    }

    protected void setUp() {
        try {
            server = new CodeReceiverServer();
            server.start();

            this.webAuth = new DbxWebAuth(DropboxService.requestConfig,
                    DropboxService.appInfo,
                    server.getRedirectUri(),
                    new SessionStore());

            String authorizeUrl = webAuth.start();
            url.setValue(authorizeUrl);

            startCodeReceiverThread(server);

        } catch (IOException e) {
            e.printStackTrace();
            onComplete.accept(false);
            cancel();
        }
    }

    private void startCodeReceiverThread(CodeReceiverServer server) {
        Thread thread = new Thread(() -> {
            try {
                Map<String, String[]> authMap = server.waitForCode();

                DbxAuthFinish authFinish;

                try {
                    authFinish = webAuth.finish(authMap);
                } catch (DbxException | BadRequestException | ProviderException | CsrfException | BadStateException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> onComplete.accept(false)); // Other error
                    return;
                } catch (NotApprovedException e) {
                    e.printStackTrace(); // User denied access
                    Platform.runLater(() -> onComplete.accept(false));
                    return;
                }

                // Finish login
                ServiceAccount account = new ServiceAccount(accountName, DropboxService.SERVICE_NAME, service);
                account.getAuth().put("access_token", authFinish.getAccessToken());
                service.setAccount(account);
                service.authenticate();

                Platform.runLater(() -> {
                    AccountManager.getInstance().addAccount(account);
                    onComplete.accept(true);
                });

            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> onComplete.accept(false));
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
