package com.oatuh.sso_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import java.util.Arrays;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class WebConfig {


   private static final Long MAX_AGE = 3600L;
   @Bean
   public CorsFilter corsFilter() {
       UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
       CorsConfiguration config = new CorsConfiguration();
       config.setAllowCredentials(true);
       config.addAllowedOrigin("http://localhost:4200");
       config.setAllowedHeaders(Arrays.asList(
               HttpHeaders.AUTHORIZATION,
               HttpHeaders.CONTENT_TYPE,
               HttpHeaders.ACCEPT));
       config.setAllowedMethods(Arrays.asList(
               HttpMethod.GET.name(),
               HttpMethod.POST.name(),
               HttpMethod.PUT.name(),
               HttpMethod.DELETE.name()));
       config.setMaxAge(MAX_AGE);
      
       source.registerCorsConfiguration("/**", config);
       CorsFilter bean = new CorsFilter(source);
       return bean;
   }
}
