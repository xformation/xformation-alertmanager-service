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

import com.synectiks.process.common.plugins.sidecar.rest.models.Sidecar;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;

import java.util.function.Predicate;

public class ActiveSidecarFilter implements Predicate<Sidecar> {
    private final Period timeoutPeriod;

    public ActiveSidecarFilter(Period timeoutPeriod) {
        this.timeoutPeriod = timeoutPeriod;
    }

    @Override
    public boolean test(Sidecar sidecar) {
        final DateTime threshold = DateTime.now(DateTimeZone.UTC).minus(timeoutPeriod);
        return sidecar.lastSeen().isAfter(threshold);
    }
}
