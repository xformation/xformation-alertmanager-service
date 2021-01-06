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
package com.synectiks.process.server.streams.matchers;

import com.synectiks.process.server.plugin.Message;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class AlwaysMatcherTest {
    @Test
    public void matchAlwaysReturnsTrue() throws Exception {
        final AlwaysMatcher matcher = new AlwaysMatcher();
        assertThat(matcher.match(null, null)).isTrue();
        assertThat(matcher.match(
                new Message("Test", "source", new DateTime(2016, 9, 7, 0, 0, DateTimeZone.UTC)),
                new StreamRuleMock(Collections.singletonMap("_id", "stream-rule-id"))))
                .isTrue();
    }

}