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
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.dashboardwidgets;

import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets.AreaVisualizationConfig;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets.Interpolation;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets.LineVisualizationConfig;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets.Pivot;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets.Series;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets.SortConfig;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets.TimeHistogramConfig;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets.ValueConfig;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets.VisualizationConfig;

import java.util.Optional;

abstract class WidgetConfigBase implements WidgetConfigWithTimeRange {
    static String TIMESTAMP_FIELD = "timestamp";

    Pivot valuesPivotForField(String field, int limit) {
        return Pivot.valuesBuilder()
                .field(field)
                .config(ValueConfig.ofLimit(limit))
                .build();
    }

    Pivot timestampPivot(String interval) {
        return Pivot.timeBuilder()
                .field(TIMESTAMP_FIELD)
                .config(TimeHistogramConfig.builder().interval(ApproximatedAutoIntervalFactory.of(interval, timerange())).build())
                .build();
    }

    Series countSeries() {
        return Series.buildFromString("count()").build();
    }

    SortConfig.Direction sortDirection(String sortOrder) {
        switch (sortOrder) {
            case "asc": return SortConfig.Direction.Ascending;
            case "desc": return SortConfig.Direction.Descending;
        }
        throw new RuntimeException("Unable to parse sort order: "  + sortOrder);
    }

    String mapStatsFunction(String function) {
        switch (function) {
            case "total": return "sum";
            case "mean": return "avg";
            case "std_deviation": return "stddev";
            case "cardinality": return "card";
            case "count":
            case "variance":
            case "min":
            case "max":
            case "sum":
                return function;
        }
        throw new RuntimeException("Unable to map statistical function: " + function);
    }

    String mapRendererToVisualization(String renderer) {
        switch (renderer) {
            case "bar":
            case "line":
            case "area":
                return renderer;
            case "scatterplot": return "scatter";
        }
        throw new RuntimeException("Unable to map renderer to visualization: " + renderer);
    }

    Optional<VisualizationConfig> createVisualizationConfig(String renderer, String interpolation) {
        switch (renderer) {
            case "line":
                return Optional.of(
                        LineVisualizationConfig.builder()
                                .interpolation(Interpolation.fromLegacyValue(interpolation))
                                .build()
                );
            case "area":
                return Optional.of(
                        AreaVisualizationConfig.builder()
                                .interpolation(Interpolation.fromLegacyValue(interpolation))
                                .build()
                );
        }
        return Optional.empty();
    }
}
