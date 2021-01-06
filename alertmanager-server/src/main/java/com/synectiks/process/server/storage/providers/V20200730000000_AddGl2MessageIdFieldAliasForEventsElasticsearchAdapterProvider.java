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
package com.synectiks.process.server.storage.providers;

import com.synectiks.process.common.plugins.views.migrations.V20200730000000_AddGl2MessageIdFieldAliasForEvents;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.storage.ElasticsearchVersion;
import com.synectiks.process.server.storage.VersionAwareProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

public class V20200730000000_AddGl2MessageIdFieldAliasForEventsElasticsearchAdapterProvider
        extends VersionAwareProvider<V20200730000000_AddGl2MessageIdFieldAliasForEvents.ElasticsearchAdapter> {
    @Inject
    public V20200730000000_AddGl2MessageIdFieldAliasForEventsElasticsearchAdapterProvider(
            @ElasticsearchVersion Version version,
            Map<Version, Provider<V20200730000000_AddGl2MessageIdFieldAliasForEvents.ElasticsearchAdapter>> pluginBindings) {
        super(version, pluginBindings);
    }
}
