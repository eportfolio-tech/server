package tech.eportfolio.server.service.impl;

import com.microsoft.azure.storage.AccessCondition;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.eportfolio.server.common.exception.UploadBlobFailedException;
import tech.eportfolio.server.service.AzureStorageService;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of AzureStorageService interface
 *
 * @author haswell
 */

@Service
@ConditionalOnProperty(value = "mock.azure.blobstorage.service.enabled", havingValue = "false", matchIfMissing = true)
public class AzureStorageServiceImpl implements AzureStorageService {

    private final CloudBlobClient cloudBlobClient;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    public AzureStorageServiceImpl(CloudBlobClient cloudBlobClient) {
        this.cloudBlobClient = cloudBlobClient;
    }

    /**
     * Upload an image to blob.
     * Note that this is intended for uploading static content on the website
     * It's not for user generated content.
     *
     * @param multipartFile file to be uploaded
     * @return uri pointing to the created resource
     */
    @Override
    public URI uploadPicture(MultipartFile multipartFile) {
        // Site content will be placed in the image container
        createContainer("image");
        URI uri = uploadBlob("image", multipartFile);
        return Optional.ofNullable(uri).orElseThrow(UploadBlobFailedException::new);
    }

    /**
     * Create a container on azure storage
     *
     * @param containerName container name. Expect UUID for user generated content
     * @return success or fail
     */
    @Override
    public boolean createContainer(String containerName) {
        boolean containerCreated = false;
        try {
            // Create a new container if not exist
            CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
            containerCreated = container.createIfNotExists(BlobContainerPublicAccessType.BLOB, new BlobRequestOptions(), new OperationContext());
        } catch (StorageException | URISyntaxException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return containerCreated;
    }

    /**
     * Delete a container on azure storage
     *
     * @param containerName container name to delete. Expect UUID for user generated content
     * @return success or fail
     */
    @Override
    public boolean deleteContainer(String containerName) {
        boolean containerDeleted = false;
        try {
            // Delete container
            CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
            containerDeleted = container.deleteIfExists(AccessCondition.generateEmptyCondition(), new BlobRequestOptions(), new OperationContext());
        } catch (StorageException | URISyntaxException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return containerDeleted;
    }

    /**
     * Upload a file to given container. It will create a new container if not exist.
     *
     * @param containerName container name
     * @param multipartFile MultipartFile file to upload
     * @return uri points to created resource
     */
    @Override
    public URI uploadBlob(String containerName, MultipartFile multipartFile) {
        URI uri = null;
        try {
            CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
            CloudBlockBlob blob = container.getBlockBlobReference(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            blob.upload(multipartFile.getInputStream(), -1);
            // Remove space in filename
            String multipartName = multipartFile.getName().replaceAll("[\n|\r|\t]", "_");
            uri = blob.getUri();
            logger.info("blob {} uploaded, url {}", multipartName, uri);
        } catch (URISyntaxException | StorageException | IOException e) {
            logger.error("Failed to upload blob: {}", e.getMessage());
        }
        return uri;
    }

    /**
     * Delete a file from given container.
     *
     * @param containerName container name
     * @param blobName      filename to be deleted
     * @return uri points to created resource
     */
    @Override
    public void deleteBlob(String containerName, String blobName) {
        try {
            CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
            CloudBlockBlob blobToBeDeleted = container.getBlockBlobReference(blobName);
            blobToBeDeleted.deleteIfExists();
        } catch (URISyntaxException | StorageException e) {
            logger.error("Failed to delete blob {}", e.getMessage());
        }
    }

    /**
     * List all files in a container.
     *
     * @param containerName container name
     * @return List<URI> resources
     */
    @Override
    public List<URI> listBlob(String containerName) {
        List<URI> uris = new ArrayList<>();
        try {
            CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
            for (ListBlobItem blobItem : container.listBlobs()) {
                uris.add(blobItem.getUri());
            }
        } catch (URISyntaxException | StorageException e) {
            e.printStackTrace();
        }
        return uris;
    }
}
