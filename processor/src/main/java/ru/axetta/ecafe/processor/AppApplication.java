package ru.axetta.ecafe.processor;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;


@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableConfigurationProperties
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@ServletComponentScan
public class AppApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(AppApplication.class)
                //.bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
