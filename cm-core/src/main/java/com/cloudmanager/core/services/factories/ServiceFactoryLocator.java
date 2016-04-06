package com.cloudmanager.core.services.factories;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class ServiceFactoryLocator {

    private static Map<String, ServiceFactory> map;

    public static ServiceFactory find(String serviceName) {
        return getServiceFactories().get(serviceName);
    }

    public static Collection<ServiceFactory> listAll() {
        return getServiceFactories().values();
    }

    private static Map<String, ServiceFactory> getServiceFactories() {
        if (map == null) {
            map = new HashMap<>();

            // TODO Enable this in the final version?
            // ServiceLoader.load(ServiceFactory.class, getPluginClassloader())

            ServiceLoader<ServiceFactory> loader = ServiceLoader.load(ServiceFactory.class);

            // Map the service name to its factory
            for (ServiceFactory serviceFactory : loader) {
                System.out.println("Service Loaded: " + serviceFactory.getClass().getName());
                map.put(serviceFactory.getServiceName(), serviceFactory);
            }

            System.out.println("All services loaded");
        }

        return map;
    }

    private static URLClassLoader getPluginClassloader() {
        File folder = new File("plugins");

        if (!folder.exists())
            folder.mkdirs();

        File[] files = folder.listFiles(file -> file.getPath().toLowerCase().endsWith(".jar"));
        URL[] urls = Arrays.stream(files).map(ServiceFactoryLocator::toURL).toArray(URL[]::new);

        return new URLClassLoader(urls);
    }

    private static URL toURL(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
