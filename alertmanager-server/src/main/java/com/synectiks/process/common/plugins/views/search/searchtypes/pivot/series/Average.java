/*
 * */
package com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.SeriesSpec;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.TypedBuilder;

import java.util.Optional;

@AutoValue
@JsonTypeName(Average.NAME)
@JsonDeserialize(builder = Average.Builder.class)
public abstract class Average implements SeriesSpec {
    public static final String NAME = "avg";
    @Override
    public abstract String type();

    @Override
    public abstract String id();

    @JsonProperty
    public abstract String field();

    public static Builder builder() {
        return new AutoValue_Average.Builder().type(NAME);
    }

    @AutoValue.Builder
    public abstract static class Builder extends TypedBuilder<Average, Builder> {
        @JsonCreator
        public static Builder create() { return Average.builder(); }

        @JsonProperty
        public abstract Builder id(String id);

        @JsonProperty
        public abstract Builder field(String field);

        abstract Optional<String> id();
        abstract String field();
        abstract Average autoBuild();

        public Average build() {
            if (!id().isPresent()) {
                id(NAME + "(" + field() + ")");
            }
            return autoBuild();
        }
    }
}
