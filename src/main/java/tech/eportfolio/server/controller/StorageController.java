package tech.eportfolio.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.eportfolio.server.common.exception.UserNotFoundException;
import tech.eportfolio.server.common.exception.handler.AuthenticationExceptionHandler;
import tech.eportfolio.server.common.jsend.SuccessResponse;
import tech.eportfolio.server.model.UserStorageContainer.User;
import tech.eportfolio.server.service.AzureStorageService;
import tech.eportfolio.server.service.UserService;

import javax.validation.constraints.NotEmpty;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/blobs")
public class StorageController extends AuthenticationExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AzureStorageService azureStorageService;

    private final UserService userService;

    @Autowired
    public StorageController(AzureStorageService azureStorageService, UserService userService) {
        this.azureStorageService = azureStorageService;
        this.userService = userService;
    }

    @PostMapping("/image")
    public ResponseEntity<SuccessResponse<String>> uploadImage(@RequestParam MultipartFile multipartFile) {
        return new SuccessResponse<>("URI", azureStorageService.uploadPicture(multipartFile).toString()).toOk();
    }

    @PostMapping("/{username}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<String>> uploadBlob(@PathVariable @NotEmpty String username, @RequestParam MultipartFile multipartFile) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        String containerName = user.getBlobUUID().toString();
        azureStorageService.createContainer(containerName);
        URI url = azureStorageService.uploadBlob(containerName, multipartFile);
        return new SuccessResponse<>("URI", url.toString()).toOk();
    }

    @GetMapping("/{username}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<List<URI>>> getBlobs(@PathVariable @NotEmpty String username) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        String containerName = user.getBlobUUID().toString();
        azureStorageService.createContainer(containerName);
        List<URI> uris = azureStorageService.listBlob(containerName);
        return new SuccessResponse<>("URI", uris).toOk();
    }

    @DeleteMapping("/{username}")
    @ApiOperation(value = "", authorizations = {@Authorization(value = "JWT")})
    public ResponseEntity<SuccessResponse<Object>> deleteBlob(@PathVariable @NotEmpty String username, @RequestParam String blobName) {
        User user = userService.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        String containerName = user.getBlobUUID().toString();
        azureStorageService.createContainer(containerName);
        azureStorageService.deleteBlob(containerName, blobName);
        return new SuccessResponse<>().toAccepted();
    }
}
