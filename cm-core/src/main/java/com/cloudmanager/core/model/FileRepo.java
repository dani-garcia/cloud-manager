package com.cloudmanager.core.model;

import com.cloudmanager.core.api.service.FileService;
import com.cloudmanager.core.service.factories.ServiceFactoryLocator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a file repository. This is the equivalent of an account in an online service.
 */
public class FileRepo implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id, name, serviceName;

    private Map<String, String> auth;

    @JsonIgnore
    private transient FileService service;

    /**
     * Constructs a FileRepo from the ID, name, service name and authentication values
     *
     * @param id          The ID of the repository, must be unique. Set to null to generate a random one
     * @param name        The name of the repository. Only visual
     * @param serviceName The name of the service. Must be able to be located by ServiceLocator
     * @param auth        The authentication parameters. These depend on the service of the repository
     */
    public FileRepo(@JsonProperty("id") String id,
                    @JsonProperty("name") String name,
                    @JsonProperty("service_name") String serviceName,
                    @JsonProperty("auth") Map<String, String> auth) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.serviceName = serviceName;
        this.auth = auth;
    }

    /**
     * Constructs a FileRepo from its name and service name
     *
     * @param name        The name of the repository. Only visual
     * @param serviceName The name of the service. Must be able to be located by ServiceLocator
     */
    public FileRepo(String name, String serviceName) {
        this(null, name, serviceName, new HashMap<>());
    }

    /**
     * Returns the ID of the repository
     *
     * @return The ID of the repository
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name of the repository. This is only visual, to get a unique identifier, use {@link #getId()}
     *
     * @return The name of the repository
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
