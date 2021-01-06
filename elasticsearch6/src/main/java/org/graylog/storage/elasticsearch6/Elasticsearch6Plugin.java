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
package org.graylog.storage.elasticsearch6;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.server.plugin.Plugin;
import com.synectiks.process.server.plugin.PluginMetaData;
import com.synectiks.process.server.plugin.PluginModule;
import com.synectiks.process.server.plugin.Version;

import java.util.Collection;

public class Elasticsearch6Plugin implements Plugin {
    public static final Version SUPPORTED_ES_VERSION = Version.from(6, 0, 0);

    @Override
    public PluginMetaData metadata() {
        return new Elasticsearch6Metadata();
    }

    @Override
    public Collection<PluginModule> modules() {
        return ImmutableList.of(
                new Elasticsearch6Module(),
                new ViewsESBackendModule()
        );
    }
}