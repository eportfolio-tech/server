package tech.eportfolio.server.config;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

@Configuration
public class AzureStorageConfig {

    @Autowired
    private Environment environment;

    @Value("${azure.storage.connectionString}")
    private String connectionString;

    @Value("${azure.storage.container.name}")
    private String containerName;


    @Bean
    public CloudBlobClient cloudBlobClient() throws URISyntaxException, InvalidKeyException {
        // Read ConnectionString from properties file and create a Azure Storage client
        CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
        return storageAccount.createCloudBlobClient();
    }

    @Bean
    public CloudBlobContainer testBlobContainer() throws URISyntaxException, StorageException, InvalidKeyException {
        // Test if cloudBlobClient is working
        return cloudBlobClient().getContainerReference(containerName);
    }
}
