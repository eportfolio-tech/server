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
import tech.eportfolio.server.common.exception.UploadPictureFailedException;
import tech.eportfolio.server.service.AzureStorageService;
import tech.eportfolio.server.service.UserService;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@ConditionalOnProperty(value = "mock.azure.blobstorage.service.enabled", havingValue = "false", matchIfMissing = true)
public class AzureStorageServiceImpl implements AzureStorageService {
    private final CloudBlobClient cloudBlobClient;

    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    public AzureStorageServiceImpl(CloudBlobClient cloudBlobClient, UserService userService) {
        this.cloudBlobClient = cloudBlobClient;
        this.userService = userService;
    }

    @Override
    public URI uploadPicture(MultipartFile multipartFile) {
        createContainer("image");
        URI uri = uploadBlob("image", multipartFile);
        return Optional.ofNullable(uri).orElseThrow(UploadPictureFailedException::new);
    }

    @Override
    public boolean createContainer(String containerName) {
        boolean containerCreated = false;
        try {
            CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
            containerCreated = container.createIfNotExists(BlobContainerPublicAccessType.BLOB, new BlobRequestOptions(), new OperationContext());
        } catch (StorageException | URISyntaxException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return containerCreated;
    }

    @Override
    public boolean deleteContainer(String containerName) {
        boolean containerCreated = false;
        try {
            CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
            containerCreated = container.deleteIfExists(AccessCondition.generateEmptyCondition(), new BlobRequestOptions(), new OperationContext());
        } catch (StorageException | URISyntaxException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return containerCreated;
    }

    @Override
    public URI uploadBlob(String containerName, MultipartFile multipartFile) {
        URI uri = null;
        try {
            CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
            CloudBlockBlob blob = container.getBlockBlobReference(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            blob.upload(multipartFile.getInputStream(), -1);
            String multipartName = multipartFile.getName().replaceAll("[\n|\r|\t]", "_");
            uri = blob.getUri();
            logger.info("blob {} uploaded, url {}", multipartName, uri.toString());
        } catch (URISyntaxException | StorageException | IOException e) {
            e.printStackTrace();
        }
        return uri;
    }

    @Override
    public void deleteBlob(String containerName, String blobName) {
        try {
            CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
            CloudBlockBlob blobToBeDeleted = container.getBlockBlobReference(blobName);
            blobToBeDeleted.deleteIfExists();
        } catch (URISyntaxException | StorageException e) {
            logger.error("Failed to delete blob {}", e.getMessage());
            e.printStackTrace();
        }
    }

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
