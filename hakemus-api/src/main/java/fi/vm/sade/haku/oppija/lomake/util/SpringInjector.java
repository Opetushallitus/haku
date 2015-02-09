package fi.vm.sade.haku.oppija.lomake.util;

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
        if (null == CONTEXT)
            return;
        CONTEXT.getAutowireCapableBeanFactory().autowireBean(object);
    }
}
