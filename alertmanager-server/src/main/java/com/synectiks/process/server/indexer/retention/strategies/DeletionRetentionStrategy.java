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
package com.synectiks.process.server.indexer.retention.strategies;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.audit.AuditActor;
import com.synectiks.process.server.audit.AuditEventSender;
import com.synectiks.process.server.indexer.IndexSet;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.indexer.indices.Indices;
import com.synectiks.process.server.plugin.indexer.retention.RetentionStrategyConfig;
import com.synectiks.process.server.plugin.system.NodeId;
import com.synectiks.process.server.shared.system.activities.ActivityWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.synectiks.process.server.audit.AuditEventTypes.ES_INDEX_RETENTION_DELETE;

public class DeletionRetentionStrategy extends AbstractIndexCountBasedRetentionStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(DeletionRetentionStrategy.class);

    private final Indices indices;
    private final NodeId nodeId;
    private final AuditEventSender auditEventSender;

    @Inject
    public DeletionRetentionStrategy(Indices indices,
                                     ActivityWriter activityWriter,
                                     NodeId nodeId,
                                     AuditEventSender auditEventSender) {
        super(indices, activityWriter);
        this.indices = indices;
        this.nodeId = nodeId;
        this.auditEventSender = auditEventSender;
    }

    @Override
    protected Optional<Integer> getMaxNumberOfIndices(IndexSet indexSet) {
        final IndexSetConfig indexSetConfig = indexSet.getConfig();
        final RetentionStrategyConfig strategyConfig = indexSetConfig.retentionStrategy();

        if (!(strategyConfig instanceof DeletionRetentionStrategyConfig)) {
            throw new IllegalStateException("Invalid retention strategy config <" + strategyConfig.getClass().getCanonicalName() + "> for index set <" + indexSetConfig.id() + ">");
        }

        final DeletionRetentionStrategyConfig config = (DeletionRetentionStrategyConfig) strategyConfig;

        return Optional.of(config.maxNumberOfIndices());
    }

    @Override
    public void retain(String indexName, IndexSet indexSet) {
        final Stopwatch sw = Stopwatch.createStarted();

        indices.delete(indexName);
        auditEventSender.success(AuditActor.system(nodeId), ES_INDEX_RETENTION_DELETE, ImmutableMap.of(
                "index_name", indexName,
                "retention_strategy", this.getClass().getCanonicalName()
        ));

        LOG.info("Finished index retention strategy [delete] for index <{}> in {}ms.", indexName,
                sw.stop().elapsed(TimeUnit.MILLISECONDS));
    }

    @Override
    public Class<? extends RetentionStrategyConfig> configurationClass() {
        return DeletionRetentionStrategyConfig.class;
    }

    @Override
    public RetentionStrategyConfig defaultConfiguration() {
        return DeletionRetentionStrategyConfig.createDefault();
    }
}
