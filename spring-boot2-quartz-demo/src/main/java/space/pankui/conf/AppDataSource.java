package space.pankui.conf;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author pankui
 * @date 14/05/2018
 * <pre>
 *
 * </pre>
 */

@Configuration
public class AppDataSource {

    @Bean
    @Primary
    @ConfigurationProperties("t3.datasource")
    public DataSourceProperties t3DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("t3.datasource")
    public HikariDataSource t3DataSource() {
        return t3DataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties("scheduler.datasource")
    public DataSourceProperties schedulerDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("scheduler.datasource")
    public HikariDataSource schedulerDataSource() {
        return schedulerDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    public PlatformTransactionManager schedulerTransactionManager() {
        final DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(schedulerDataSource());

        return transactionManager;
    }
}
