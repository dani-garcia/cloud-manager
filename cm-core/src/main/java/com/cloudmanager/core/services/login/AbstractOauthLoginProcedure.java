package com.cloudmanager.core.services.login;

import com.cloudmanager.core.services.login.LoginField.FieldType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractOauthLoginProcedure implements LoginProcedure {
    private List<LoginField> fields = new ArrayList<>();
    protected LoginField url;
    protected String accountName;

    protected Consumer<Boolean> onComplete;

    @Override
    public void preLogin(String accountName) {
        this.accountName = accountName;

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

    @Override
    public void addLoginCompleteListener(Consumer<Boolean> listener) {
        if (onComplete == null)
            onComplete = listener;
        else
            onComplete = onComplete.andThen(listener);
    }
}
