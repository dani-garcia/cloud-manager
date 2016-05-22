package com.cloudmanager.core.service.local;

import com.cloudmanager.core.model.FileRepo;
import com.cloudmanager.core.api.service.FileService;
import com.cloudmanager.core.api.service.ServiceFactory;
import com.cloudmanager.core.api.login.LoginProcedure;

/**
 * Factory for the local service
 */
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