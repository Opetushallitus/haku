package fi.vm.sade.haku.oppija.configuration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringInjector implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        CONTEXT = context;
    }

    public static void injectSpringDependencies(Object object) {
        CONTEXT.getAutowireCapableBeanFactory().autowireBean(object);
    }
}
