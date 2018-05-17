package space.pankui.controller;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pankui
 * @date 17/05/2018
 * <pre>
 *
 *   https://github.com/ajaegle/boot2-metrics/blob/master/src/main/java/de/ajaegle/sample/monitoring/boot2metrics/SomeController.java
 *
 *   统计这个接口访问次数
 *
 *   访问这个接口
 *
 *  http://localhost:8080/hellos
 *
 *   查看访问次数
 *
 *
 *   http://localhost:8080/actuator/metrics/hellos
 *
 *   其实在 /metrics 里面包含所有注册的监控
 *
 * </pre>
 */

@RestController
public class SomeController {

    private Counter hellos = Metrics.counter("hellos");

    @Timed
    @GetMapping("/hellos")
    public Map<String,String> index() {
        hellos.increment();
        return new HashMap<String, String>() {{ put("hello", "world"); }};
    }


}
