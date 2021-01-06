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
package com.synectiks.process.server.inputs.codecs;

import com.google.common.collect.Maps;
import com.synectiks.process.server.plugin.configuration.Configuration;
import org.junit.Test;

import java.util.Map;

import static com.synectiks.process.server.inputs.codecs.JsonPathCodec.CK_PATH;
import static org.junit.Assert.assertEquals;

// TODO migrate test to use codec instead
public class JsonPathCodecTest {

    private static final double DELTA = 1e-15;

    private static Configuration configOf(String key, Object value) {
        Map<String, Object> vals = Maps.newHashMap();
        vals.put(key, value);
        return new Configuration(vals);
    }

    @Test
    public void testReadResultingInSingleInteger() throws Exception {
        String json = "{\"url\":\"https://api.github.com/repos/Graylog2/graylog2-server/releases/assets/22660\",\"download_count\":76185,\"id\":22660,\"name\":\"graylog2-server-0.20.0-preview.1.tgz\",\"label\":\"graylog2-server-0.20.0-preview.1.tgz\",\"content_type\":\"application/octet-stream\",\"state\":\"uploaded\",\"size\":38179285,\"updated_at\":\"2013-09-30T20:05:46Z\"}";
        String path = "$.download_count";

        Map<String, Object> result = new JsonPathCodec(configOf(CK_PATH, path)).read(json);
        assertEquals(1, result.size());
        assertEquals(76185, result.get("result"));
    }

    @Test
    public void testReadResultingInSingleString() throws Exception {
        String json = "{\"url\":\"https://api.github.com/repos/Graylog2/graylog2-server/releases/assets/22660\",\"download_count\":76185,\"id\":22660,\"name\":\"graylog2-server-0.20.0-preview.1.tgz\",\"label\":\"graylog2-server-0.20.0-preview.1.tgz\",\"content_type\":\"application/octet-stream\",\"state\":\"uploaded\",\"size\":38179285,\"updated_at\":\"2013-09-30T20:05:46Z\"}";
        String path = "$.state";

        Map<String, Object> result = new JsonPathCodec(configOf(CK_PATH, path)).read(json);
        assertEquals(1, result.size());
        assertEquals("uploaded", result.get("result"));
    }

    @Test
    public void testReadFromMap() throws Exception {
        String json = "{\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95},{\"category\":\"fiction\",\"author\":\"Evelyn Waugh\",\"title\":\"Sword of Honour\",\"price\":12.99,\"isbn\":\"0-553-21311-3\"}],\"bicycle\":{\"color\":\"red\",\"price\":19.95}}}";
        String path = "$.store.book[?(@.category == 'fiction')].author";

        Map<String, Object> result = new JsonPathCodec(configOf(CK_PATH, path)).read(json);
        assertEquals(1, result.size());
        assertEquals("Evelyn Waugh", result.get("result"));
    }

    @Test
    public void testReadResultingInDouble() throws Exception {
        String json = "{\"url\":\"https://api.github.com/repos/Graylog2/graylog2-server/releases/assets/22660\",\"some_double\":0.50,\"id\":22660,\"name\":\"graylog2-server-0.20.0-preview.1.tgz\",\"label\":\"graylog2-server-0.20.0-preview.1.tgz\",\"content_type\":\"application/octet-stream\",\"state\":\"uploaded\",\"size\":38179285,\"updated_at\":\"2013-09-30T20:05:46Z\"}";
        String path = "$.some_double";

        Map<String, Object> result = new JsonPathCodec(configOf(CK_PATH, path)).read(json);
        assertEquals(1, result.size());
        assertEquals(0.5, (Double) result.get("result"), DELTA);
    }

    @Test
    public void testBuildShortMessage() throws Exception {
        Map<String, Object> fields = Maps.newLinkedHashMap();
        fields.put("baz", 9001);
        fields.put("foo", "bar");

        JsonPathCodec selector = new JsonPathCodec(configOf(CK_PATH, "$.download_count"));
        assertEquals("JSON API poll result: $['download_count'] -> {baz=9001, foo=bar}", selector.buildShortMessage(fields));
    }

    @Test
    public void testBuildShortMessageThatGetsCut() throws Exception {
        Map<String, Object> fields = Maps.newLinkedHashMap();
        fields.put("baz", 9001);
        fields.put("foo", "bargggdzrtdfgfdgldfsjgkfdlgjdflkjglfdjgljslfperitperoujglkdnfkndsbafdofhasdpfoöadjsFOO");

        JsonPathCodec selector = new JsonPathCodec(configOf(CK_PATH, "$.download_count"));
        assertEquals("JSON API poll result: $['download_count'] -> {baz=9001, foo=bargggdzrtdfgfdgldfsjgkfdlgjdflkjgl[...]", selector.buildShortMessage(fields));
    }

}
