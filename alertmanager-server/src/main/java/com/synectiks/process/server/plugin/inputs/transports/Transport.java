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
package com.synectiks.process.server.plugin.inputs.transports;

import com.codahale.metrics.MetricSet;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.plugin.inputs.MisfireException;
import com.synectiks.process.server.plugin.inputs.codecs.CodecAggregator;

public interface Transport {
    void setMessageAggregator(CodecAggregator aggregator);

    void launch(MessageInput input) throws MisfireException;

    void stop();

    MetricSet getMetricSet();

    interface Config {
        ConfigurationRequest getRequestedConfiguration();
    }

    interface Factory<T extends Transport> {
        T create(Configuration configuration);

        Config getConfig();
    }
}
