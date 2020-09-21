package tech.eportfolio.server.service;

import com.microsoft.azure.storage.StorageException;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public interface AzureStorageService {
     void createContainer(String containerName) throws URISyntaxException, StorageException;

     void deleteContainer(String containerName) throws URISyntaxException, StorageException;

     URI uploadBlob(String containerName, MultipartFile multipartFile);

     List<URI> listBlob(String containerName);

     void deleteBlob(String containerName, String blobName);

     URI uploadPicture(MultipartFile multipartFile);

     URI uploadBlobFromInputStream(String containerName, InputStream inputStream, String filename);

}
