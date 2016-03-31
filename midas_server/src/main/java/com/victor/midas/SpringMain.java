package com.victor.midas;


import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

/**
 * used to launch restful services
 */
@ComponentScan
@EnableAutoConfiguration
@ImportResource("classpath:midas-content.xml")
@PropertySource("classpath:/local.properties")
public class SpringMain {

    private static final Logger logger = Logger.getLogger(SpringMain.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringMain.class, args);
    }
}