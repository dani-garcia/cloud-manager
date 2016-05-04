package com.cloudmanager.core.services.local;

import com.cloudmanager.core.model.FileRepo;
import com.cloudmanager.core.services.FileService;
import com.cloudmanager.core.services.factories.ServiceFactory;
import com.cloudmanager.core.services.login.LoginProcedure;

public class LocalServiceFactory implements ServiceFactory {
    @Override
    public String getServiceName() {
        return LocalService.SERVICE_NAME;
    }

    @Override
    public String getServiceDisplayName() {
        return LocalService.SERVICE_DISPLAY_NAME;
    }

    @Override
    public String getIcon() {
        return LocalService.SERVICE_ICON;
    }

    @Override
    public LoginProcedure startLoginProcedure() {
        return new LocalLoginProcedure();
    }

    @Override
    public FileService create(FileRepo account) {
        return new LocalService().setRepo(account);
    }
}
