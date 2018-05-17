package space.pankui;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.jmx.JmxMeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import space.pankui.micrometer.DemoMetrics;
import io.micrometer.jmx.JmxConfig;

/**
 * @author pankui
 * @date 16/05/2018
 * <pre>
 *
 *   curl -i http://localhost:8080/actuator/metrics/demo.count
 *
 *
 *   注意开始监控这里 配置
 *
 *   management.endpoints.web.exposure.include=*
 *
 * </pre>
 */

@SpringBootApplication
public class MicrometerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicrometerApplication.class);
    }


    @Bean
    public DemoMetrics demoMetrics(){
        return new DemoMetrics();
    }


    @Bean
    public JmxMeterRegistry jmxMeterRegistry(JmxConfig config, Clock clock) {
        return new JmxMeterRegistry(config, clock, HierarchicalNameMapper.DEFAULT);
    }

    @Bean
    JvmThreadMetrics threadMetrics() {
        return new JvmThreadMetrics();
    }

}
