package com.mu.musmart;

import com.mu.musmart.domain.vo.ForumExceptionHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@MapperScan("com.mu.musmart.mapper")
@SpringBootApplication
public class MuSmartApplication implements WebMvcConfigurer, ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(MuSmartApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }

    /**
     * 解决swagger-ui访问 /doc.html 404问题
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(0, new ForumExceptionHandler());
    }
}
