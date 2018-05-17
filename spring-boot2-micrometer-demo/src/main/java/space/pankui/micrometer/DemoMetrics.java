package space.pankui.micrometer;

import java.util.concurrent.atomic.AtomicInteger;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.stereotype.Service;

/**
 * @author pankui
 * @date 16/05/2018
 * <pre>
 *
 *
 *   这里实现了MeterBinder接口的bindTo方法，将要采集的指标注册到MeterRegistry
 *
 *
 *   curl -i http://localhost:8080/actuator/metrics/demo.count
 *
 *
 *
 *   ResourceBundleMessageSource should avoid ResourceBundle.Control on Jigsaw
 *
 *   spring 5.0.5 bug
 *
 *
 *
 * </pre>
 */
public class DemoMetrics implements MeterBinder {

    AtomicInteger count = new AtomicInteger(0);

    @Override
    public void bindTo(MeterRegistry meterRegistry) {
        Gauge.builder("demo.count", count, c -> c.incrementAndGet())
                .tags("host", "localhost")
                .description("demo of custom meter binder")
                .register(meterRegistry);
    }
}
