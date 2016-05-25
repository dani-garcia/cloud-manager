package com.cloudmanager.core.localservice;

import com.cloudmanager.core.api.login.AbstractLoginProcedure;
import com.cloudmanager.core.api.login.LoginField;
import com.cloudmanager.core.model.FileServiceSettings;

import java.util.Collections;
import java.util.List;

/**
 * Login procedure implementation for the local login. This one is manual and doesn't need any authentication
 */
class LocalLoginProcedure extends AbstractLoginProcedure {
    private FileServiceSettings settings;

    @Override
    public List<LoginField> getFields() {
        return Collections.emptyList();
    }

    @Override
    public boolean isPostLoginManual() {
        return true;
    }

    @Override
    public void preLogin(String visualName) {
        settings = new FileServiceSettings(visualName, LocalService.SERVICE_NAME);
    }

    @Override
    public boolean postLogin() {
        onComplete.accept(Status.OK, settings);
        return true;
    }

    @Override
    public void cancel() { /* Nothing to cancel */ }
}
