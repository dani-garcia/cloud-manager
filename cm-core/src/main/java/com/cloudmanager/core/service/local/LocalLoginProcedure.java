package com.cloudmanager.core.service.local;

import com.cloudmanager.core.model.FileRepo;
import com.cloudmanager.core.api.login.AbstractLoginProcedure;
import com.cloudmanager.core.api.login.LoginField;

import java.util.Collections;
import java.util.List;

/**
 * Login procedure implementation for the local login. This one is manual and doesn't need any authentication
 */
class LocalLoginProcedure extends AbstractLoginProcedure {
    private FileRepo repo;

    @Override
    public List<LoginField> getFields() {
        return Collections.emptyList();
    }

    @Override
    public boolean isPostLoginManual() {
        return true;
    }

    @Override
    public void preLogin(String repoName) {
        repo = new FileRepo(repoName, LocalService.SERVICE_NAME);
    }

    @Override
    public boolean postLogin() {
        onComplete.accept(true, repo);
        return true;
    }

    @Override
    public void cancel() { /* Nothing to cancel */ }
}
