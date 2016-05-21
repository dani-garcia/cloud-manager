package com.cloudmanager.core.util;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class Util {

    /**
     * Copy all data from an InputStream to an OutputStream
     *
     * @param in  Source InputStream
     * @param out Target OutputStream
     * @return Bytes transfered
     * @throws IOException If there is any problem reading or writing the streams
     */
    public static long copy(InputStream in, OutputStream out) throws IOException {
        long totalBytesRead = 0L;
        byte[] buffer = new byte[4096];

        int bytesRead;
        while ((bytesRead = in.read(buffer)) > 0) {
            out.write(buffer, 0, bytesRead);
            totalBytesRead += bytesRead;
        }
        return totalBytesRead;
    }

    /**
     * Returns the properties map from the specified properties file
     *
     * @param path The path to the properties file
     * @return The properties map
     */
    public static Map<String, String> getPropertiesMap(String path) {
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
