package com.cloudmanager.gui.view;

import com.cloudmanager.core.services.factories.ServiceFactory;
import com.cloudmanager.gui.util.ResourceManager;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents a service factory in the login service selector dropdown
 */
public class ServiceFactoryListCell extends ListCell<ServiceFactory> {

    @Override
    public void updateItem(ServiceFactory item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);

        } else {
            // Get the icon
            Image icon = ResourceManager.loadImage(item.getIcon());
            ImageView view = new ImageView(icon);

            // Set its size
            final int imgSize = 32;
            view.setFitWidth(imgSize);
            view.setFitHeight(imgSize);

            // Set the image
            setGraphic(view);

            // Set the text
            setText(item.getServiceDisplayName());
        }
    }
}
