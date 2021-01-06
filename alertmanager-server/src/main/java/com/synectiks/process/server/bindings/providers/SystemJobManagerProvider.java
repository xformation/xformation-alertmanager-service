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
package com.synectiks.process.server.bindings.providers;

import com.codahale.metrics.MetricRegistry;
import com.synectiks.process.server.shared.system.activities.ActivityWriter;
import com.synectiks.process.server.system.jobs.SystemJobManager;

import javax.inject.Inject;
import javax.inject.Provider;

public class SystemJobManagerProvider implements Provider<SystemJobManager> {
    private static SystemJobManager systemJobManager = null;

    @Inject
    public SystemJobManagerProvider(ActivityWriter activityWriter, MetricRegistry metricRegistry) {
        if (systemJobManager == null)
            systemJobManager = new SystemJobManager(activityWriter, metricRegistry);
    }

    @Override
    public SystemJobManager get() {
        return systemJobManager;
    }
}
