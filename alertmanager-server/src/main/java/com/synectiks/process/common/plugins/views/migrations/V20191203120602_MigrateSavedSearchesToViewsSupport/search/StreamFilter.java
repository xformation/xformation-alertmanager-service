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
package com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.search;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Map;

@AutoValue
@JsonAutoDetect
abstract class StreamFilter {
    abstract String streamId();

    @JsonValue
    public Map<String, Object> value() {
        return ImmutableMap.of(
                "type", "or",
                "filters", ImmutableSet.of(
                        ImmutableMap.of(
                                "type", "stream",
                                "id", streamId()
                                )
                )
        );
    }

    public static StreamFilter create(String streamId) {
        return new AutoValue_StreamFilter(streamId);
    }
}
