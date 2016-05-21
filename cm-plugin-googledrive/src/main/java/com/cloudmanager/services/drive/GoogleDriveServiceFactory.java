package com.cloudmanager.services.drive;

import com.cloudmanager.core.model.FileRepo;
import com.cloudmanager.core.services.FileService;
import com.cloudmanager.core.services.factories.ServiceFactory;
import com.cloudmanager.core.services.login.LoginProcedure;

/**
 * Implementation of the service factory for the Google Drive service
 */
public class GoogleDriveServiceFactory implements ServiceFactory {

    @Override
    public String getServiceName() {
        return GoogleDriveService.SERVICE_NAME;
    }

    @Override
    public String getServiceDisplayName() {
        return GoogleDriveService.SERVICE_DISPLAY_NAME;
    }

    @Override
    public String getIcon() {
        return GoogleDriveService.SERVICE_ICON;
    }

    @Override
    public LoginProcedure startLoginProcedure() {
        return new GoogleDriveLoginProcedure();
    }

    @Override
    public FileService create(FileRepo repo) {
        return new GoogleDriveService().setRepo(repo);
    }
}