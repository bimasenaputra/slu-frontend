package id.ac.ui.cs.advprog.frontend.config;

import id.ac.ui.cs.advprog.frontend.dto.JWTToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;


@Configuration
public class SessionConfig {

    @Bean
    @SessionScope
    public JWTToken sessionToken() { return new JWTToken();}
}