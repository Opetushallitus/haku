package fi.vm.sade.haku.oppija.lomake.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public final class SpringInjector implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;
    private static boolean inTestMode;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        CONTEXT = context;
    }

    public static void injectSpringDependencies(Object object) {
        if (inTestMode && null == CONTEXT)
            return;
        CONTEXT.getAutowireCapableBeanFactory().autowireBean(object);
    }

    public static void setTestMode(boolean testMode){
        inTestMode = testMode;
    }
}
