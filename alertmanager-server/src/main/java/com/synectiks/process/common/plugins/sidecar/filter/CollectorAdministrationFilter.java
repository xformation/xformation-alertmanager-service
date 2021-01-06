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
import com.synectiks.process.common.plugins.sidecar.rest.models.Collector;
import com.synectiks.process.common.plugins.sidecar.rest.models.Sidecar;
import com.synectiks.process.common.plugins.sidecar.services.CollectorService;

import javax.inject.Inject;

public class CollectorAdministrationFilter implements AdministrationFilter {
    private final Collector collector;

    @Inject
    public CollectorAdministrationFilter(CollectorService collectorService,
                                         @Assisted String collectorId) {
        this.collector = collectorService.find(collectorId);
    }

    @Override
    public boolean test(Sidecar sidecar) {
        return collector.nodeOperatingSystem().equalsIgnoreCase(sidecar.nodeDetails().operatingSystem());
    }
}
