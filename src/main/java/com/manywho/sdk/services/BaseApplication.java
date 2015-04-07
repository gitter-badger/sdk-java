package com.manywho.sdk.services;

import com.manywho.sdk.services.features.BodyReaderFeature;
import com.manywho.sdk.services.listeners.ReflectionListener;
import com.manywho.sdk.services.providers.ObjectMapperProvider;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;

public class BaseApplication extends ResourceConfig {
    public ResourceConfig registerSdk() {
        return packages("com.manywho.sdk.services")
                .register(new ServiceBinder())
                .register(BodyReaderFeature.class)
                .register(ReflectionListener.class)
                .register(LoggingFilter.class)
                .register(ObjectMapperProvider.class);
    }
}
