package com.cloudmanager.core.service.local;

import com.cloudmanager.core.model.FileTransfer;
import com.cloudmanager.core.model.ModelFile;
import com.cloudmanager.core.api.service.AbstractFileService;
import com.cloudmanager.core.util.Util;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * File Service implemtation for the local filesystem
 */
public class LocalService extends AbstractFileService {
    public static final String SERVICE_NAME = "local";
    public static final String SERVICE_DISPLAY_NAME = "Local";
    public static final String SERVICE_ICON = "/icons/computer_keyboard.png";

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public String getServiceDisplayName() {
        return SERVICE_DISPLAY_NAME;
    }

    @Override
    public String getIcon() { return SERVICE_ICON; }

    @Override
    public boolean authenticate() { return true; /* No auth needed */ }

    @Override
    public ModelFile getRootFile() {
        // We might have more than one root (e.g. in Windows we have one root per drive),
        // so we create a fake node and add the real roots as its children
        ModelFile root = new ModelFile("", "", "", ModelFile.Type.FOLDER, 0L, null, null);

        // Set the real roots as children of the fake node
        root.setChildren(Arrays
                .stream(File.listRoots())
                .map(f -> toModelFile(f, root))
                .collect(Collectors.toList()));

        return root;
    }

    @Override
    public List<ModelFile> getChildren(ModelFile parent) {
        if (!parent.isFolder())
            return null;

        // Get the children
        File[] files = new File(parent.getPath()).listFiles();
        if (files == null)
            return null;

        // Convert them
        return Arrays.stream(files)
                .filter(f -> !f.isHidden()) // Don't show hidden files
                .map(f -> toModelFile(f, parent))
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public ModelFile getDefaultDir() {
        File home = new File(System.getProperty("user.home"));
        return toModelFile(home, null);
    }

    private ModelFile toModelFile(File file, ModelFile parent) {
        final FileSystemView fsv = FileSystemView.getFileSystemView();

        String path = file.getAbsolutePath();
        String name = fsv.getSystemDisplayName(file);

        ModelFile.Type type = file.isDirectory() ? ModelFile.Type.FOLDER : ModelFile.Type.FILE;
        long length = file.length();
        Date lastModified = new Date(file.lastModified());

        return new ModelFile(path, name, path, type, length, lastModified, parent, file);
    }

    @Override
    public FileTransfer sendFile(ModelFile file) {
        try {
            InputStream stream = new FileInputStream(file.getPath());
            return new FileTransfer(stream, file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean receiveFile(FileTransfer transfer) {
        String targetPath = getCurrentDir().getPath();
        String targetName = transfer.getTargetFileName();
        File targetFile = new File(targetPath, targetName);

        try (InputStream input = transfer.getContentStream();
             FileOutputStream output = new FileOutputStream(targetFile)) {

            output.getChannel().lock(); // Lock the file

            Util.copy(input, output);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean createFolder(ModelFile parent, String name) {
        return new File(parent.getPath(), name).mkdir();
    }

    @Override
    public boolean moveFile(ModelFile file, ModelFile target) {
        Path filePath = new File(file.getPath()).toPath();
        Path targetPath = new File(target.getPath(), file.getName()).toPath();

        try {
            Files.move(filePath, targetPath);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean copyFile(ModelFile file, ModelFile target) {
        Path filePath = new File(file.getPath()).toPath();
        Path targetPath = new File(target.getPath()).toPath();

        try {
            Files.copy(filePath, targetPath);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteFile(ModelFile file) {
        Path filePath = new File(file.getPath()).toPath();

        try {
            Files.delete(filePath);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
