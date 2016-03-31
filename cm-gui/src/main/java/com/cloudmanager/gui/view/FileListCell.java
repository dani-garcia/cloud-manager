package com.cloudmanager.gui.view;

import com.cloudmanager.core.services.FileService;
import com.cloudmanager.core.services.local.LocalService;
import com.cloudmanager.gui.util.ResourceManager;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableStringValue;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FileListCell extends ListCell<FileService> {

    private ObservableStringValue otherSelection;

    public FileListCell(ObservableStringValue otherSelection) {
        this.otherSelection = otherSelection;
    }

    @Override
    public void updateItem(FileService item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);

        } else {
            // Get the icon
            Image icon = ResourceManager.loadImage(item.getIcon());
            ImageView view = new ImageView(icon);

            // Set its size
            final int imgSize = 16;
            view.setFitWidth(imgSize);
            view.setFitHeight(imgSize);

            // Set the image
            setGraphic(view);

            // Set the name
            if (item.getServiceName().equals(LocalService.SERVICE_NAME))
                setText(ResourceManager.getString("local_filesystem"));
            else
                setText(item.getAccountName());

            // If the service is selected on the other panel, disable it here
            BooleanBinding serviceInUse = Bindings.equal(item.getAccountId(), otherSelection);

            disableProperty().bind(serviceInUse);
            styleProperty().bind(Bindings.when(serviceInUse)
                    .then("-fx-opacity: 0.3")
                    .otherwise("-fx-opacity: 1"));
        }
    }
}
