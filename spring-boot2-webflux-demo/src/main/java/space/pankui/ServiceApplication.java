package space.pankui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author pankui
 *
 * spring-boot-starter-webflux 依赖，是我们核心需要学习 webflux 的包，
 * 里面默认包含了 spring-boot-starter-reactor-netty 、spring 5 webflux 包。
 * 也就是说默认是通过 netty 启动的。
 *
 *
 * webflux 启动之后  浏览器 http://localhost:8080/hello
 *
 */
@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }
}
