package com.cloudmanager.core.config;

import com.cloudmanager.core.model.FileRepo;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

class Config {
    /**
     * Application language
     */
    @JsonProperty
    private Locale locale = Locale.getDefault();

    /**
     * Repos list
     */
    @JsonProperty
    private List<FileRepo> repos = new ArrayList<>();

    // Getters and setters

    Locale getLocale() {
        return locale;
    }

    List<FileRepo> getRepos() {
        return Collections.unmodifiableList(repos);
    }

    List<FileRepo> _getRepos() {
        return repos;
    }
}
