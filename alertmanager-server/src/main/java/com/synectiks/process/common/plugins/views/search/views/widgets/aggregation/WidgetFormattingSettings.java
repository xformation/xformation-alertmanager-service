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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.List;

@AutoValue
@JsonDeserialize(builder = WidgetFormattingSettings.Builder.class)
public abstract class WidgetFormattingSettings {
    private static final String FIELD_CHART_COLORS = "chart_colors";

    @JsonProperty(FIELD_CHART_COLORS)
    public abstract List<ChartColorMapping> chartColors();

    @AutoValue.Builder
    public static abstract class Builder {
        @JsonProperty(FIELD_CHART_COLORS)
        public abstract Builder chartColors(List<ChartColorMapping> chartColors);

        public abstract WidgetFormattingSettings build();

        @JsonCreator
        static Builder builder() {
            return new AutoValue_WidgetFormattingSettings.Builder()
                    .chartColors(Collections.emptyList());
        }
    }
}