package com.colin.bh.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 2024年06月06日18:31
 */
@Component
public class MvcConfig implements WebMvcConfigurer {

    @Value("${my.project.user.head-img.resource-location}")
    private String headImgResourceLocation;

    @Value("${my.project.user.head-img.resource-handler}")
    private String headImgResourceHandler;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry
                .addResourceHandler(headImgResourceHandler + "**")
                .addResourceLocations("file:///" + headImgResourceLocation);
    }
}
