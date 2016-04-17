package com.cloudmanager.core.services.login;

import java.util.List;
import java.util.function.Consumer;

public interface LoginProcedure {

    void preLogin(String accountName);

    List<LoginField> getFields();

    boolean isPostLoginManual();

    /**
     * Only use if {@link #isPostLoginManual} is true
     *
     * @return
     */
    boolean postLogin();

    void addLoginCompleteListener(Consumer<Boolean> listener);

    void cancel();

}
