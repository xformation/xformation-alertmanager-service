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
package com.synectiks.process.common.plugins.sidecar.filter;

import com.google.inject.assistedinject.Assisted;
import com.synectiks.process.common.plugins.sidecar.rest.models.Sidecar;
import com.synectiks.process.common.plugins.sidecar.rest.requests.ConfigurationAssignment;

import javax.inject.Inject;
import java.util.List;

public class ConfigurationAdministrationFilter implements AdministrationFilter {
    private final String configurationId;

    @Inject
    public ConfigurationAdministrationFilter(@Assisted String configurationId) {
        this.configurationId = configurationId;
    }

    @Override
    public boolean test(Sidecar sidecar) {
        final List<ConfigurationAssignment> assignments = sidecar.assignments();
        if (assignments == null) {
            return false;
        }
        return assignments.stream().anyMatch(assignment -> assignment.configurationId().equals(configurationId));
    }
}
