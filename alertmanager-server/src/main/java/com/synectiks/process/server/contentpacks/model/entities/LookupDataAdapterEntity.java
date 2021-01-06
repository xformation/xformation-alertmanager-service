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
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;
import com.synectiks.process.server.contentpacks.model.entities.references.Reference;
import com.synectiks.process.server.contentpacks.model.entities.references.ReferenceMap;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;

import java.util.Map;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class LookupDataAdapterEntity {
    @JsonProperty("name")
    public abstract ValueReference name();

    @JsonProperty("title")
    public abstract ValueReference title();

    @JsonProperty("description")
    public abstract ValueReference description();

    @JsonProperty("configuration")
    public abstract ReferenceMap configuration();

    @JsonCreator
    public static LookupDataAdapterEntity create(
            @JsonProperty("name") ValueReference name,
            @JsonProperty("title") ValueReference title,
            @JsonProperty("description") ValueReference description,
            @JsonProperty("configuration") ReferenceMap configuration) {
        return new AutoValue_LookupDataAdapterEntity(name, title, description, configuration);
    }
}