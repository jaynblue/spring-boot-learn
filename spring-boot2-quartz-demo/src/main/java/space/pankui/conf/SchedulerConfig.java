package space.pankui.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author pankui
 * @date 14/05/2018
 * <pre>
 *
 * </pre>
 */
@Configuration
public class SchedulerConfig {

    private DataSource dataSource;

    private PlatformTransactionManager transactionManager;

    @Autowired
    public SchedulerConfig(@Qualifier("schedulerDataSource") DataSource dataSource, @Qualifier("schedulerTransactionManager") PlatformTransactionManager transactionManager)
    {
        this.dataSource = dataSource;
        this.transactionManager = transactionManager;
    }

    @Bean
    public SchedulerFactoryBeanCustomizer schedulerFactoryBeanCustomizer()
    {
        return bean ->
        {
            bean.setDataSource(dataSource);
            bean.setTransactionManager(transactionManager);
        };
    }
}
