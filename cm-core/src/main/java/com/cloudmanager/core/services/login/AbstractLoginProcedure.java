package com.cloudmanager.core.services.login;

import com.cloudmanager.core.model.FileRepo;

import java.util.function.BiConsumer;

public abstract class AbstractLoginProcedure implements LoginProcedure {

    protected BiConsumer<Boolean, FileRepo> onComplete;

    @Override
    public void addLoginCompleteListener(BiConsumer<Boolean, FileRepo> listener) {
        if (onComplete == null)
            onComplete = listener;
        else
            onComplete = onComplete.andThen(listener);
    }
}
