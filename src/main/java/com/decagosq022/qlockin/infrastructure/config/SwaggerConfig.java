package com.decagosq022.qlockin.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI createOpenAPIConfig() {
		return new OpenAPI()
				// General API information
				.info(new Info()
						.title("Qlock-In API")
						.version("1.0")
						.description("API documentation for Qlock-In application"))
				// Security schemes component
				.components(new Components()
						.addSecuritySchemes("bearer-jwt", new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")
								.in(SecurityScheme.In.HEADER)
								.name("Authorization")
						))
				// Global security requirement
				.addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
	}
}