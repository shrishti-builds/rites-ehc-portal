package com.rites.ehc;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> type) {
        if (context == null) {
            throw new IllegalStateException("Spring context has not been initialized yet.");
        }
        return context.getBean(type);
    }
}
