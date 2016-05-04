package com.cloudmanager.core.model;

import com.cloudmanager.core.services.FileService;
import com.cloudmanager.core.services.factories.ServiceFactoryLocator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileRepo implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id, name, serviceName;

    private Map<String, String> auth;

    @JsonIgnore
    private transient FileService service;

    public FileRepo(@JsonProperty("id") String id,
                    @JsonProperty("name") String name,
                    @JsonProperty("service_name") String serviceName,
                    @JsonProperty("auth") Map<String, String> auth) {
        // Random id
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.serviceName = serviceName;
        this.auth = auth;
    }

    public FileRepo(String name, String serviceName) {
        this(null, name, serviceName, new HashMap<>());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @JsonGetter("service_name")
    public String getServiceName() {
        return serviceName;
    }

    public FileService getService() {
        if (service == null)
            service = ServiceFactoryLocator.find(serviceName).create(this);

        return service;
    }

    public Map<String, String> getAuth() {
        return auth;
    }
}
