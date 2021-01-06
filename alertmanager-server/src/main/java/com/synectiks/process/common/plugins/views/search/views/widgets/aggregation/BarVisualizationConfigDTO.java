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
package com.synectiks.process.common.plugins.views.search.views.widgets.aggregation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonTypeName(BarVisualizationConfigDTO.NAME)
@JsonDeserialize(builder = BarVisualizationConfigDTO.Builder.class)
public abstract class BarVisualizationConfigDTO implements VisualizationConfigDTO {
    public static final String NAME = "bar";
    private static final String FIELD_BAR_MODE = "barmode";

    public enum BarMode {
        stack,
        overlay,
        group,
        relative
    };

    @JsonProperty
    public abstract BarMode barmode();

    @AutoValue.Builder
    public abstract static class Builder {

        @JsonProperty(FIELD_BAR_MODE)
        public abstract Builder barmode(BarMode barMode);

        public abstract BarVisualizationConfigDTO build();

        @JsonCreator
        public static Builder builder() {
            return new AutoValue_BarVisualizationConfigDTO.Builder();
        }
    }
}
