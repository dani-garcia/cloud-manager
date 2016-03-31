package com.cloudmanager.services.dropbox;

import com.cloudmanager.core.model.ServiceAccount;
import com.cloudmanager.core.services.FileService;
import com.cloudmanager.core.services.factories.ServiceFactory;

public class DropboxServiceFactory implements ServiceFactory {

    @Override
    public String getServiceName() {
        return DropboxService.SERVICE_NAME;
    }

    @Override
    public FileService create() {
        return new DropboxService();
    }

    @Override
    public FileService create(ServiceAccount account) {
        return new DropboxService().setAccount(account);
    }
}
