package com.cloudmanager.core.api.login;

import com.cloudmanager.core.model.FileRepo;

import java.util.function.BiConsumer;

/**
 * Login procedure with the complete listener already implemented
 */
public abstract class AbstractLoginProcedure implements LoginProcedure {

    /**
     * Notifies any listeners of the completion of the procedure.
     */
    protected BiConsumer<Boolean, FileRepo> onComplete;

    @Override
    public void addLoginCompleteListener(BiConsumer<Boolean, FileRepo> listener) {
        if (onComplete == null)
            onComplete = listener;
        else
            onComplete = onComplete.andThen(listener);
    }
}
