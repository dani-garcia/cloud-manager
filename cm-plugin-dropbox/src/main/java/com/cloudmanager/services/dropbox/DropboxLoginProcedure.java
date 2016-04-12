package com.cloudmanager.services.dropbox;

import com.cloudmanager.core.config.AccountManager;
import com.cloudmanager.core.model.ServiceAccount;
import com.cloudmanager.core.services.login.LoginField;
import com.cloudmanager.core.services.login.LoginField.FieldType;
import com.cloudmanager.core.services.login.LoginProcedure;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWebAuthNoRedirect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

class DropboxLoginProcedure implements LoginProcedure {

    private List<LoginField> fields = new ArrayList<>();
    private LoginField code, name;

    private DbxWebAuthNoRedirect webAuth;
    private DropboxService service;

    private Consumer<Boolean> onComplete;

    DropboxLoginProcedure(DbxWebAuthNoRedirect webAuth, DropboxService service) {
        this.webAuth = webAuth;
        this.service = service;
    }

    @Override
    public List<LoginField> getFields() {
        return fields;
    }

    @Override
    public void preLogin() {
        LoginField text = new LoginField(FieldType.PLAIN_TEXT, "", "login_url_copy_code");
        LoginField url = new LoginField(FieldType.OUTPUT, "login_url", "");
        code = new LoginField(FieldType.INPUT, "login_code", "");
        name = new LoginField(FieldType.INPUT, "service_name", "");

        fields.add(text);
        fields.add(url);
        fields.add(code);
        fields.add(name);

        String authorizeUrl = webAuth.start();

        url.setValue(authorizeUrl); // TODO Add redirect_uri as by https://www.dropbox.com/developers-v1/core/docs#oa2-authorize
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
        return true;
    }

    @Override
    public boolean postLogin() {
        if (code.getValue().trim().isEmpty()) {
            onComplete.accept(true);
            return false;
        }

        DbxAuthFinish authFinish;

        try {
            authFinish = webAuth.finish(code.getValue());
        } catch (DbxException ex) {
            onComplete.accept(false);
            return false;
        }

        // Finish login
        ServiceAccount account = new ServiceAccount(name.getValue(), DropboxService.SERVICE_NAME, service);
        account.getAuth().put("access_token", authFinish.getAccessToken());
        service.setAccount(account);

        AccountManager.getInstance().addAccount(account);

        service.authenticate();

        onComplete.accept(true);
        return true;
    }

    @Override
    public void cancel() { } // No need to do anything
}
