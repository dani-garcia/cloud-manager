package com.cloudmanager.core.model;

import com.cloudmanager.core.api.service.FileService;
import com.cloudmanager.core.managers.ServiceFactoryLocator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents the settings of a service instance. This includes a name given
 * by the user and any authentication information used by the service.
 */
public class FileServiceSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id, name, serviceName;

    private Map<String, String> auth;

    @JsonIgnore
    private transient FileService service;

    /**
     * Constructs a FileServiceSettings from the ID, name, service name and authentication values
     *
     * @param id          The ID of the service, must be unique. Set to null to generate a random one
     * @param name        The name of the service. Only visual
     * @param serviceName The name of the service. Must be able to be located by ServiceLocator
     * @param auth        The authentication parameters. These depend on the service of the service
     */
    public FileServiceSettings(@JsonProperty("id") String id,
                               @JsonProperty("name") String name,
                               @JsonProperty("service_name") String serviceName,
                               @JsonProperty("auth") Map<String, String> auth) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.serviceName = serviceName;
        this.auth = auth;
    }

    /**
     * Constructs a FileServiceSettings from its name and service name
     *
     * @param name        A visual name for this service instance.
     * @param serviceName The name of the service. Must be able to be located by ServiceLocator
     */
    public FileServiceSettings(String name, String serviceName) {
        this(null, name, serviceName, new HashMap<>());
    }

    /**
     * Returns the ID of the service
     *
     * @return The ID of the service
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name of the service. This is only visual, to get a unique identifier, use {@link #getId()}
     *
     * @return The name of the service
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the service name. This is used by {@link ServiceFactoryLocator} to locate the service factory.
     *
     * @return The service name
     */
    @JsonGetter("service_name")
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Returns an instance of the service. If it's not created yet, this method creates it.
     *
     * @return The service instance
     */
    public FileService getService() {
        if (service == null)
            service = ServiceFactoryLocator.find(serviceName).create(this);

        return service;
    }

    /**
     * Returns a map with the authentication values, dependent on the service.
     *
     * @return The authentication map
     */
    public Map<String, String> getAuth() {
        return auth;
    }
}
