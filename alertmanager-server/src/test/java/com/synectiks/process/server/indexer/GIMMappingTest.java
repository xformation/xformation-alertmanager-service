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
package com.synectiks.process.server.indexer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.glassfish.grizzly.utils.Charsets;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

abstract class GIMMappingTest {
    private static final ObjectMapper mapper = new ObjectMapperProvider().get();

    String json(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    IndexSetConfig mockIndexSetConfig() {
        final IndexSetConfig indexSetConfig = mock(IndexSetConfig.class);
        when(indexSetConfig.indexAnalyzer()).thenReturn("standard");

        return indexSetConfig;
    }

    String resource(String filename) throws IOException {
        return Resources.toString(Resources.getResource(this.getClass(), filename), Charsets.UTF8_CHARSET);
    }
}
