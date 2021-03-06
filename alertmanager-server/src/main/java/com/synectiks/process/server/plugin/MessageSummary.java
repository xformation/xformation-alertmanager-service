/*
 * */
package com.synectiks.process.server.plugin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * MessageSummary is being used as a return value for AlarmCallbacks.
 */
public class MessageSummary {
    // for these fields we define individual json properties
    private static final HashSet<String> RESERVED_FIELDS = Sets.newHashSet(Message.FIELD_ID,
                                                                          Message.FIELD_MESSAGE,
                                                                          Message.FIELD_SOURCE,
                                                                          Message.FIELD_TIMESTAMP,
                                                                          Message.FIELD_STREAMS);
    @JsonIgnore
    private final String index;

    @JsonIgnore
    private final Message message;

    public MessageSummary(String index, Message message) {
        this.index = index;
        this.message = message;
    }

    @JsonProperty
    public String getIndex() {
        return index;
    }

    @JsonProperty
    public String getId() {
        return message.getId();
    }

    @JsonProperty
    public String getSource() {
        return message.getSource();
    }

    @JsonProperty
    public String getMessage() {
        return message.getMessage();
    }

    @JsonProperty
    public DateTime getTimestamp() {
        return message.getTimestamp();
    }

    @JsonProperty
    public Collection<String> getStreamIds() {
        return message.getStreamIds();
    }

    @JsonProperty
    public Map<String, Object> getFields() {
        Map<String, Object> genericFields = Maps.newHashMap();

        // strip out common "fields" that we report as individual properties
        for (Map.Entry<String, Object> entry : message.getFieldsEntries()) {
            if (!RESERVED_FIELDS.contains(entry.getKey())) {
                genericFields.put(entry.getKey(), entry.getValue());
            }

        }

        return genericFields;
    }

    @JsonIgnore
    public boolean hasField(String key) {
        return message.hasField(key);
    }

    @JsonIgnore
    public Object getField(String key) {
        return message.getField(key);
    }

    @JsonIgnore
    public Message getRawMessage() { return message;}
}
