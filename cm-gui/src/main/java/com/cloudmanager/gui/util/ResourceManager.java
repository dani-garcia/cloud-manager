package com.cloudmanager.gui.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;

import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceManager {

    private static final String BUNDLE_NAME = "bundles.strings";

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);


    public static FXMLLoader getFXMLLoader(String path) {
        return new FXMLLoader(ResourceManager.class.getResource(path), RESOURCE_BUNDLE);
    }

    public static <T> T loadFXML(String path) {
        try {
            return getFXMLLoader(path).load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    public static String getString(String key, Object... params) {
        try {
            return MessageFormat.format(RESOURCE_BUNDLE.getString(key), params);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    public static Image loadImage(String path) {
        return new Image(ResourceManager.class.getResourceAsStream(path));
    }

    public static Image toFXImage(ImageIcon icon) {
        if (icon == null) return null;

        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        icon.paintIcon(null, bi.getGraphics(), 0, 0);
        return SwingFXUtils.toFXImage(bi, null);
    }
}
