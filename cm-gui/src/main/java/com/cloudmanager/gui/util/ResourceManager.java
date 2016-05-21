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

/**
 * Manages the different resources: fxml loading, internationalized strings and images
 */
public class ResourceManager {

    private static final String BUNDLE_NAME = "bundles.strings";

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);


    /**
     * Returns the FXML loader for the intenationalization bundle in use
     *
     * @param path Path to the FXML
     * @return The loader
     */
    public static FXMLLoader getFXMLLoader(String path) {
        return new FXMLLoader(ResourceManager.class.getResource(path), RESOURCE_BUNDLE);
    }

    /**
     * Loads and returns the component from the given FXML file
     *
     * @param path The path to the FXML file
     * @param <T>  The type of the root component of the FXML
     * @return The component
     */
    public static <T> T loadFXML(String path) {
        try {
            return getFXMLLoader(path).load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the internationalized string with the given key
     *
     * @param key The key of the string
     * @return The internationalized string, or '!key!' if it doesn't exist
     */
    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    /**
     * Returns the internationalized string with the given key and formated with the given parameters
     * <p>
     * The format for the parameters is '{n}', n being the position of the parameter starting from 0.
     *
     * @param key    The key of the string
     * @param params The parameters
     * @return The internationalized string, or '!key!' if it doesn't exist
     */
    public static String getString(String key, Object... params) {
        try {
            return MessageFormat.format(RESOURCE_BUNDLE.getString(key), params);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    /**
     * Load an image from a file
     *
     * @param path The path to the file
     * @return The image
     */
    public static Image loadImage(String path) {
        return new Image(ResourceManager.class.getResourceAsStream(path));
    }

    /**
     * Converts an image from Swing to JavaFX
     *
     * @param icon Swing's {@link ImageIcon}
     * @return JavaFX's {@link Image}
     */
    public static Image toFXImage(ImageIcon icon) {
        if (icon == null) return null;

        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        icon.paintIcon(null, bi.getGraphics(), 0, 0);
        return SwingFXUtils.toFXImage(bi, null);
    }
}
