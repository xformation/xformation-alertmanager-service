/*
 * */
package com.synectiks.process.common.events.processor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.graph.MutableGraph;
import com.synectiks.process.common.events.contentpack.entities.EventDefinitionEntity;
import com.synectiks.process.common.events.contentpack.entities.EventNotificationHandlerConfigEntity;
import com.synectiks.process.common.events.contentpack.entities.EventProcessorConfigEntity;
import com.synectiks.process.common.events.fields.EventFieldSpec;
import com.synectiks.process.common.events.notifications.EventNotificationHandler;
import com.synectiks.process.common.events.notifications.EventNotificationSettings;
import com.synectiks.process.common.events.processor.storage.EventStorageHandler;
import com.synectiks.process.common.events.processor.storage.PersistToStreamsStorageHandler;
import com.synectiks.process.server.contentpacks.ContentPackable;
import com.synectiks.process.server.contentpacks.EntityDescriptorIds;
import com.synectiks.process.server.contentpacks.model.ModelId;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.rest.ValidationResult;

import org.graylog.autovalue.WithBeanGetter;
import org.mongojack.Id;
import org.mongojack.ObjectId;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AutoValue
@JsonAutoDetect
@JsonDeserialize(builder = EventDefinitionDto.Builder.class)
@WithBeanGetter
public abstract class EventDefinitionDto implements EventDefinition, ContentPackable<EventDefinitionEntity> {
    public static final String FIELD_ID = "id";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_NOTIFICATIONS = "notifications";
    private static final String FIELD_PRIORITY = "priority";
    private static final String FIELD_ALERT = "alert";
    private static final String FIELD_CONFIG = "config";
    private static final String FIELD_FIELD_SPEC = "field_spec";
    private static final String FIELD_KEY_SPEC = "key_spec";
    private static final String FIELD_NOTIFICATION_SETTINGS = "notification_settings";
    private static final String FIELD_STORAGE = "storage";

    @Override
    @Id
    @ObjectId
    @Nullable
    @JsonProperty(FIELD_ID)
    public abstract String id();

    @Override
    @JsonProperty(FIELD_TITLE)
    public abstract String title();

    @Override
    @JsonProperty(FIELD_DESCRIPTION)
    public abstract String description();

    @Override
    @JsonProperty(FIELD_PRIORITY)
    public abstract int priority();

    @Override
    @JsonProperty(FIELD_ALERT)
    public abstract boolean alert();

    @Override
    @JsonProperty(FIELD_CONFIG)
    public abstract EventProcessorConfig config();

    @Override
    @JsonProperty(FIELD_FIELD_SPEC)
    public abstract ImmutableMap<String, EventFieldSpec> fieldSpec();

    @Override
    @JsonProperty(FIELD_KEY_SPEC)
    public abstract ImmutableList<String> keySpec();

    @Override
    @JsonProperty(FIELD_NOTIFICATION_SETTINGS)
    public abstract EventNotificationSettings notificationSettings();

    @Override
    @JsonProperty(FIELD_NOTIFICATIONS)
    public abstract ImmutableList<EventNotificationHandler.Config> notifications();

    @Override
    @JsonProperty(FIELD_STORAGE)
    public abstract ImmutableList<EventStorageHandler.Config> storage();

    public static Builder builder() {
        return Builder.create();
    }

    public abstract Builder toBuilder();

    @JsonIgnore
    public ValidationResult validate() {
        final ValidationResult validation = new ValidationResult();

        if (title().isEmpty()) {
            validation.addError(FIELD_TITLE, "Event Definition title cannot be empty.");
        }

        try {
            validation.addAll(config().validate());
        } catch (UnsupportedOperationException e) {
            validation.addError(FIELD_CONFIG, "Event Definition config type cannot be empty.");
        }

        for (Map.Entry<String, EventFieldSpec> fieldSpecEntry : fieldSpec().entrySet()) {
            final String fieldName = fieldSpecEntry.getKey();
            if (!Message.validKey(fieldName)) {
                validation.addError(FIELD_FIELD_SPEC,
                    "Event Definition field_spec contains invalid message field \"" + fieldName + "\"");
            }
        }

        if (keySpec().stream().anyMatch(key -> !fieldSpec().containsKey(key))) {
            validation.addError(FIELD_KEY_SPEC, "Event Definition key_spec can only contain fields defined in field_spec.");
        }

        return validation;
    }

