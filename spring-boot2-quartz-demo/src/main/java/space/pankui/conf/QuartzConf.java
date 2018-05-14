package space.pankui.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import space.pankui.job.SampleJob;

/**
 * @author pankui
 * @date 14/05/2018
 * <pre>
 *
 * </pre>
 */

@Configuration
public class QuartzConf {

    @Bean
    public JobDetail sampleJobDetail() {
        return JobBuilder.newJob(SampleJob.class).withIdentity("sampleJob")
                .usingJobData("name", "World").storeDurably().build();
    }

    /***
     * every 2 seconds.
     *
     * */
    @Bean
    public Trigger sampleJobTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(2).repeatForever();

        return TriggerBuilder.newTrigger().forJob(sampleJobDetail())
                .withIdentity("sampleTrigger").withSchedule(scheduleBuilder).build();
    }
}
