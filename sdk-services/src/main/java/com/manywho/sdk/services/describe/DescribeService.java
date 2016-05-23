package com.manywho.sdk.services.describe;

import com.google.common.collect.Lists;
import com.manywho.sdk.api.describe.DescribeValue;
import com.manywho.sdk.services.actions.ActionRepository;
import com.manywho.sdk.services.configuration.Configuration;
import com.manywho.sdk.services.configuration.ConfigurationRepository;
import com.manywho.sdk.services.controllers.AbstractDataController;
import com.manywho.sdk.services.controllers.AbstractFileController;
import com.manywho.sdk.services.controllers.AbstractIdentityController;
import com.manywho.sdk.services.controllers.AbstractListenerController;
import com.manywho.sdk.services.controllers.AbstractSocialController;
import com.manywho.sdk.services.database.Database;
import com.manywho.sdk.services.types.TypeRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class DescribeService {
    private final ActionRepository actionRepository;
    private final ConfigurationRepository configurationRepository;
    private final DescribeRepository describeRepository;
    private final TypeRepository typeRepository;

    @Inject
    public DescribeService(ActionRepository actionRepository, ConfigurationRepository configurationRepository, DescribeRepository describeRepository, TypeRepository typeRepository) {
        this.actionRepository = actionRepository;
        this.configurationRepository = configurationRepository;
        this.describeRepository = describeRepository;
        this.typeRepository = typeRepository;
    }

    public boolean anyActionsDefined() {
        return !actionRepository.getActions().isEmpty();
    }

    public boolean anyConfigurationValuesExist() {
        return describeRepository.doFieldsAnnotatedWithExist(Configuration.Value.class);
    }

    public boolean anyDataControllersExist() {
        return describeRepository.doSubtypesOfExist(AbstractDataController.class) || describeRepository.doSubtypesOfExist(Database.class);
    }

    public boolean anyFileControllersExist() {
        return describeRepository.doSubtypesOfExist(AbstractFileController.class);
    }

    public boolean anyIdentityControllersExist() {
        return describeRepository.doSubtypesOfExist(AbstractIdentityController.class);
    }

    public boolean anyListenerControllersExist() {
        return describeRepository.doSubtypesOfExist(AbstractListenerController.class);
    }

    public boolean anySocialControllersExist() {
        return describeRepository.doSubtypesOfExist(AbstractSocialController.class);
    }

    public boolean anyTypesDefined() {
        return !typeRepository.getTypeElements().isEmpty();
    }

    public List<DescribeValue> createConfigurationValues() {
        List<DescribeValue> values = configurationRepository.getConfigurationValues()
                .stream()
                .map(klass -> klass.getAnnotation(Configuration.Value.class))
                .map(annotation -> new DescribeValue(annotation.name(), annotation.contentType(), annotation.required()))
                .collect(Collectors.toList());

        if (values.isEmpty()) {
            return Lists.newArrayList();
        }

        return values;
    }
}
