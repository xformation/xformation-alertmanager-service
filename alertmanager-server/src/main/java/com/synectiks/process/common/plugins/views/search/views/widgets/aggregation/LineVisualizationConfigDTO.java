/*
 * Copyright (C) 2020 Graylog, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
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
package com.synectiks.process.common.plugins.views.search.views.widgets.aggregation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonTypeName(LineVisualizationConfigDTO.NAME)
@JsonDeserialize(builder = LineVisualizationConfigDTO.Builder.class)
public abstract class LineVisualizationConfigDTO implements VisualizationConfigDTO {
    public static final String NAME = "line";
    private static final String FIELD_INTERPOLATION = "interpolation";

    @JsonProperty(FIELD_INTERPOLATION)
    public abstract Interpolation interpolation();

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonProperty(FIELD_INTERPOLATION)
        public abstract Builder interpolation(Interpolation interpolation);

        public abstract LineVisualizationConfigDTO build();

        @JsonCreator
        public static Builder builder() {
            return new AutoValue_LineVisualizationConfigDTO.Builder()
                    .interpolation(Interpolation.defaultValue());
        }
    }
}