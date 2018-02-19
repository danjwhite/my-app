package com.example.myapp;

import com.example.myapp.converter.RoleConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@ComponentScan("com.example.myapp")
public class Application extends WebMvcConfigurerAdapter {

    @Autowired
    private RoleConverter roleConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(roleConverter);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}