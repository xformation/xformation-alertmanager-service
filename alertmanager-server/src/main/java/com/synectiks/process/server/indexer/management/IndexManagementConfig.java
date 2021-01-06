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
package com.synectiks.process.server.indexer.management;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class IndexManagementConfig {
    @JsonProperty("rotation_strategy")
    public abstract String rotationStrategy();

    @JsonProperty("retention_strategy")
    public abstract String retentionStrategy();

    @JsonCreator
    public static IndexManagementConfig create(@JsonProperty("rotation_strategy") String rotationStrategy,
                                               @JsonProperty("retention_strategy") String retentionStrategy) {
        return new AutoValue_IndexManagementConfig(rotationStrategy, retentionStrategy);
    }
}
