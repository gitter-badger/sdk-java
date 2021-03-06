package com.manywho.sdk.services.describe;

import com.github.fge.lambdas.Throwing;
import com.manywho.sdk.entities.describe.DescribeServiceInstall;
import com.manywho.sdk.entities.describe.DescribeServiceResponse;
import com.manywho.sdk.entities.describe.DescribeValue;
import com.manywho.sdk.entities.describe.DescribeValueCollection;
import com.manywho.sdk.entities.draw.elements.type.TypeElementBinding;
import com.manywho.sdk.entities.draw.elements.type.TypeElementBindingCollection;
import com.manywho.sdk.entities.draw.elements.type.TypeElementCollection;
import com.manywho.sdk.entities.draw.elements.type.TypeElementProperty;
import com.manywho.sdk.entities.draw.elements.type.TypeElementPropertyBinding;
import com.manywho.sdk.entities.draw.elements.type.TypeElementPropertyBindingCollection;
import com.manywho.sdk.entities.draw.elements.type.TypeElementPropertyCollection;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.CachedData;
import com.manywho.sdk.services.annotations.Action;
import com.manywho.sdk.services.annotations.ActionInput;
import com.manywho.sdk.services.annotations.ActionOutput;
import com.manywho.sdk.services.annotations.TypeElement;
import com.manywho.sdk.services.annotations.TypeProperty;
import com.manywho.sdk.services.describe.actions.AbstractAction;
import com.manywho.sdk.services.describe.actions.ActionCollection;
import com.manywho.sdk.services.describe.actions.DefaultAction;
import com.manywho.sdk.services.describe.types.AbstractType;
import com.manywho.sdk.services.types.TypeParser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractDescribeService implements DescribeService {
    @Override
    public boolean getProvidesAutoBinding() {
        return false;
    }

    @Override
    public boolean getProvidesDatabase() {
        return false;
    }

    @Override
    public boolean getProvidesFiles() {
        return false;
    }

    @Override
    public boolean getProvidesIdentity() {
        return false;
    }

    @Override
    public boolean getProvidesListening() {
        return false;
    }

    @Override
    public boolean getProvidesLogic() {
        return false;
    }

    @Override
    public boolean getProvidesNotifications() {
        return false;
    }

    @Override
    public boolean getProvidesSmartSave() {
        return false;
    }

    @Override
    public boolean getProvidesSocial() {
        return false;
    }

    @Override
    public boolean getProvidesSharing() {
        return false;
    }

    @Override
    public boolean getProvidesViews() {
        return false;
    }

    @Override
    public boolean getProvidesVoting() {
        return false;
    }

    @Override
    public ActionCollection createActions() throws IllegalAccessException, InstantiationException {
        ActionCollection actions = new ActionCollection();
        actions.addAll(buildActionsFromAbstractActions());
        actions.addAll(buildActionsFromAnnotatedActions());

        Collections.sort(actions);

        return actions;
    }

    @Override
    public DescribeServiceInstall createInstall() throws IllegalAccessException, InstantiationException {
        TypeElementCollection typeElements = new TypeElementCollection();
        typeElements.addAll(buildTypeElementsFromAbstractTypes());
        typeElements.addAll(buildTypeElementsFromAnnotatedTypes());

        Collections.sort(typeElements);

        return new DescribeServiceInstall(typeElements);
    }

    @Override
    public DescribeServiceResponse createResponse() throws Exception {
        return new DescribeServiceResponse() {{
            setCulture(AbstractDescribeService.this.createCulture());
            setConfigurationValues(AbstractDescribeService.this.createConfigurationValues());
            setProvidesAutoBinding(AbstractDescribeService.this.getProvidesAutoBinding());
            setProvidesDatabase(AbstractDescribeService.this.getProvidesDatabase());
            setProvidesFiles(AbstractDescribeService.this.getProvidesFiles());
            setProvidesIdentity(AbstractDescribeService.this.getProvidesIdentity());
            setProvidesListening(AbstractDescribeService.this.getProvidesListening());
            setProvidesLogic(AbstractDescribeService.this.getProvidesLogic());
            setProvidesNotifications(AbstractDescribeService.this.getProvidesNotifications());
            setProvidesSmartSave(AbstractDescribeService.this.getProvidesSmartSave());
            setProvidesSocial(AbstractDescribeService.this.getProvidesSocial());
            setProvidesSharing(AbstractDescribeService.this.getProvidesSharing());
            setProvidesViews(AbstractDescribeService.this.getProvidesViews());
            setProvidesVoting(AbstractDescribeService.this.getProvidesVoting());
            setActions(AbstractDescribeService.this.createActions());
            setInstall(AbstractDescribeService.this.createInstall());
        }};
    }

    private ActionCollection buildActionsFromAbstractActions() {
        final Set<Class<? extends AbstractAction>> actions = CachedData.reflections
                .getSubTypesOf(AbstractAction.class);

        if (CollectionUtils.isNotEmpty(actions)) {
            return actions.stream()
                    .map(Throwing.function(Class::newInstance))
                    .collect(Collectors.toCollection(ActionCollection::new));
        }

        return new ActionCollection();
    }

    private ActionCollection buildActionsFromAnnotatedActions() {
        final Set<Class<?>> actions = CachedData.reflections.getTypesAnnotatedWith(Action.class);

        if (CollectionUtils.isNotEmpty(actions)) {
            final Set<Field> annotatedInputs = CachedData.reflections.getFieldsAnnotatedWith(ActionInput.class);
            final Set<Field> annotatedOutputs = CachedData.reflections.getFieldsAnnotatedWith(ActionOutput.class);

            return actions.stream()
                    .map(action -> buildActionFromAnnotatedAction(action, annotatedInputs, annotatedOutputs))
                    .collect(Collectors.toCollection(ActionCollection::new));
        }

        return new ActionCollection();
    }

    private com.manywho.sdk.services.describe.actions.Action buildActionFromAnnotatedAction(Class<?> annotatedAction, Set<Field> annotatedInputs, Set<Field> annotatedOutputs) {
        Action action = annotatedAction.getAnnotation(Action.class);

        // Build the list of inputs for the action
        DescribeValueCollection inputs = annotatedInputs.stream()
                .filter(field -> field.getDeclaringClass().equals(annotatedAction))
                .map(Throwing.function(field -> createActionInputFromField(action, field)))
                .sorted()
                .collect(Collectors.toCollection(DescribeValueCollection::new));

        // Build the list of outputs for the action
        DescribeValueCollection outputs = annotatedOutputs.stream()
                .filter(field -> field.getDeclaringClass().equals(annotatedAction))
                .map(Throwing.function(field -> createActionOutputFromField(action, field)))
                .sorted()
                .collect(Collectors.toCollection(DescribeValueCollection::new));

        return new DefaultAction(action.uriPart(), action.name(), action.summary(), inputs, outputs);
    }

    private DescribeValue createActionInputFromField(Action action, Field field) throws Exception {
        ActionInput input = field.getAnnotation(ActionInput.class);

        return createActionValueFromField(action.name(), field, input.contentType(), input.name(), input.required(), input.referencedType());
    }

    private DescribeValue createActionOutputFromField(Action action, Field field) throws Exception {
        ActionOutput output = field.getAnnotation(ActionOutput.class);

        return createActionValueFromField(action.name(), field, output.contentType(), output.name(), output.required(), output.referencedType());
    }

    private DescribeValue createActionValueFromField(String actionName, Field field, ContentType contentType, String name, boolean required, Class<?> referencedType) throws Exception {
        DescribeValue input = new DescribeValue();
        input.setContentType(contentType);
        input.setDeveloperName(name);
        input.setRequired(required);

        if (contentType.equals(ContentType.Object) || contentType.equals(ContentType.List)) {
            input.setTypeElementDeveloperName(TypeParser.getReferencedTypeName(actionName, field, referencedType, name, contentType));
        }

        return input;
    }

    private TypeElementCollection buildTypeElementsFromAbstractTypes() {
        final Set<Class<? extends AbstractType>> types = CachedData.reflections
                .getSubTypesOf(AbstractType.class);

        // Loop over all the classes that extend AbstractType, instantiate, then add them into a TypeElementCollection
        if (CollectionUtils.isNotEmpty(types)) {
            return types.stream()
                    .map(Throwing.function(type -> (com.manywho.sdk.entities.draw.elements.type.TypeElement) type.newInstance()))
                    .collect(Collectors.toCollection(TypeElementCollection::new));
        }

        return new TypeElementCollection();
    }

    private TypeElementCollection buildTypeElementsFromAnnotatedTypes() {
        final Set<Class<?>> types = CachedData.reflections.getTypesAnnotatedWith(TypeElement.class);

        if (CollectionUtils.isNotEmpty(types)) {
            final Set<Field> annotatedProperties = CachedData.reflections.getFieldsAnnotatedWith(TypeProperty.class);

            // Build type elements for all the detected types that aren't in the SDK
            return types.stream()
                    .filter(type -> !type.getPackage().getName().startsWith("com.manywho.sdk"))
                    .map(type -> buildTypeElementFromAnnotatedType(type, annotatedProperties))
                    .collect(Collectors.toCollection(TypeElementCollection::new));
        }

        return new TypeElementCollection();
    }

    private com.manywho.sdk.entities.draw.elements.type.TypeElement buildTypeElementFromAnnotatedType(Class<?> annotatedType, Set<Field> annotatedProperties) {
        TypeElement typeElement = annotatedType.getAnnotation(TypeElement.class);

        // Build a list of ManyWho Properties created from the annotated fields in the type passed in
        TypeElementPropertyCollection properties = annotatedProperties.stream()
                .filter(field -> field.getDeclaringClass().equals(annotatedType))
                .map(Throwing.function(field -> createTypeElementProperty(typeElement.name(), field)))
                .sorted()
                .collect(Collectors.toCollection(TypeElementPropertyCollection::new));

        TypeElementPropertyBindingCollection propertyBindings = annotatedProperties.stream()
                .filter(field -> field.getDeclaringClass().equals(annotatedType))
                .map(field -> field.getAnnotation(TypeProperty.class))
                .filter(TypeProperty::bound)
                .map(property -> new TypeElementPropertyBinding(property.name(), property.name()))
                .sorted()
                .collect(Collectors.toCollection(TypeElementPropertyBindingCollection::new));

        // Create the default summary value, if one wasn't provided
        String typeElementSummary = typeElement.summary();
        if (StringUtils.isEmpty(typeElementSummary)) {
            typeElementSummary = "The " + typeElement.name() + " object structure";
        }

        TypeElementBindingCollection bindings = null;

        // Only add the binding if there are properties that are set to bound
        if (CollectionUtils.isNotEmpty(propertyBindings)) {
            bindings = new TypeElementBindingCollection();
            bindings.add(new TypeElementBinding(typeElement.name(), typeElementSummary, typeElement.name(), propertyBindings));
        }

        return new com.manywho.sdk.entities.draw.elements.type.TypeElement(typeElement.name(), typeElementSummary, properties, bindings);
    }

    /**
     * @param typeElementName the name of the type element the property is defined in
     * @param propertyField the field that the property is defined as in the type
     * @return a new property containing the values from the given annotations
     * @throws Exception when a referenced type could not be found
     */
    private TypeElementProperty createTypeElementProperty(String typeElementName, Field propertyField) throws Exception {
        TypeProperty property = propertyField.getAnnotation(TypeProperty.class);

        String referencedTypeName = null;

        // If the type property annotation is of Object or List, then we need to find the typeElementName of the referenced type
        if (property.contentType().equals(ContentType.Object) || property.contentType().equals(ContentType.List)) {
            referencedTypeName = TypeParser.getReferencedTypeName(typeElementName, propertyField, property);
        }

        // Return a new TypeElementProperty with the values from annotations
        return new TypeElementProperty(property.name(), property.contentType(), referencedTypeName);
    }


}
