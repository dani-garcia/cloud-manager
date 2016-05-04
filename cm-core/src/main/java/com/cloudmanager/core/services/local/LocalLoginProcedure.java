package com.cloudmanager.core.services.local;

import com.cloudmanager.core.model.FileRepo;
import com.cloudmanager.core.services.login.AbstractLoginProcedure;
import com.cloudmanager.core.services.login.LoginField;

import java.util.Collections;
import java.util.List;

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
    public void cancel() { }
}
