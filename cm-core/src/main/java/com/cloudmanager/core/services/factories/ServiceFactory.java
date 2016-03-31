package com.cloudmanager.core.services.factories;

import com.cloudmanager.core.model.ServiceAccount;
import com.cloudmanager.core.services.FileService;

public interface ServiceFactory {

    String getServiceName();

    FileService create();

    FileService create(ServiceAccount account);
}
