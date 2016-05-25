package com.cloudmanager.services.drive;

import com.cloudmanager.core.api.service.AbstractFileService;
import com.cloudmanager.core.model.FileServiceSettings;
import com.cloudmanager.core.model.FileTransfer;
import com.cloudmanager.core.model.ModelFile;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Joiner;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * File Service implementation for the Google Drive Service
 */
class GoogleDriveService extends AbstractFileService {
    /* Service Name and Icon */
    public static final String SERVICE_NAME = "googledrive";
    public static final String SERVICE_DISPLAY_NAME = "Google Drive";
    public static final String SERVICE_ICON = "/branding/googledrive-icon.png";

    /*Google Drive folder type. Used to differenciate folders from normal files */
    private static final String MIME_FOLDER = "application/vnd.google-apps.folder";

    /*----------------------------*/
    /* Google drive service class */
    /*----------------------------*/
    private Drive client;

    GoogleDriveService(FileServiceSettings settings) {
        super(settings);
    }

    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public String getServiceDisplayName() {
        return SERVICE_DISPLAY_NAME;
    }

    @Override
    public String getIcon() {
        return SERVICE_ICON;
    }

    @Override
    public boolean authenticate() {
        HttpTransport httpTransport;

        try {
            // Create secure connection
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            return false;
        }

        // Try to authenticate
        try {
            // set up authorization code flow
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JacksonFactory.getDefaultInstance(), GoogleDriveApiKeys.SECRETS,
                    Collections.singleton(DriveScopes.DRIVE))
                    .setCredentialDataStore(new CredentialDataStore(settings.getAuth()))
                    .build();

            // Get credentials
            Credential credential = flow.loadCredential("user");
            if (credential == null || credential.getRefreshToken() == null) {
                return false;
            }

            // Create client
            client = new Drive.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                    .setApplicationName(APP_NAME)
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public ModelFile getRootFile() {
        try {
            String id = client.files().get("root").setFields("id").execute().getId();
            return new ModelFile(id, "/", "", ModelFile.Type.FOLDER, 0L, null, null);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ModelFile> getChildren(ModelFile parent) {
        if (!parent.isFolder())
            return null;

        try {
            // Create query
            String query = "'" + parent.getId() + "' in parents and trashed = false";
            String fields = "nextPageToken, files(id, name, mimeType, size, modifiedTime, trashed)";
            int pageSize = 250;

            List<File> files = new ArrayList<>();
            String token = null;

            do {
                // We get the files
                FileList _files = client.files().list()
                        .setPageSize(pageSize)
                        .setPageToken(token)
                        .setQ(query).setFields(fields).execute();

                // If there is more than one page of results,
                // we use the token to get the next ones
                token = _files.getNextPageToken();

                // we add them to the list
                _files.getFiles().forEach(files::add);
            } while (token != null);

            // Return the list converted and sorted
            return files.stream()
                    .map(f -> toModelFile(f, parent))
                    .sorted()
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ModelFile getDefaultDir() {
        return getRootFile();
    }

    private ModelFile toModelFile(File f, ModelFile parent) {
        String path = parent.getPath() + "/" + f.getName();
        ModelFile.Type type = f.getMimeType().equals(MIME_FOLDER) ? ModelFile.Type.FOLDER : ModelFile.Type.FILE;
        long size = f.getSize() == null ? 0 : f.getSize();
        Date date = f.getModifiedTime() == null ? null : new Date(f.getModifiedTime().getValue());

        return new ModelFile(f.getId(), f.getName(), path, type, size, date, parent);
    }

    @Override
    public FileTransfer sendFile(ModelFile file) {
        try {
            InputStream stream = client.files().get(file.getId()).executeMediaAsInputStream();
            return new FileTransfer(stream, file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean receiveFile(FileTransfer transfer) {
        String parentId = getCurrentDir().getId();
        String targetName = transfer.getTargetFileName();
        InputStream stream = transfer.getContentStream();

        try {
            // File's metadata.
            File body = new File();
            body.setName(targetName);

            // Set the parent folder.
            body.setParents(Collections.singletonList(parentId));

            // File's content.
            String mimeType = null;
            InputStreamContent mediaContent = new InputStreamContent(mimeType, new BufferedInputStream(stream));

            File file = client.files().create(body, mediaContent).execute();
            stream.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createFolder(ModelFile parent, String name) {
        File meta = new File();
        meta.setName(name);
        meta.setParents(Collections.singletonList(parent.getId()));
        meta.setMimeType(MIME_FOLDER);

        try {
            client.files().create(meta)
                    .setFields("id")
                    .execute();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean moveFile(ModelFile file, ModelFile targetFolder) {
        try {
            File _file = client.files().get(file.getId()).setFields("parents").execute();

            // To move the file, we remove the parents and add the new one
            client.files().update(file.getId(), null)
                    .setRemoveParents(Joiner.on(',').join(_file.getParents()))
                    .setAddParents(targetFolder.getId())
                    .setFields("id, parents")
                    .execute();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean copyFile(ModelFile file, ModelFile targetFolder) {
        File meta = new File();
        meta.setName(file.getName());
        meta.setParents(Collections.singletonList(targetFolder.getId()));

        try {
            client.files().copy(file.getId(), meta).execute();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteFile(ModelFile file) {
        try {
            client.files().delete(file.getId()).execute();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean renameFile(ModelFile file, String newName) {
        try {
            File newFile = new File();
            newFile.setName(newName);

            client.files().update(file.getId(), newFile)
                    .execute();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}