    @AutoValue.Builder
    public static abstract class Builder {
        @JsonCreator
        public static Builder create() {
            return new AutoValue_EventDefinitionDto.Builder()
                    .fieldSpec(ImmutableMap.of())
                    .notifications(ImmutableList.of())
                    .storage(ImmutableList.of());
        }

        @Id
        @ObjectId
        @JsonProperty(FIELD_ID)
        public abstract Builder id(String id);

        @JsonProperty(FIELD_TITLE)
        public abstract Builder title(String title);

        @JsonProperty(FIELD_DESCRIPTION)
        public abstract Builder description(String description);

        @JsonProperty(FIELD_PRIORITY)
        public abstract Builder priority(int priority);

        @JsonProperty(FIELD_ALERT)
        public abstract Builder alert(boolean alert);

        @JsonProperty(FIELD_CONFIG)
        public abstract Builder config(EventProcessorConfig config);

        @JsonProperty(FIELD_FIELD_SPEC)
        public abstract Builder fieldSpec(ImmutableMap<String, EventFieldSpec> fieldSpec);

        @JsonProperty(FIELD_KEY_SPEC)
        public abstract Builder keySpec(ImmutableList<String> keySpec);

        @JsonProperty(FIELD_NOTIFICATION_SETTINGS)
        public abstract Builder notificationSettings(EventNotificationSettings notificationSettings);

        @JsonProperty(FIELD_NOTIFICATIONS)
        public abstract Builder notifications(ImmutableList<EventNotificationHandler.Config> notifications);

        @JsonProperty(FIELD_STORAGE)
        public abstract Builder storage(ImmutableList<EventStorageHandler.Config> storageHandlers);

        abstract EventDefinitionDto autoBuild();

        public EventDefinitionDto build() {
            final EventDefinitionDto dto = autoBuild();
            final PersistToStreamsStorageHandler.Config withDefaultEventsStream = PersistToStreamsStorageHandler.Config.createWithDefaultEventsStream();

            if (dto.storage().stream().noneMatch(withDefaultEventsStream::equals)) {
                final List<EventStorageHandler.Config> handlersWithoutPersistToStreams = dto.storage().stream()
                        // We don't allow custom persist-to-streams handlers at the moment
                        .filter(handler -> !PersistToStreamsStorageHandler.Config.TYPE_NAME.equals(handler.type()))
                        .collect(Collectors.toList());

                return dto.toBuilder()
                        // Right now we always want to persist events into the default events stream
                        .storage(ImmutableList.<EventStorageHandler.Config>builder()
                                .addAll(handlersWithoutPersistToStreams)
                                .add(withDefaultEventsStream)
                                .build())
                        .build();
            }

            return dto;
        }
    }

    public EventDefinitionEntity toContentPackEntity(EntityDescriptorIds entityDescriptorIds) {
        final EventProcessorConfig config = config();
        final EventProcessorConfigEntity eventProcessorConfigEntity = config.toContentPackEntity(entityDescriptorIds);
        final ImmutableList<EventNotificationHandlerConfigEntity> notificationList = ImmutableList.copyOf(
            notifications().stream()
                .map(notification -> notification.toContentPackEntity(entityDescriptorIds))
                .collect(Collectors.toList()));

        return EventDefinitionEntity.builder()
            .title(ValueReference.of(title()))
            .description(ValueReference.of(description()))
            .priority(ValueReference.of(priority()))
            .alert(ValueReference.of(alert()))
            .config(eventProcessorConfigEntity)
            .notifications(notificationList)
            .notificationSettings(notificationSettings())
            .fieldSpec(fieldSpec())
            .keySpec(keySpec())
            .storage(storage())
            .build();
    }

    @Override
    public void resolveNativeEntity(EntityDescriptor entityDescriptor, MutableGraph<EntityDescriptor> mutableGraph) {
        notifications().stream().map(EventNotificationHandler.Config::notificationId)
            .forEach(id -> {
                    final EntityDescriptor depNotification = EntityDescriptor.builder()
                        .id(ModelId.of(id))
                        .type(ModelTypes.NOTIFICATION_V1)
                        .build();
                    mutableGraph.putEdge(entityDescriptor, depNotification);
                });
        config().resolveNativeEntity(entityDescriptor, mutableGraph);
    }
}
