package tech.eportfolio.server.controller;

import com.microsoft.azure.storage.StorageException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.exception.handler.AuthenticationExceptionHandler;
import tech.eportfolio.server.common.jsend.SuccessResponse;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.AzureStorageService;
import tech.eportfolio.server.service.UserService;

import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/blobs")
public class BlobController extends AuthenticationExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AzureStorageService azureStorageService;

    private final UserService userService;

    @Autowired
    public BlobController(AzureStorageService azureStorageService, @Qualifier("UserServiceCacheImpl") UserService userService) {
        this.azureStorageService = azureStorageService;
        this.userService = userService;
    }

    /**
     * Upload an image to blob storage. This endpoint is intended for moderator and developer to
     * upload static content.
     *
     * @param multipartFile File
     * @return URI pointing to the created resource
     */
    @PostMapping("/image")
    public ResponseEntity<SuccessResponse<String>> uploadImage(@RequestParam MultipartFile multipartFile) throws StorageException, IOException, URISyntaxException {
        return new SuccessResponse<>("URI", azureStorageService.uploadPicture(multipartFile).toString()).toOk();
    }

    /**
     * Upload a file to the user's storage
     *
     * @param username      username
     * @param multipartFile file
     * @return URI pointing to the created resource
     */
    @PostMapping("/{username}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<String>> uploadBlob(@PathVariable @NotEmpty String username, @RequestParam MultipartFile multipartFile) throws StorageException, IOException, URISyntaxException {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        URI url = azureStorageService.uploadBlob(user.getBlobUUID(), multipartFile);
        logger.debug("File {} uploaded to {}", multipartFile.getOriginalFilename(), url);
        return new SuccessResponse<>("URI", url.toString()).toOk();
    }

    /**
     * Return a list of URIs of files in the user's container
     *
     * @param username username
     * @return URI pointing to the created resource
     */
    @GetMapping("/{username}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<List<URI>>> getBlobs(@PathVariable @NotEmpty String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        List<URI> uris = new LinkedList<>();
        try {
            uris.addAll(azureStorageService.listBlob(user.getBlobUUID()));
        } catch (URISyntaxException | StorageException exception) {
            logger.error("Failed to list blob {}", exception.getMessage());
        }
        // Returns a list of urls
        return new SuccessResponse<>("URI", uris).toOk();
    }

    /**
     * Delete a file from the user's storage
     *
     * @param username username
     * @param blobName file
     * @return 200
     */
    @DeleteMapping("/{username}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Object>> deleteBlob(@PathVariable @NotEmpty String username, @RequestParam String blobName) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        // Delete blob
        try {
            azureStorageService.deleteBlob(user.getBlobUUID(), blobName);
        } catch (URISyntaxException | StorageException exception) {
            logger.info("Failed to delete blob {} from container {}", blobName, username);
        }
        return new SuccessResponse<>().toAccepted();
    }
}
