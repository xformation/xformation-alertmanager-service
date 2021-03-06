/*
 * */
package com.synectiks.process.server.indexer.searches;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;
import java.util.List;

@AutoValue
@WithBeanGetter
public abstract class SearchesConfig {
    public final static int DEFAULT_LIMIT = 150;

    @JsonProperty
    public abstract String query();

    @JsonProperty
    @Nullable
    public abstract String filter();

    @JsonProperty
    @Nullable
    public abstract List<String> fields();

    @JsonProperty
    public abstract TimeRange range();

    @JsonProperty
    public abstract int limit();

    @JsonProperty
    public abstract int offset();

    @JsonProperty
    @Nullable
    public abstract Sorting sorting();

    @JsonCreator
    public SearchesConfig create(@JsonProperty("query") String query,
                                 @JsonProperty("filter") @Nullable String filter,
                                 @JsonProperty("fields") @Nullable List<String> fields,
                                 @JsonProperty("range") TimeRange timeRange,
                                 @JsonProperty("limit") int limit,
                                 @JsonProperty("offset") int offset,
                                 @JsonProperty("sorting") @Nullable Sorting sorting) {
        return builder()
                .query(query)
                .filter(filter)
                .fields(fields)
                .range(timeRange)
                .limit(limit)
                .offset(offset)
                .sorting(sorting)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_SearchesConfig.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder query(String query);

        public abstract Builder filter(String filter);

        public abstract Builder fields(List<String> fields);

        public abstract Builder range(TimeRange timeRange);

        public abstract Builder limit(int limit);

        public abstract int limit();

        public abstract Builder offset(int offset);

        public abstract Builder sorting(Sorting sorting);

        abstract SearchesConfig autoBuild();

        public SearchesConfig build() {
            if (limit() <= 0) {
                limit(DEFAULT_LIMIT);
            }
            return autoBuild();
        }
    }
}
