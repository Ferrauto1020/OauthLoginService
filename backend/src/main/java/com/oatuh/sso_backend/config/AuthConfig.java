package com.oatuh.sso_backend.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
public class AuthConfig {
	
	
	

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
  
        http
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/google/**","/azure/**","/facebook/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2Login ->
                oauth2Login
                     // Pagina di login, se necessaria
                    .successHandler(new SimpleUrlAuthenticationSuccessHandler("/loading")) // Redirigi su /loading dopo il login
                    .failureHandler((request, response, exception) -> {
                        response.sendRedirect("/");
                    })
            )
                       
                .logout(log -> log
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .clearAuthentication(true)
                    )
                .exceptionHandling(customizer->customizer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .csrf(conf->
                		conf.disable())
                .httpBasic(httpBasic -> {})
              
                .exceptionHandling(customizer->customizer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.NOT_IMPLEMENTED)))
       
                ;
        return http.build();
    }


   
    
}