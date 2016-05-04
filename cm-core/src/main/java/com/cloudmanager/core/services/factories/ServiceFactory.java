package com.cloudmanager.core.services.factories;

import com.cloudmanager.core.model.FileRepo;
import com.cloudmanager.core.services.FileService;
import com.cloudmanager.core.services.login.LoginProcedure;

public interface ServiceFactory {

    String getServiceName();

    String getServiceDisplayName();

    String getIcon();

    LoginProcedure startLoginProcedure();

    FileService create(FileRepo repo);
}
