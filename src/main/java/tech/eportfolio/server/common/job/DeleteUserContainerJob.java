package tech.eportfolio.server.common.job;

import com.microsoft.azure.storage.StorageException;
import org.joda.time.LocalDate;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.eportfolio.server.model.User;
import tech.eportfolio.server.service.AzureStorageService;
import tech.eportfolio.server.service.UserService;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * This job clean deleted users' container
 * If a user has been marked as delete,
 * its container will be removed after 7 days of grace period
 */

@Component
public class DeleteUserContainerJob implements Job {

    @Autowired
    private UserService userService;

    @Autowired
    private AzureStorageService azureStorageService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        deleteUserContainerTask();
    }

    private void deleteUserContainerTask() {
        //Obtains a LocalDate set to the current system millisecond time using ISOChronology in the default time zone
        LocalDate current = LocalDate.now();
        // Get date of a week ago
        LocalDate weekAgoDate = current.minusDays(7);
        // Find user with a container who has been deleted a week ago
        List<User> toRemoveContainer = userService.findDeletedUserWithContainer(weekAgoDate.toDate());

        List<User> toUpdate = new LinkedList<>();
        // count how many job finished
        AtomicInteger success = new AtomicInteger();
        if (!toRemoveContainer.isEmpty()) {
            toRemoveContainer.forEach(u -> {
                try {
                    azureStorageService.deleteContainer(u.getBlobUUID());
                    logger.info("Container has been removed for user {}", u.getUsername());
                    // Set BlobUUID to null
                    u.setBlobUUID(null);
                    // Add user to update list
                    toUpdate.add(u);
                    // add success counter
                    success.getAndIncrement();
                } catch (URISyntaxException | StorageException e) {
                    logger.info("Failed to remove container for user {}: {}", u.getUsername(), e.getMessage());
                }
            });
        }
        userService.saveAll(toUpdate);
        logger.info("deleteUserContainerTask: {} container(s) removed", success.get());
    }

}
