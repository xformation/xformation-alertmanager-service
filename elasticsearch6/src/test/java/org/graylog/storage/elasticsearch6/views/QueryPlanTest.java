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
package org.graylog.storage.elasticsearch6.views;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.Search;
import com.synectiks.process.common.plugins.views.search.SearchJob;
import com.synectiks.process.common.plugins.views.search.SearchType;
import com.synectiks.process.common.plugins.views.search.elasticsearch.ElasticsearchQueryString;
import com.synectiks.process.common.plugins.views.search.elasticsearch.FieldTypesLookup;
import com.synectiks.process.common.plugins.views.search.elasticsearch.IndexLookup;
import com.synectiks.process.common.plugins.views.search.elasticsearch.QueryStringDecorators;
import com.synectiks.process.common.plugins.views.search.elasticsearch.QueryStringParser;
import com.synectiks.process.common.plugins.views.search.engine.QueryEngine;
import com.synectiks.process.common.plugins.views.search.engine.QueryPlan;
import com.synectiks.process.common.plugins.views.search.searchtypes.MessageList;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.InvalidRangeParametersException;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.RelativeRange;

import org.graylog.storage.elasticsearch6.views.ESGeneratedQueryContext;
import org.graylog.storage.elasticsearch6.views.ElasticsearchBackend;
import org.graylog.storage.elasticsearch6.views.searchtypes.ESMessageList;
import org.graylog.storage.elasticsearch6.views.searchtypes.ESSearchTypeHandler;
import org.junit.Test;

import javax.inject.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static com.google.common.collect.ImmutableSet.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class QueryPlanTest {
    private final RelativeRange timerange;
    private final QueryEngine queryEngine;

    public QueryPlanTest() throws InvalidRangeParametersException {
        timerange = RelativeRange.create(60);
        Map<String, Provider<ESSearchTypeHandler<? extends SearchType>>> handlers = Maps.newHashMap();
        handlers.put(MessageList.NAME, () -> new ESMessageList(new QueryStringDecorators.Fake()));

        final FieldTypesLookup fieldTypesLookup = mock(FieldTypesLookup.class);
        final QueryStringParser queryStringParser = new QueryStringParser();
        ElasticsearchBackend backend = new ElasticsearchBackend(handlers,
                queryStringParser,
                null,
                mock(IndexLookup.class),
                new QueryStringDecorators.Fake(),
                (elasticsearchBackend, ssb, job, query, results) -> new ESGeneratedQueryContext(elasticsearchBackend, ssb, job, query, results, fieldTypesLookup),
                false);
        queryEngine = new QueryEngine(backend, Collections.emptySet());
    }

    private static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    @Test
    public void singleQuery() {
        Search search = Search.builder()
                .queries(of(wildcardQueryBuilder()
                        .build()))
                .build();
        SearchJob job = new SearchJob(randomUUID(), search, "admin");
        final QueryPlan queryPlan = new QueryPlan(queryEngine, job);

        ImmutableList<Query> queries = queryPlan.queries();
        assertThat(queries).doesNotContain(Query.emptyRoot());
    }

    private Query.Builder stringQueryBuilder(String queryString, String id) {
        return Query.builder()
                .id(id == null ? randomUUID() : id)
                .timerange(timerange)
                .query(ElasticsearchQueryString.builder().queryString(queryString).build());
    }

    private Query.Builder wildcardQueryBuilder() {
        return wildcardQueryBuilder(null);
    }

    private Query.Builder wildcardQueryBuilder(String id) {
        return stringQueryBuilder("*", id);
    }
}
