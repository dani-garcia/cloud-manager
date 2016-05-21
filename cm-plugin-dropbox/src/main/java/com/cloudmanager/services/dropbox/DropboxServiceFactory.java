package com.cloudmanager.services.dropbox;

import com.cloudmanager.core.model.FileRepo;
import com.cloudmanager.core.services.FileService;
import com.cloudmanager.core.services.factories.ServiceFactory;
import com.cloudmanager.core.services.login.LoginProcedure;

/**
 * Implementation of the service factory for the Dropbox service
 */
public class DropboxServiceFactory implements ServiceFactory {
    @Override
    public String getServiceName() {
        return DropboxService.SERVICE_NAME;
    }

    @Override
    public String getServiceDisplayName() {
        return DropboxService.SERVICE_DISPLAY_NAME;
    }

    @Override
    public String getIcon() {
        return DropboxService.SERVICE_ICON;
    }

    @Override
    public LoginProcedure startLoginProcedure() {
        return new DropboxLoginProcedure();
    }

    @Override
    public FileService create(FileRepo repo) {
        return new DropboxService().setRepo(repo);
    }
}
