package edu.cit.olodin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    public WebConfig() {
        System.out.println("WebConfig loaded");
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File file = new File("../uploads");

        System.out.println("Serving uploads from: " + file.getAbsolutePath());

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + file.getAbsolutePath() + "/");
    }
}