package com.example.ordinaMii.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    private final Path dishImagesDirectory;

    public StaticResourceConfig(
            @Value("${app.uploads.dishes-directory:uploads/dishes}")
            String directory) {

        this.dishImagesDirectory = Paths.get(directory)
                .toAbsolutePath()
                .normalize();
    }

    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry) {

        String resourceLocation =
                dishImagesDirectory.toUri().toString();

        if (!resourceLocation.endsWith("/")) {
            resourceLocation += "/";
        }

        registry
                .addResourceHandler("/uploads/dishes/**")
                .addResourceLocations(resourceLocation);
    }
}