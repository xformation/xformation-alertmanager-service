/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.search.MessagesSearchType;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.search.SearchType;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@AutoValue
@JsonAutoDetect
public abstract class MessagesWidget implements ViewWidget {
    private static final String TYPE = "messages";
    private static final String FIELD_ID = "id";
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_TIMERANGE = "timerange";
    private static final String FIELD_QUERY = "query";
    private static final String FIELD_STREAMS = "streams";
    private static final String FIELD_CONFIG = "config";

    @JsonProperty(FIELD_ID)
    public abstract String id();

    @JsonProperty(FIELD_TYPE)
    String type() {
        return TYPE;
    }

    @JsonProperty(FIELD_TIMERANGE)
    @Nullable
    TimeRange timerange() {
        return null;
    }

    @JsonProperty(FIELD_QUERY)
    @Nullable
    String query() {
        return null;
    }

    @JsonProperty(FIELD_STREAMS)
    Set<String> streams() {
        return Collections.emptySet();
    }

    @JsonProperty(FIELD_CONFIG)
    abstract MessagesWidgetConfig config();

    @Override
    public Set<SearchType> toSearchTypes(RandomUUIDProvider randomUUIDProvider) {
        return Collections.singleton(MessagesSearchType.create(randomUUIDProvider.get()));
    }

    public static MessagesWidget create(String id, List<String> fields, boolean showMessageRow) {
        return new AutoValue_MessagesWidget(id, MessagesWidgetConfig.create(fields, showMessageRow));
    }
}
