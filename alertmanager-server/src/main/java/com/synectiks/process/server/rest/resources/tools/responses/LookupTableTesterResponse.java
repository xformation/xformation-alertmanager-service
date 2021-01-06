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
package com.synectiks.process.server.rest.resources.tools.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;
import com.synectiks.process.server.plugin.lookup.LookupResult;

import javax.annotation.Nullable;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class LookupTableTesterResponse {
    @JsonProperty("empty")
    public abstract boolean empty();

    @JsonProperty("error")
    public abstract boolean error();

    @JsonProperty("error_message")
    public abstract String errorMessage();

    @JsonProperty("key")
    @Nullable
    public abstract Object key();

    @JsonProperty("value")
    @Nullable
    public abstract Object value();

    @JsonCreator
    public static LookupTableTesterResponse create(@JsonProperty("empty") boolean empty,
                                                   @JsonProperty("error") boolean error,
                                                   @JsonProperty("error_message") String errorMessage,
                                                   @JsonProperty("key") @Nullable Object key,
                                                   @JsonProperty("value") @Nullable Object value) {
        return new AutoValue_LookupTableTesterResponse(empty, error, errorMessage, key, value);
    }

    public static LookupTableTesterResponse error(String errorMessage) {
        return create(true, true, errorMessage, null, null);
    }

    public static LookupTableTesterResponse emptyResult(String string) {
        return create(true, false, "", string, null);
    }

    public static LookupTableTesterResponse result(String string, LookupResult result) {
        return create(result.isEmpty(), false, "", string, result.singleValue());
    }
}
