package com.lemon.challenge;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .paths(PathSelectors.any())
                .apis(RequestHandlerSelectors.any())
                .build()
                .globalOperationParameters(operationParameters());
    }

    private List<Parameter> operationParameters() {
        List<Parameter> headers = new ArrayList<>();
        headers.add(new ParameterBuilder().name("user-id")
                .description("User ID")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(true).build());


        return headers;
    }
}
