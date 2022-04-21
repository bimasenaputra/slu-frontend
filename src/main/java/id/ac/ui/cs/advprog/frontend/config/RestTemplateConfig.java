package id.ac.ui.cs.advprog.frontend.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class RestTemplateConfig {

    @Bean
    @DependsOn(value = {"customRestTemplateCustomizer"})
    public RestTemplateBuilder restTemplateBuilder()
    {
        return new RestTemplateBuilder(customRestTemplateCustomizer());
    }

    @Bean
    public CustomRestTemplateCustomizer customRestTemplateCustomizer()
    {
        return new CustomRestTemplateCustomizer();
    }
}