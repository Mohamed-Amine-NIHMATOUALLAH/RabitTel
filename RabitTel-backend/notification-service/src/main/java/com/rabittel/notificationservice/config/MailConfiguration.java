package com.rabittel.notificationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Dedicated Thymeleaf engine for email rendering.
 *
 * <p>Uses {@link SpringTemplateEngine} (not the bare {@link org.thymeleaf.TemplateEngine})
 * so that OGNL and all Spring expression evaluators are correctly wired.
 * Named {@code emailTemplateEngine} to avoid conflicting with the default
 * Spring MVC {@code templateEngine} bean.</p>
 */
@Configuration
public class MailConfiguration {

    @Bean
    public ClassLoaderTemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);
        resolver.setOrder(1);
        resolver.setCheckExistence(true);
        return resolver;
    }

    @Bean("emailTemplateEngine")
    public SpringTemplateEngine emailTemplateEngine(
            ClassLoaderTemplateResolver emailTemplateResolver
    ) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(emailTemplateResolver);
        // Enable Spring EL so ${variable} expressions work
        engine.setEnableSpringELCompiler(true);
        return engine;
    }
}
