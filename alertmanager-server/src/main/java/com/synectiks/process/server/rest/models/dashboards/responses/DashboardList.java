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
package com.synectiks.process.server.rest.models.dashboards.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.List;
import java.util.Map;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class DashboardList {
    @JsonProperty
    public abstract int total();

    @JsonProperty
    public abstract List<Map<String, Object>> dashboards();

    public static DashboardList create(int total, List<Map<String, Object>> dashboards) {
        return new AutoValue_DashboardList(total, dashboards);
    }
}
