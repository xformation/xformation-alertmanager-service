/*
 * Copyright (C) 2020 Graylog, Inc.
 *
 
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program. If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.synectiks.process.common.events.contentpack.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.events.notifications.EventNotificationConfig;
import com.synectiks.process.common.events.notifications.types.EmailEventNotificationConfig;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;

import java.util.Map;
import java.util.Set;

@AutoValue
@JsonTypeName(EmailEventNotificationConfigEntity.TYPE_NAME)
@JsonDeserialize(builder = EmailEventNotificationConfigEntity.Builder.class)
public abstract class EmailEventNotificationConfigEntity implements EventNotificationConfigEntity {

    public static final String TYPE_NAME = "email-notification-v1";
    private static final String FIELD_SENDER = "sender";
    private static final String FIELD_SUBJECT = "subject";
    private static final String FIELD_BODY_TEMPLATE = "body_template";
    private static final String FIELD_EMAIL_RECIPIENTS = "email_recipients";
    private static final String FIELD_USER_RECIPIENTS = "user_recipients";

    @JsonProperty(FIELD_SENDER)
    public abstract ValueReference sender();

    @JsonProperty(FIELD_SUBJECT)
    public abstract ValueReference subject();

    @JsonProperty(FIELD_BODY_TEMPLATE)
    public abstract ValueReference bodyTemplate();

    @JsonProperty(FIELD_EMAIL_RECIPIENTS)
    public abstract Set<String> emailRecipients();

    @JsonProperty(FIELD_USER_RECIPIENTS)
    public abstract Set<String> userRecipients();

    public static Builder builder() {
        return Builder.create();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public static abstract class Builder implements EventNotificationConfigEntity.Builder<Builder> {

        @JsonCreator
        public static Builder create() {
            return new AutoValue_EmailEventNotificationConfigEntity.Builder()
                    .type(TYPE_NAME);
        }

        @JsonProperty(FIELD_SENDER)
        public abstract Builder sender(ValueReference sender);

        @JsonProperty(FIELD_SUBJECT)
        public abstract Builder subject(ValueReference subject);

        @JsonProperty(FIELD_BODY_TEMPLATE)
        public abstract Builder bodyTemplate(ValueReference bodyTemplate);

        @JsonProperty(FIELD_EMAIL_RECIPIENTS)
        public abstract Builder emailRecipients(Set<String> emailRecipients);

        @JsonProperty(FIELD_USER_RECIPIENTS)
        public abstract Builder userRecipients(Set<String> userRecipients);

        public abstract EmailEventNotificationConfigEntity build();
    }

    @Override
    public EventNotificationConfig toNativeEntity(Map<String, ValueReference> parameters, Map<EntityDescriptor, Object> nativeEntities) {
        return EmailEventNotificationConfig.builder()
                .sender(sender().asString(parameters))
                .subject(subject().asString(parameters))
                .bodyTemplate(bodyTemplate().asString())
                .emailRecipients(emailRecipients())
                .userRecipients(userRecipients())
                .build();
    }
}
