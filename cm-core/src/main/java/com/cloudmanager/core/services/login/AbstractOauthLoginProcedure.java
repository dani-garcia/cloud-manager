package com.cloudmanager.core.services.login;

import com.cloudmanager.core.services.login.LoginField.FieldType;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractOauthLoginProcedure extends AbstractLoginProcedure {
    private List<LoginField> fields = new ArrayList<>();
    protected LoginField url;
    protected String repoName;

    @Override
    public void preLogin(String repoName) {
        this.repoName = repoName;

        LoginField text = new LoginField(FieldType.PLAIN_TEXT, "", "login_url_auto_code_explanation");
        url = new LoginField(FieldType.OUTPUT, "login_url", "");

        fields.add(text);
        fields.add(url);

        setUp();
    }

    protected abstract void setUp();

    @Override
    public List<LoginField> getFields() {
        return fields;
    }

    @Override
    public boolean isPostLoginManual() {
        return false;
    }

    @Override
    public boolean postLogin() {
        return false;
    }
}
