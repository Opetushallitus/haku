package fi.vm.sade.hakutest;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan(basePackages = {"fi.vm.sade.haku"})
@PropertySource(value = {"config/it/haku.properties", "config/it/ext.properties", "haku-test.properties"})
@Profile("it")
@ImportResource("/spring/integration-test-mock-context.xml")
public class ApiIntegrationTestSpringConfiguration {
    @Bean
    public static PropertySourcesPlaceholderConfigurer enablePlaceholderReplacement() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public static AnnotationConfigApplicationContext createApplicationContext() {
        final AnnotationConfigApplicationContext appContext = new AnnotationConfigApplicationContext();
        appContext.getEnvironment().setActiveProfiles("it");
        appContext.register(ApiIntegrationTestSpringConfiguration.class);
        appContext.refresh();
        return appContext;
    }
}
