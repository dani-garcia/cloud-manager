package com.cloudmanager.core.api.service;

import com.cloudmanager.core.model.FileTransfer;
import com.cloudmanager.core.model.ModelFile;

import java.util.List;

/**
 * Represents a file service. This can be an online service or the local filesystem service.
 * <p>
 * To use it you first need to create a {@link com.cloudmanager.core.model.FileRepo} with the
 * help of a {@link com.cloudmanager.core.api.login.LoginProcedure}. The recommended way to
 * get this is with a {@link ServiceFactory}
 */
public interface FileService {

    /**
     * App Name. Used in some services APIs
     */
    String APP_NAME = "CloudManager";

    /*
     * REPOSITORY INFO
     */

    /**
     * Returns the repo ID.
     *
     * @return The repo ID
     */
    String getRepoId();

    /**
     * Returns the repo name. This is only visual.
     *
     * @return The repo name
     */
    String getRepoName();

    /*
     * SERVICE INFO
     */

    /**
     * Return the service name.
     *
     * @return The service name
     */
    String getServiceName();

    /**
     * Returns the service display name.
     *
     * @return The service display name
     */
    String getServiceDisplayName();

    /**
     * Returns the service icon.
     *
     * @return The icon
     */
    String getIcon();

    /*
     * SERVICE METHODS
     */

    /**
     * Authenticate. This needs to be called before doing anything else.
     * If it returns false, the authentication failed.
     *
     * @return True if successful authentication, false otherwise
     */
    boolean authenticate();


    /**
     * Returns the root file.
     *
     * @return The root file
     */
    ModelFile getRootFile();

    /**
     * Returns the provided file's children.
     *
     * @param parent The parent folder
     * @return The children
     */
    List<ModelFile> getChildren(ModelFile parent);


    /**
     * Returns the current dir.
     *
     * @return The current dir
     */
    ModelFile getCurrentDir();

    /**
     * Sets current dir.
     *
     * @param file The new current dir
     */
    void setCurrentDir(ModelFile file);

    /**
     * Returns the default dirrectory
     *
     * @return The default directory
     */
    ModelFile getDefaultDir();


    /**
     * Send a file (to another repository).
     *
     * @param file The file to transfer
     * @return The trasfer object
     */
    FileTransfer sendFile(ModelFile file);

    /**
     * Receive a file (from another repository).
     *
     * @param transfer The transfer object
     * @return True if the trasfer is successful, false otherwise
     */
    boolean receiveFile(FileTransfer transfer);


    /**
     * Create a folder at the given location, with the given name
     *
     * @param parent The parent folder
     * @param name   the name
     * @return True if the creation is successful, false otherwise
     */
    boolean createFolder(ModelFile parent, String name);

    /**
     * Move a file (on the same repository).
     *
     * @param file         The file to move
     * @param targetFolder Where to move it
     * @return True if the move is successful, false otherwise
     */
    boolean moveFile(ModelFile file, ModelFile targetFolder);

    /**
     * Copy a file (on the same repository).
     *
     * @param file         The file to copy
     * @param targetFolder Where to copy it
     * @return True if the copy is successful, false otherwise
     */
    boolean copyFile(ModelFile file, ModelFile targetFolder);

    /**
     * Copy a file.
     *
     * @param file The file to delete
     * @return True if the deletion is successful, false otherwise
     */
    boolean deleteFile(ModelFile file);
}
