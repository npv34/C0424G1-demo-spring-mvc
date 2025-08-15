package com.codegym.demo.configs;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebInit extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null; // No root context configuration
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebConfig.class}; // Use WebConfig for servlet context
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"}; // Map all requests to the DispatcherServlet
    }
}
