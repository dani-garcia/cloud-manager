package com.cloudmanager.core.services.login;

import com.cloudmanager.core.model.FileRepo;

import java.util.List;
import java.util.function.BiConsumer;

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

    void addLoginCompleteListener(BiConsumer<Boolean, FileRepo> listener);

    void cancel();

}
