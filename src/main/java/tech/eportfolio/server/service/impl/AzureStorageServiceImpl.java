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
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
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
    public URI uploadPicture(MultipartFile multipartFile) throws StorageException, IOException, URISyntaxException {
        // Site content will be placed in the image container
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
    public void createContainer(String containerName) throws URISyntaxException, StorageException {
        CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
        container.createIfNotExists(BlobContainerPublicAccessType.BLOB, new BlobRequestOptions(), new OperationContext());
    }

    /**
     * Delete a container on azure storage
     *
     * @param containerName container name to delete. Expect UUID for user generated content
     * @return success or fail
     */
    @Override
    public void deleteContainer(String containerName) throws URISyntaxException, StorageException {
        // Delete container
        CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
        container.deleteIfExists(AccessCondition.generateEmptyCondition(), new BlobRequestOptions(), new OperationContext());
    }

    /**
     * Upload a file to given container. It will create a new container if not exist.
     *
     * @param containerName container name
     * @param multipartFile MultipartFile file to upload
     * @return uri points to created resource
     */
    @Override
    public URI uploadBlob(String containerName, MultipartFile multipartFile) throws IOException, URISyntaxException, StorageException {
        return uploadBlobFromInputStream(containerName, multipartFile.getInputStream(), multipartFile.getOriginalFilename());
    }

    @Override
    public URI uploadBlobFromInputStream(String containerName, InputStream inputStream, String filename)
            throws URISyntaxException, StorageException, IOException {
        URI uri = null;
        createContainer(containerName);
        // Replace new line character, tab and whitespace with underscore
        filename = filename.replaceAll("[\\n\\r\\t\\s]", "_");
        CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
        CloudBlockBlob blob = container.getBlockBlobReference(filename);
        blob.upload(inputStream, -1);
        // Remove space in filename
        uri = blob.getUri();
        logger.info("blob {} uploaded, url {}", filename, uri);
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
    public void deleteBlob(String containerName, String blobName) throws URISyntaxException, StorageException {
        createContainer(containerName);
        CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
        CloudBlockBlob blobToBeDeleted = container.getBlockBlobReference(blobName);
        blobToBeDeleted.delete();
    }

    /**
     * List all files in a container.
     *
     * @param containerName container name
     * @return List<URI> resources
     */
    @Override
    public List<URI> listBlob(String containerName) throws URISyntaxException, StorageException {
        List<URI> uris = new ArrayList<>();
        createContainer(containerName);
        CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
        for (ListBlobItem blobItem : container.listBlobs()) {
            uris.add(blobItem.getUri());
        }
        return uris;
    }
}
