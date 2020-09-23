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
public class ConfigureQuartzJob {

    // TODO: Change jobA to a more reasonable name
    @Bean(name = "jobADetail")
    public JobDetail jobADetails() {
        return JobBuilder.newJob(DeleteUserContainerJob.class).withIdentity("sampleJobA")
                .storeDurably().build();
    }

    @Bean(name = "mockContentJobDetail")
    public JobDetail mockContentJob() {
        return JobBuilder.newJob(MockContentJob.class).withIdentity("mockContentJob")
                .storeDurably().build();
    }

    @Bean(name = "DeleteUserContainerJobTrigger")
    public Trigger jobATrigger(@Qualifier("jobADetail") JobDetail jobDetail) {

        return TriggerBuilder.newTrigger().forJob(jobDetail)
                .withIdentity("TriggerA")
                // Execute every day at mid night
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 * * ?"))
                .build();
    }

//    @Bean(name = "mockContentJobTrigger")
//    public Trigger mockContentJobTrigger(@Qualifier("mockContentJobDetail") JobDetail mockContentJob) {
//
//        return TriggerBuilder.newTrigger().forJob(mockContentJob)
//                .withIdentity("mockContentJob")
//                // Execute every day at mid night
//                .withSchedule(CronScheduleBuilder.cronSchedule("* * * ? * *"))
//                .build();
//    }


    //    @Bean
//    public JobDetailFactoryBean jobDetail() {
//        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
//        jobDetailFactory.setJobClass(DeleteUserContainerJob.class);
//        jobDetailFactory.setDescription("Invoke DeleteUserContainer Job service...");
//        jobDetailFactory.setDurability(true);
//        return jobDetailFactory;
//    }
//
//    @Bean
//    public SimpleTriggerFactoryBean trigger(JobDetail job) {
//        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
//        trigger.setJobDetail(job);
//        trigger.setRepeatInterval(3600000);
//        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
//        return trigger;
//    }
//
//
//    @Bean
//    public Scheduler scheduler(Trigger trigger, JobDetail job, SchedulerFactoryBean factory)
//            throws SchedulerException {
//        Scheduler scheduler = factory.getScheduler();
//        scheduler.scheduleJob(job, trigger);
//        scheduler.start();
//        return scheduler;
//    }
}
