package com.example.recipesapp.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * Registra HandlerMappingIntrospector solo si no existe ya en el contexto.
 * Esto evita fallos en entornos ligeros (p. ej. cuando la auto-config MVC no corre),
 * y no interfiere cuando Spring Boot ya definió el bean.
 */
@Configuration
public class MvcHandlerMappingIntrospectorRegistrar implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof GenericApplicationContext context
            && !context.containsBean("mvcHandlerMappingIntrospector")) {
            context.registerBean("mvcHandlerMappingIntrospector", HandlerMappingIntrospector.class);
        }
    }
}
