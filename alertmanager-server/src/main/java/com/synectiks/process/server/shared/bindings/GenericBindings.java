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
package com.synectiks.process.server.shared.bindings;

import com.codahale.metrics.MetricRegistry;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ServiceManager;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import okhttp3.OkHttpClient;
import com.synectiks.process.server.plugin.IOState;
import com.synectiks.process.server.plugin.LocalMetricRegistry;
import com.synectiks.process.server.plugin.buffers.InputBuffer;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.plugin.inputs.util.ThroughputCounter;
import com.synectiks.process.server.plugin.system.NodeId;
import com.synectiks.process.server.shared.bindings.providers.EventBusProvider;
import com.synectiks.process.server.shared.bindings.providers.MetricRegistryProvider;
import com.synectiks.process.server.shared.bindings.providers.NodeIdProvider;
import com.synectiks.process.server.shared.bindings.providers.OkHttpClientProvider;
import com.synectiks.process.server.shared.bindings.providers.ProxiedRequestsExecutorService;
import com.synectiks.process.server.shared.bindings.providers.ServiceManagerProvider;
import com.synectiks.process.server.shared.buffers.InputBufferImpl;
import com.synectiks.process.server.shared.buffers.ProcessBuffer;
import com.synectiks.process.server.shared.buffers.processors.DecodingProcessor;
import com.synectiks.process.server.shared.inputs.InputRegistry;

import javax.activation.MimetypesFileTypeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public class GenericBindings extends AbstractModule {

    @Override
    protected void configure() {
        // This is holding all our metrics.
        bind(MetricRegistry.class).toProvider(MetricRegistryProvider.class).asEagerSingleton();
        bind(LocalMetricRegistry.class).in(Scopes.NO_SCOPE); // must not be a singleton!

        install(new FactoryModuleBuilder().build(DecodingProcessor.Factory.class));

        bind(ProcessBuffer.class).asEagerSingleton();
        bind(InputBuffer.class).to(InputBufferImpl.class);
        bind(NodeId.class).toProvider(NodeIdProvider.class);

        bind(ServiceManager.class).toProvider(ServiceManagerProvider.class).asEagerSingleton();

        bind(ThroughputCounter.class);

        bind(EventBus.class).toProvider(EventBusProvider.class).in(Scopes.SINGLETON);

        bind(Semaphore.class).annotatedWith(Names.named("JournalSignal")).toInstance(new Semaphore(0));

        install(new FactoryModuleBuilder().build(new TypeLiteral<IOState.Factory<MessageInput>>(){}));

        bind(InputRegistry.class).asEagerSingleton();

        bind(OkHttpClient.class).toProvider(OkHttpClientProvider.class).asEagerSingleton();

        bind(MimetypesFileTypeMap.class).toInstance(new MimetypesFileTypeMap());

        bind(ExecutorService.class).annotatedWith(Names.named("proxiedRequestsExecutorService")).toProvider(ProxiedRequestsExecutorService.class).asEagerSingleton();
    }
}
