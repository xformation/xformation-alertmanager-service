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
package com.synectiks.process.server.rest.resources.search;

import com.synectiks.process.server.decorators.DecoratorProcessor;
import com.synectiks.process.server.indexer.searches.Searches;
import com.synectiks.process.server.indexer.searches.SearchesClusterConfig;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.AbsoluteRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class SearchResourceTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Searches searches;

    @Mock
    private ClusterConfigService clusterConfigService;

    @Mock
    private DecoratorProcessor decoratorProcessor;

    private SearchResource searchResource;
    private Period queryLimitPeriod;

    @Before
    public void setUp() {
        queryLimitPeriod = Period.parse("P1D");
        searchResource = new SearchResource(searches, clusterConfigService, decoratorProcessor) {
        };

        when(clusterConfigService.get(SearchesClusterConfig.class)).thenReturn(SearchesClusterConfig.createDefault()
                .toBuilder()
                .queryTimeRangeLimit(queryLimitPeriod)
                .build());
    }

    @Test
    public void restrictTimeRangeReturnsGivenTimeRangeWithinLimit() {
        when(clusterConfigService.get(SearchesClusterConfig.class)).thenReturn(SearchesClusterConfig.createDefault()
                .toBuilder()
                .queryTimeRangeLimit(queryLimitPeriod)
                .build());

        final DateTime from = new DateTime(2015, 1, 15, 12, 0, DateTimeZone.UTC);
        final DateTime to = from.plusHours(1);
        final TimeRange timeRange = AbsoluteRange.create(from, to);

        final TimeRange restrictedTimeRange = searchResource.restrictTimeRange(timeRange);
        assertThat(restrictedTimeRange).isNotNull();
        assertThat(restrictedTimeRange.getFrom()).isEqualTo(from);
        assertThat(restrictedTimeRange.getTo()).isEqualTo(to);
    }

    @Test
    public void restrictTimeRangeReturnsGivenTimeRangeIfNoLimitHasBeenSet() {
        when(clusterConfigService.get(SearchesClusterConfig.class)).thenReturn(SearchesClusterConfig.createDefault()
                .toBuilder()
                .queryTimeRangeLimit(Period.ZERO)
                .build());

        final SearchResource resource = new SearchResource(searches, clusterConfigService, decoratorProcessor) {
        };

        final DateTime from = new DateTime(2015, 1, 15, 12, 0, DateTimeZone.UTC);
        final DateTime to = from.plusYears(1);
        final TimeRange timeRange = AbsoluteRange.create(from, to);

        final TimeRange restrictedTimeRange = resource.restrictTimeRange(timeRange);
        assertThat(restrictedTimeRange).isNotNull();
        assertThat(restrictedTimeRange.getFrom()).isEqualTo(from);
        assertThat(restrictedTimeRange.getTo()).isEqualTo(to);
    }

    @Test
    public void restrictTimeRangeReturnsLimitedTimeRange() {
        when(clusterConfigService.get(SearchesClusterConfig.class)).thenReturn(SearchesClusterConfig.createDefault()
                .toBuilder()
                .queryTimeRangeLimit(queryLimitPeriod)
                .build());

        final DateTime from = new DateTime(2015, 1, 15, 12, 0, DateTimeZone.UTC);
        final DateTime to = from.plus(queryLimitPeriod.multipliedBy(2));
        final TimeRange timeRange = AbsoluteRange.create(from, to);
        final TimeRange restrictedTimeRange = searchResource.restrictTimeRange(timeRange);

        assertThat(restrictedTimeRange).isNotNull();
        assertThat(restrictedTimeRange.getFrom()).isEqualTo(to.minus(queryLimitPeriod));
        assertThat(restrictedTimeRange.getTo()).isEqualTo(to);
    }
}
