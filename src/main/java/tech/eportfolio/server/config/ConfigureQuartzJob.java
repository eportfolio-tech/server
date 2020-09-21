package tech.eportfolio.server.config;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.eportfolio.server.common.job.DeleteUserContainerJob;

/**
 * https://www.baeldung.com/spring-quartz-schedule
 */
@Configuration
public class ConfigureQuartzJob {

    // TODO: Change jobA to a more reasonable name
    @Bean
    public JobDetail jobADetails() {
        return JobBuilder.newJob(DeleteUserContainerJob.class).withIdentity("sampleJobA")
                .storeDurably().build();
    }

    @Bean
    public Trigger jobATrigger(JobDetail jobADetails) {

        return TriggerBuilder.newTrigger().forJob(jobADetails)
                .withIdentity("TriggerA")
                // Execute every day at mid night
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 * * ?"))
                .build();
    }

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
