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
package com.synectiks.process.common.plugins.views.search.elasticsearch;

import com.synectiks.process.common.plugins.views.search.IndexRangeContainsOneOfStreams;
import com.synectiks.process.server.indexer.ranges.IndexRange;
import com.synectiks.process.server.indexer.ranges.IndexRangeService;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.streams.StreamService;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class IndexLookup {
    private final IndexRangeService indexRangeService;
    private final StreamService streamService;

    //this is only here for mocking purposes
    BiFunction<IndexRange, Set<Stream>, Boolean> indexRangeContainsOneOfStreams = this::indexRangeContainsOneOfStreams;

    @Inject
    public IndexLookup(IndexRangeService indexRangeService, StreamService streamService) {
        this.indexRangeService = indexRangeService;
        this.streamService = streamService;
    }

    public Set<String> indexNamesForStreamsInTimeRange(Set<String> streamIds, TimeRange timeRange) {
        if (streamIds.isEmpty())
            return Collections.emptySet();

        Set<Stream> usedStreams = streamService.loadByIds(streamIds);
        SortedSet<IndexRange> candidateIndices = indexRangeService.find(timeRange.getFrom(), timeRange.getTo());

        return candidateIndices.stream()
                .filter(i -> indexRangeContainsOneOfStreams.apply(i, usedStreams))
                .map(IndexRange::indexName)
                .collect(Collectors.toSet());
    }

    private boolean indexRangeContainsOneOfStreams(IndexRange indexRange, Set<Stream> streams) {
        return new IndexRangeContainsOneOfStreams(streams).test(indexRange);
    }
}
