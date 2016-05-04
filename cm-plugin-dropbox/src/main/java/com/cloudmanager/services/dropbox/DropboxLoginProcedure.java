package com.cloudmanager.services.dropbox;

import com.cloudmanager.core.model.FileRepo;
import com.cloudmanager.core.services.login.AbstractOauthLoginProcedure;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.DbxWebAuth.*;

import java.io.IOException;
import java.util.Map;

class DropboxLoginProcedure extends AbstractOauthLoginProcedure {
    private DbxWebAuth webAuth;
    private CodeReceiverServer server;

    protected void setUp() {
        try {
            server = new CodeReceiverServer();
            server.start();

            this.webAuth = new DbxWebAuth(DropboxService.requestConfig,
                    DropboxApiKeys.APP_INFO,
                    server.getRedirectUri(),
                    new SessionStore());

            String authorizeUrl = webAuth.start();
            url.setValue(authorizeUrl);

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
                Map<String, String[]> authMap = server.waitForCode();

                DbxAuthFinish authFinish;

                try {
                    authFinish = webAuth.finish(authMap);
                } catch (DbxException | BadRequestException | ProviderException | CsrfException | BadStateException e) {
                    e.printStackTrace();
                    onComplete.accept(false, null); // Other error
                    return;
                } catch (NotApprovedException e) {
                    e.printStackTrace(); // User denied access
                    onComplete.accept(false, null);
                    return;
                }

                // Finish login
                FileRepo repo = new FileRepo(repoName, DropboxService.SERVICE_NAME);
                repo.getAuth().put("access_token", authFinish.getAccessToken());

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
