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
package com.synectiks.process.common.events;

import com.synectiks.process.common.events.configuration.EventsConfiguration;
import com.synectiks.process.common.events.configuration.EventsConfigurationProvider;

public class EventsConfigurationTestProvider extends EventsConfigurationProvider {
    private final EventsConfiguration config;

    public static EventsConfigurationTestProvider create() {
        return new EventsConfigurationTestProvider(EventsConfiguration.builder().build());
    }

    public EventsConfigurationTestProvider(EventsConfiguration config) {
        super(null);
        this.config = config;
    }

    @Override
    public EventsConfiguration get() {
        return config;
    }
}
