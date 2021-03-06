/*
 * */
package com.synectiks.process.server.streams.events;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class StreamDeletedEvent {
    private static final String FIELD_STREAM_ID = "stream_id";

    @JsonProperty(FIELD_STREAM_ID)
    public abstract String streamId();

    @JsonCreator
    public static StreamDeletedEvent create(@JsonProperty(FIELD_STREAM_ID) String streamId) {
        return new AutoValue_StreamDeletedEvent(streamId);
    }
}
