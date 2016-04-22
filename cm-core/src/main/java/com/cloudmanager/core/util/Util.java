package com.cloudmanager.core.util;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public final class Util {

    public static String[] splitDir(String dir) {
        return dir.split(Pattern.quote(File.separator), -1);
    }

    public static long copy(InputStream source, OutputStream sink)
            throws IOException {
        long nread = 0L;
        byte[] buf = new byte[4096];
        int n;
        while ((n = source.read(buf)) > 0) {
            sink.write(buf, 0, n);
            nread += n;
        }
        return nread;
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
