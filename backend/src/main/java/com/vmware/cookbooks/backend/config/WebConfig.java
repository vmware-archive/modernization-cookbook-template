package com.vmware.cookbooks.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public WebMvcConfigurer configurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // Cache /css, /images, /webfonts for a while
                for (String directory : Arrays.asList("css", "images", "webfonts")) {
                    registry.addResourceHandler(String.format("/%s/**", directory))
                            .addResourceLocations(String.format("classpath:/static/%s/", directory))
                            .setCacheControl(CacheControl.maxAge(Duration.ofDays(7L)))
                            .resourceChain(true);
                }

                // Add IndexHtmlResolver for other locations. This makes /path/to/recipe (no slash at the end) work
                // in addition to /path/to/recipe/ (slash at the end).
                // Cache content for a short time only.
                registry.addResourceHandler("/**")
                        .addResourceLocations("classpath:/static/")
                        .setCacheControl(CacheControl.maxAge(Duration.ofMinutes(15L)))
                        .resourceChain(true)
                        .addResolver(new IndexHtmlResolver());
            }
        };
    }

    static class IndexHtmlResolver implements ResourceResolver {
        @Override
        public Resource resolveResource(HttpServletRequest request,
                                        String requestPath,
                                        List<? extends Resource> locations,
                                        ResourceResolverChain chain) {
            Resource exactMatch = chain.resolveResource(request, requestPath, locations);
            if (exactMatch != null) {
                return exactMatch;
            }
            return chain.resolveResource(request, requestPath + "/index.html", locations);
        }

        @Override
        public String resolveUrlPath(String resourcePath,
                                     List<? extends Resource> locations,
                                     ResourceResolverChain chain) {
            return null;
        }
    }
}
