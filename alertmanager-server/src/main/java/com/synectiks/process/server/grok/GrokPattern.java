/*
 * */
package com.synectiks.process.server.grok;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;
import org.mongojack.Id;
import org.mongojack.ObjectId;

import javax.annotation.Nullable;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class GrokPattern {

    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_PATTERN = "pattern";
    public static final String FIELD_CONTENT_PACK = "content_pack";

    @JsonProperty(FIELD_ID)
    @Nullable
    @Id
    @ObjectId
    public abstract String id();

    @JsonProperty
    public abstract String name();

    @JsonProperty
    public abstract String pattern();

    @JsonProperty
    @Nullable
    public abstract String contentPack();

    @JsonCreator
    public static GrokPattern create(@Id @ObjectId @JsonProperty("_id") @Nullable String id,
                                     @JsonProperty(FIELD_NAME) String name,
                                     @JsonProperty(FIELD_PATTERN) String pattern,
                                     @JsonProperty(FIELD_CONTENT_PACK) @Nullable String contentPack) {
        return builder()
                .id(id)
                .name(name)
                .pattern(pattern)
                .contentPack(contentPack)
                .build();
    }

    public static GrokPattern create(String name, String pattern) {
        return create(null, name, pattern, null);
    }

    public static Builder builder() {
        return new AutoValue_GrokPattern.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(String id);

        public abstract Builder name(String name);

        public abstract Builder pattern(String pattern);

        public abstract Builder contentPack(String contentPack);

        public abstract GrokPattern build();
    }
}
