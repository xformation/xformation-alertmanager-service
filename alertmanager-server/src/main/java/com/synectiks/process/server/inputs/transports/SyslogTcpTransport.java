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
package com.synectiks.process.server.inputs.transports;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import com.synectiks.process.server.inputs.syslog.tcp.SyslogTCPFramingRouterHandler;
import com.synectiks.process.server.inputs.transports.netty.EventLoopGroupFactory;
import com.synectiks.process.server.plugin.LocalMetricRegistry;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.plugin.inputs.annotations.ConfigClass;
import com.synectiks.process.server.plugin.inputs.annotations.FactoryClass;
import com.synectiks.process.server.plugin.inputs.transports.Transport;
import com.synectiks.process.server.plugin.inputs.util.ThroughputCounter;

import java.util.LinkedHashMap;
import java.util.concurrent.Callable;

public class SyslogTcpTransport extends TcpTransport {
    @AssistedInject
    public SyslogTcpTransport(@Assisted Configuration configuration,
                              EventLoopGroup eventLoopGroup,
                              EventLoopGroupFactory eventLoopGroupFactory,
                              NettyTransportConfiguration nettyTransportConfiguration,
                              ThroughputCounter throughputCounter,
                              LocalMetricRegistry localRegistry,
                              com.synectiks.process.server.Configuration graylogConfiguration) {
        super(configuration,
                eventLoopGroup,
                eventLoopGroupFactory,
                nettyTransportConfiguration,
                throughputCounter,
                localRegistry,
                graylogConfiguration);
    }

    @Override
    protected LinkedHashMap<String, Callable<? extends ChannelHandler>> getCustomChildChannelHandlers(MessageInput input) {
        final LinkedHashMap<String, Callable<? extends ChannelHandler>> finalChannelHandlers = new LinkedHashMap<>(super.getCustomChildChannelHandlers(input));

        // Replace the "framer" channel handler inserted by the parent.
        finalChannelHandlers.replace("framer", () -> new SyslogTCPFramingRouterHandler(maxFrameLength, delimiter));

        return finalChannelHandlers;
    }

    @FactoryClass
    public interface Factory extends Transport.Factory<SyslogTcpTransport> {
        @Override
        SyslogTcpTransport create(Configuration configuration);
    }

    @ConfigClass
    public static class Config extends TcpTransport.Config {
    }
}
