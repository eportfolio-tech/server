package tech.eportfolio.server.service;

import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

public interface AzureStorageService {
     boolean createContainer(String containerName);

     boolean deleteContainer(String containerName);

     URI uploadBlob(String containerName, MultipartFile multipartFile);

     List<URI> listBlob(String containerName);

     void deleteBlob(String containerName, String blobName);

     URI uploadPicture(MultipartFile multipartFile);
}
