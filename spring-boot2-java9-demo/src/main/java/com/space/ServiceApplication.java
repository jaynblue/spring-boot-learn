package com.space;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author pankui
 */
@SpringBootApplication
public class ServiceApplication extends SpringBootServletInitializer{

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ServiceApplication.class);
    }
}
