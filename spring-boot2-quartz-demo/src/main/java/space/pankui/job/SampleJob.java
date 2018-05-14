package space.pankui.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.springframework.scheduling.quartz.QuartzJobBean;


/**
 * @author pankui
 * @date 14/05/2018
 * <pre>
 *
 * </pre>
 */
public class SampleJob extends QuartzJobBean {

    private String name;

    // Invoked if a Job data map entry with that name
    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {
        System.out.println(String.format("Hello %s!", this.name));
    }
}
