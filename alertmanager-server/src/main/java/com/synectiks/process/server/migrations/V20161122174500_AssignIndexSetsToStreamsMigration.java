/*
 * */
package com.synectiks.process.server.migrations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.indexer.indexset.IndexSetService;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.streams.StreamService;

import org.graylog.autovalue.WithBeanGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;

public class V20161122174500_AssignIndexSetsToStreamsMigration extends Migration {
    private static final Logger LOG = LoggerFactory.getLogger(V20161122174500_AssignIndexSetsToStreamsMigration.class);

    private final StreamService streamService;
    private final IndexSetService indexSetService;
    private final ClusterConfigService clusterConfigService;

    @Inject
    public V20161122174500_AssignIndexSetsToStreamsMigration(final StreamService streamService,
                                                             final IndexSetService indexSetService,
                                                             final ClusterConfigService clusterConfigService) {
        this.streamService = streamService;
        this.indexSetService = indexSetService;
        this.clusterConfigService = clusterConfigService;
    }

    @Override
    public ZonedDateTime createdAt() {
        return ZonedDateTime.parse("2016-11-22T17:45:00Z");
    }

    @Override
    public void upgrade() {
        // Only run this migration once.
        if (clusterConfigService.get(MigrationCompleted.class) != null) {
            LOG.debug("Migration already completed.");
            return;
        }

        final IndexSetConfig indexSetConfig = findDefaultIndexSet();
        final ImmutableSet.Builder<String> completedStreamIds = ImmutableSet.builder();
        final ImmutableSet.Builder<String> failedStreamIds = ImmutableSet.builder();

        // Assign the "default index set" to all existing streams. Until now, there was no way to manually create
        // index sets, so the only one that exists is the "default" one created by an earlier migration.
        for (Stream stream : streamService.loadAll()) {
            if (isNullOrEmpty(stream.getIndexSetId())) {
                LOG.info("Assigning index set <{}> ({}) to stream <{}> ({})", indexSetConfig.id(),
                        indexSetConfig.title(), stream.getId(), stream.getTitle());
                stream.setIndexSetId(indexSetConfig.id());
                try {
                    streamService.save(stream);
                    completedStreamIds.add(stream.getId());
                } catch (ValidationException e) {
                    LOG.error("Unable to save stream <{}>", stream.getId(), e);
                    failedStreamIds.add(stream.getId());
                }
            }
        }

        // Mark this migration as done.
        clusterConfigService.write(MigrationCompleted.create(indexSetConfig.id(), completedStreamIds.build(), failedStreamIds.build()));
    }

    private IndexSetConfig findDefaultIndexSet() {
        final List<IndexSetConfig> indexSetConfigs = indexSetService.findAll();

        // If there is more than one index set, we have a problem. Since there wasn't a way to create index sets
        // manually until now, this should not happen.
        checkState(indexSetConfigs.size() < 2, "Found more than one index set config!");

        // If there is no index set, a previous migration didn't work.
        return indexSetConfigs.stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Couldn't find any index set config!"));
    }

    @JsonAutoDetect
    @AutoValue
    @WithBeanGetter
    public static abstract class MigrationCompleted {
        @JsonProperty("index_set_id")
        public abstract String indexSetId();

        @JsonProperty("completed_stream_ids")
        public abstract Set<String> completedStreamIds();

        @JsonProperty("failed_stream_ids")
        public abstract Set<String> failedStreamIds();

        @JsonCreator
        public static MigrationCompleted create(@JsonProperty("index_set_id") String indexSetId,
                                                @JsonProperty("completed_stream_ids") Set<String> completedStreamIds,
                                                @JsonProperty("failed_stream_ids") Set<String> failedStreamIds) {
            return new AutoValue_V20161122174500_AssignIndexSetsToStreamsMigration_MigrationCompleted(indexSetId, completedStreamIds, failedStreamIds);
        }
    }
}
