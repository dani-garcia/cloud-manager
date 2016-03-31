package com.cloudmanager.core.util;


import java.awt.Desktop;
import java.io.*;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public final class Util {

    public static String consoleReadLine() {
        try {
            return new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            return null;
        }
    }

    public static String[] splitDir(String dir) {
        return dir.split(Pattern.quote(File.separator), -1);
    }

    public static void openInBrowser(String url) {
        System.out.println(url);

        try {
            if (Desktop.isDesktopSupported()) {
                Desktop e = Desktop.getDesktop();
                if (e.isSupported(Desktop.Action.BROWSE)) {
                    System.out.println("Attempting to open that address in the default browser now...");
                    e.browse(URI.create(url));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getPropertiesMap(Class clazz, String path) {
        final Map<String, String> properties = new HashMap<>();

        try (InputStream is = Util.class.getClassLoader().getResourceAsStream(path)) {

            Properties prop = new Properties();
            prop.load(is);

            Enumeration<?> e = prop.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                String value = prop.getProperty(key);
                properties.put(key, value);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return properties;
    }
}
