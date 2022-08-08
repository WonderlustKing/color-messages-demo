package com.chrisb.colors.prj.demo.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.function.Predicate;

import static springfox.documentation.builders.PathSelectors.regex;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket postsApi() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("public-api")
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .select().paths(postPaths()).build();
    }

    private Predicate<String> postPaths() {
        return regex("/api/colors.*");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Colors API")
                .description("Colors API reference for developers")
                .termsOfServiceUrl("http://google.com")
                .license("Foo License")
                .licenseUrl("john@doe.com")
                .version("1.0").build();
    }

}
