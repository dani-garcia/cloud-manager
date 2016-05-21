package com.cloudmanager.services.dropbox;

import com.cloudmanager.core.model.FileRepo;
import com.cloudmanager.core.services.login.AbstractOauthLoginProcedure;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.DbxWebAuth.*;

import java.io.IOException;
import java.util.Map;

/**
 * Implementation of the login procedure for the Dropbox service
 */
class DropboxLoginProcedure extends AbstractOauthLoginProcedure {
    private DbxWebAuth webAuth;
    private CodeReceiverServer server;

    protected void setUp() {
        try {
            // Start the code receiver server
            server = new CodeReceiverServer();
            server.start();

            // Start the login procedure
            this.webAuth = new DbxWebAuth(DropboxService.requestConfig,
                    DropboxApiKeys.APP_INFO,
                    server.getRedirectUri(),
                    new SessionStore());

            // Set the login URL
            String authorizeUrl = webAuth.start();
            url.setValue(authorizeUrl);

            // Wait for the code on a separate thread
            startCodeReceiverThread(server);

        } catch (IOException e) {
            e.printStackTrace();
            onComplete.accept(false, null);
            cancel();
        }
    }

    private void startCodeReceiverThread(CodeReceiverServer server) {
        Thread thread = new Thread(() -> {
            try {
                // Wait for the browser response
                Map<String, String[]> authMap = server.waitForCode();

                DbxAuthFinish authFinish;

                try {
                    // Authenticate the response
                    authFinish = webAuth.finish(authMap);
                } catch (NotApprovedException e) {
                    e.printStackTrace(); // User denied access
                    onComplete.accept(false, null);
                    return;
                } catch (DbxException | BadRequestException | ProviderException | CsrfException | BadStateException e) {
                    e.printStackTrace();
                    onComplete.accept(false, null); // Other error
                    return;
                }

                // Finish login
                FileRepo repo = new FileRepo(repoName, DropboxService.SERVICE_NAME);
                repo.getAuth().put("access_token", authFinish.getAccessToken());

                // Call the listeners
                onComplete.accept(true, repo);

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
