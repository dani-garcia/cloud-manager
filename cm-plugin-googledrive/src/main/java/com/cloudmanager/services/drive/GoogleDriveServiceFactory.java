package com.cloudmanager.services.drive;

import com.cloudmanager.core.api.login.LoginProcedure;
import com.cloudmanager.core.api.service.FileService;
import com.cloudmanager.core.api.service.ServiceFactory;
import com.cloudmanager.core.model.FileServiceSettings;

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
    public FileService create(FileServiceSettings settings) {
        return new GoogleDriveService(settings);
    }
}