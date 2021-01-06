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
package com.synectiks.process.server.indexer.rotation;

import com.synectiks.process.server.indexer.rotation.strategies.MessageCountRotationStrategy;
import com.synectiks.process.server.indexer.rotation.strategies.SizeBasedRotationStrategy;
import com.synectiks.process.server.indexer.rotation.strategies.TimeBasedRotationStrategy;
import com.synectiks.process.server.plugin.PluginModule;

public class RotationStrategyBindings extends PluginModule {
    @Override
    protected void configure() {
        addRotationStrategy(MessageCountRotationStrategy.class);
        addRotationStrategy(SizeBasedRotationStrategy.class);
        addRotationStrategy(TimeBasedRotationStrategy.class);
    }

}