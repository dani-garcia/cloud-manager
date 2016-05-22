package com.cloudmanager.core.api.service;

import com.cloudmanager.core.api.login.LoginProcedure;
import com.cloudmanager.core.model.FileRepo;

/**
 * Represents a factory used to create instances of services.
 */
public interface ServiceFactory {

    /**
     * Returns the name of the service this factory creates. This is used to identify the service.
     *
     * @return The service name
     */
    String getServiceName();

    /**
     * Returns the display name of the service this factory creates. This is used only for display.
     *
     * @return The service name
     */
    String getServiceDisplayName();

    /**
     * Returns the icon of the service this factory creates.
     *
     * @return The service name
     */
    String getIcon();

    /**
     * Starts the login procedure for the service this factory creates.
     *
     * @return The login procedure
     */
    LoginProcedure startLoginProcedure();

    /**
     * Create an instance of the service from the repository object
     *
     * @param repo The repository
     * @return The service instance
     */
    FileService create(FileRepo repo);
}
