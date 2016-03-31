package com.cloudmanager.services.drive;

import com.cloudmanager.core.model.ServiceAccount;
import com.cloudmanager.core.services.FileService;
import com.cloudmanager.core.services.factories.ServiceFactory;

public class GoogleDriveServiceFactory implements ServiceFactory {

    @Override
    public String getServiceName() {
        return GoogleDriveService.SERVICE_NAME;
    }

    @Override
    public FileService create() {
        return new GoogleDriveService();
    }

    @Override
    public FileService create(ServiceAccount account) {
        return new GoogleDriveService().setAccount(account);
    }
}