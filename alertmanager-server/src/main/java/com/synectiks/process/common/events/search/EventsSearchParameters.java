/*
 * */
package com.synectiks.process.common.events.search;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.InvalidRangeParametersException;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.RelativeRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

@AutoValue
@JsonAutoDetect
@JsonDeserialize(builder = EventsSearchParameters.Builder.class)
public abstract class EventsSearchParameters {
    private static final String FIELD_PAGE = "page";
    private static final String FIELD_PER_PAGE = "per_page";
    private static final String FIELD_TIMERANGE = "timerange";
    private static final String FIELD_QUERY = "query";
    private static final String FIELD_FILTER = "filter";
    private static final String FIELD_SORT_BY = "sort_by";
    private static final String FIELD_SORT_DIRECTION = "sort_direction";

    public enum SortDirection {
        @JsonProperty("asc")
        ASC,
        @JsonProperty("desc")
        DESC
    }

    @JsonProperty(FIELD_PAGE)
    public abstract int page();

    @JsonProperty(FIELD_PER_PAGE)
    public abstract int perPage();

    @JsonProperty(FIELD_TIMERANGE)
    public abstract TimeRange timerange();

    @JsonProperty(FIELD_QUERY)
    public abstract String query();

    @JsonProperty(FIELD_FILTER)
    public abstract EventsSearchFilter filter();

    @JsonProperty(FIELD_SORT_BY)
    public abstract String sortBy();

    @JsonProperty(FIELD_SORT_DIRECTION)
    public abstract SortDirection sortDirection();

    public static Builder builder() {
        return Builder.create();
    }

    public static EventsSearchParameters empty() {
        return builder().build();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public static abstract class Builder {
        @JsonCreator
        public static Builder create() {
            RelativeRange timerange = null;
            try {
                timerange = RelativeRange.create(3600);
            } catch (InvalidRangeParametersException e) {
                // Should not happen
            }
            return new AutoValue_EventsSearchParameters.Builder()
                    .page(1)
                    .perPage(10)
                    .timerange(timerange)
                    .query("")
                    .filter(EventsSearchFilter.empty())
                    .sortBy(Message.FIELD_TIMESTAMP)
                    .sortDirection(SortDirection.DESC);
        }

        @JsonProperty(FIELD_PAGE)
        public abstract Builder page(int page);

        @JsonProperty(FIELD_PER_PAGE)
        public abstract Builder perPage(int perPage);

        @JsonProperty(FIELD_TIMERANGE)
        public abstract Builder timerange(TimeRange timerange);

        @JsonProperty(FIELD_QUERY)
        public abstract Builder query(String query);

        @JsonProperty(FIELD_FILTER)
        public abstract Builder filter(EventsSearchFilter filter);

        @JsonProperty(FIELD_SORT_BY)
        public abstract Builder sortBy(String sortBy);

        @JsonProperty(FIELD_SORT_DIRECTION)
        public abstract Builder sortDirection(SortDirection sortDirection);

        public abstract EventsSearchParameters build();
    }
}
