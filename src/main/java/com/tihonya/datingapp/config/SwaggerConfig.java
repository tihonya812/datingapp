package com.tihonya.datingapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenapi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dating App API")  // Название API
                        .version("1.0")  // Версия API
                        .description("API для веб-приложения знакомств"));  // Описание API
    }
}
