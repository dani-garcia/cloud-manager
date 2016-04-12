package com.cloudmanager.gui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import sun.awt.image.IntegerComponentRaster;

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

        int width = icon.getIconWidth();
        int height = icon.getIconHeight();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().drawImage(icon.getImage(), 0, 0, null);

        IntegerComponentRaster icr = (IntegerComponentRaster) image.getRaster();

        WritableImage wimg = new WritableImage(width, height);

        wimg.getPixelWriter().setPixels(0, 0, width, height,
                PixelFormat.getIntArgbInstance(),
                icr.getDataStorage(), icr.getDataOffset(0), icr.getScanlineStride());

        return wimg;
    }
}
