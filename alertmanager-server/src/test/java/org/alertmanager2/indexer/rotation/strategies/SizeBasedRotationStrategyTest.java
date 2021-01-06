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
package com.synectiks.process.server.indexer.rotation.strategies;

import com.synectiks.process.server.audit.AuditEventSender;
import com.synectiks.process.server.indexer.IndexSet;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.indexer.indices.Indices;
import com.synectiks.process.server.plugin.system.NodeId;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Optional;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SizeBasedRotationStrategyTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private IndexSet indexSet;

    @Mock
    private IndexSetConfig indexSetConfig;

    @Mock
    private Indices indices;

    @Mock
    private NodeId nodeId;

    @Mock
    private AuditEventSender auditEventSender;

    @Test
    public void testRotate() throws Exception {
        when(indices.getStoreSizeInBytes("name")).thenReturn(Optional.of(1000L));
        when(indexSet.getNewestIndex()).thenReturn("name");
        when(indexSet.getConfig()).thenReturn(indexSetConfig);
        when(indexSetConfig.rotationStrategy()).thenReturn(SizeBasedRotationStrategyConfig.create(100L));

        final SizeBasedRotationStrategy strategy = new SizeBasedRotationStrategy(indices, nodeId, auditEventSender);

        strategy.rotate(indexSet);
        verify(indexSet, times(1)).cycle();
        reset(indexSet);
    }


    @Test
    public void testDontRotate() throws Exception {
        when(indices.getStoreSizeInBytes("name")).thenReturn(Optional.of(1000L));
        when(indexSet.getNewestIndex()).thenReturn("name");
        when(indexSet.getConfig()).thenReturn(indexSetConfig);
        when(indexSetConfig.rotationStrategy()).thenReturn(SizeBasedRotationStrategyConfig.create(100000L));

        final SizeBasedRotationStrategy strategy = new SizeBasedRotationStrategy(indices, nodeId, auditEventSender);

        strategy.rotate(indexSet);
        verify(indexSet, never()).cycle();
        reset(indexSet);
    }


    @Test
    public void testRotateFailed() throws Exception {
        when(indices.getStoreSizeInBytes("name")).thenReturn(Optional.empty());
        when(indexSet.getNewestIndex()).thenReturn("name");
        when(indexSet.getConfig()).thenReturn(indexSetConfig);
        when(indexSetConfig.rotationStrategy()).thenReturn(SizeBasedRotationStrategyConfig.create(100L));

        final SizeBasedRotationStrategy strategy = new SizeBasedRotationStrategy(indices, nodeId, auditEventSender);

        strategy.rotate(indexSet);
        verify(indexSet, never()).cycle();
        reset(indexSet);
    }
}