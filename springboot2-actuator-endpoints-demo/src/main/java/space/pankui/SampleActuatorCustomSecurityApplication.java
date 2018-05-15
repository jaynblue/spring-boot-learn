package space.pankui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author pankui
 * @date 15/05/2018
 * <pre>
 *
 *      如果开启所有的监控，但是却没有，你需要执行下 gradle build
 *
 *      或者直接使用IDE 执行下就可以了。我弄了一天  执行 build 就可以了。囧
 *
 *      使用 run Dashboard 运行
 *
 * </pre>
 */

@SpringBootApplication
public class SampleActuatorCustomSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleActuatorCustomSecurityApplication.class);
    }
}
