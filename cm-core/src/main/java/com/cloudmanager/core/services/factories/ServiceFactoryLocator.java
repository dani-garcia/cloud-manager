package com.cloudmanager.core.services.factories;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Locates the {@link ServiceFactory} implementations.
 * <p>
 * To be able to locate these implementations, their JAR file must be on the application's classpath
 * and there must be a file on the 'META-INF\services' folder named
 * {@linkplain com.cloudmanager.core.services.factories.ServiceFactory com.cloudmanager.core.services.factories.ServiceFactory}
 * that contains the fully qualified class name to the Factory implementation (To include more than one, put each one on a separate line)
 */
public class ServiceFactoryLocator {

    private static Map<String, ServiceFactory> map;

    /**
     * Returns a ServiceFactory for the service name provided, if it doesn't exist, returns null instead.
     *
     * @param serviceName The service name to find
     * @return The factory for the provided service
     */
    public static ServiceFactory find(String serviceName) {
        return getServiceFactories().get(serviceName);
    }

    /**
     * Returns all the factories found
     *
     * @return List with all the factories
     */
    public static Collection<ServiceFactory> listAll() {
        return getServiceFactories().values();
    }

    private static Map<String, ServiceFactory> getServiceFactories() {
        if (map == null) {
            map = new HashMap<>();

            // TODO To load plugins from a specific folder, use this instead
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
