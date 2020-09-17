package tech.eportfolio.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tech.eportfolio.server.common.constant.SecurityConstant;
import tech.eportfolio.server.interceptor.PathUsernameInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PathUsernameInterceptor());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE", "PATCH")
//            .allowedHeaders("X-Auth-Token", "Content-Type")
                .exposedHeaders(SecurityConstant.JWT_TOKEN_HEADER)
                .allowCredentials(false)
                .maxAge(4800);
    }
}
