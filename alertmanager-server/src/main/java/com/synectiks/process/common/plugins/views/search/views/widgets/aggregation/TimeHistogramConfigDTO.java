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
@JsonTypeName(TimeHistogramConfigDTO.NAME)
@JsonDeserialize(builder = TimeHistogramConfigDTO.Builder.class)
public abstract class TimeHistogramConfigDTO implements PivotConfigDTO {
    public static final String NAME = "time";
    static final String FIELD_INTERVAL = "interval";

    @JsonProperty(FIELD_INTERVAL)
    public abstract IntervalDTO interval();

    public static Builder builder() {
        return new AutoValue_TimeHistogramConfigDTO.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonProperty(FIELD_INTERVAL)
        public abstract Builder interval(IntervalDTO interval);

        public abstract TimeHistogramConfigDTO build();

        @JsonCreator
        public static Builder builder() {
            return new AutoValue_TimeHistogramConfigDTO.Builder();
        }
    }
}
