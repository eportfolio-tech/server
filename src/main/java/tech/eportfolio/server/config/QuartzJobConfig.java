package tech.eportfolio.server.config;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.eportfolio.server.job.DeleteUserContainerJob;
import tech.eportfolio.server.job.MockContentJob;

/**
 * https://www.baeldung.com/spring-quartz-schedule
 */
@Configuration
public class QuartzJobConfig {

    @Bean(name = "DeleteUserContainerJob")
    public JobDetail deleteUserContainerJobDetails() {
        return JobBuilder.newJob(DeleteUserContainerJob.class).withIdentity("sampleJobA")
                .storeDurably().build();
    }

    @Bean(name = "mockContentJobDetail")
    public JobDetail mockContentJob() {
        return JobBuilder.newJob(MockContentJob.class).withIdentity("mockContentJob")
                .storeDurably().build();
    }

    @Bean(name = "DeleteUserContainerJobTrigger")
    public Trigger jobATrigger(@Qualifier("DeleteUserContainerJob") JobDetail jobDetail) {

        return TriggerBuilder.newTrigger().forJob(jobDetail)
                .withIdentity("TriggerA")
                // Execute every day at 1 am
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 * * ?"))
                .build();
    }

    @Bean(name = "mockContentJobTrigger")
    public Trigger mockContentJobTrigger(@Qualifier("mockContentJobDetail") JobDetail mockContentJob) {

        return TriggerBuilder.newTrigger().forJob(mockContentJob)
                .withIdentity("mockContentJob")
//               Every 30 minutes
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * ? * *"))
                // Every second
//                .withSchedule(CronScheduleBuilder.cronSchedule("* * * ? * *"))
                .build();
    }
}
