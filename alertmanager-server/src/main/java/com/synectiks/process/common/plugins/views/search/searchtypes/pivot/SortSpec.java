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
package com.synectiks.process.common.plugins.views.search.searchtypes.pivot;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = SortSpec.TYPE_FIELD,
        visible = true)
public interface SortSpec {
    enum Direction {
        Ascending,
        Descending
    }

    String TYPE_FIELD = "type";
    String FIELD_FIELD = "field";
    String FIELD_DIRECTION = "direction";

    @JsonProperty(TYPE_FIELD)
    String type();
    @JsonProperty(FIELD_FIELD)
    String field();
    @JsonProperty(FIELD_DIRECTION)
    Direction direction();
}
