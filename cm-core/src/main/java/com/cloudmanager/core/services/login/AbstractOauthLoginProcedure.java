package com.cloudmanager.core.services.login;

import com.cloudmanager.core.services.login.LoginField.FieldType;

import java.util.ArrayList;
import java.util.List;

/**
 * Login procedure made specifically for OAuth login.
 * It already includes a adequate form and is using the automatic postLogin method.
 */
public abstract class AbstractOauthLoginProcedure extends AbstractLoginProcedure {
    /**
     * The fields for the login form, includes a message
     * explaining the login procedure and a text field to put the login URL
     */
    private List<LoginField> fields = new ArrayList<>();

    /**
     * The URL field
     */
    protected LoginField url;

    /**
     * The repository name chosen by the user
     */
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

    /**
     * Sets the login URL and starts the authentication code receiver server.
     * Once complete, it must call the {@link #onComplete} listener.
     */
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
