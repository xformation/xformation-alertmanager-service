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
package org.graylog.storage.elasticsearch6;

import com.synectiks.process.common.testing.elasticsearch.ElasticsearchInstance;

import static org.graylog.storage.elasticsearch6.testing.TestUtils.jestClient;

import org.graylog.storage.elasticsearch6.CountsAdapterES6;
import org.graylog.storage.elasticsearch6.testing.ElasticsearchInstanceES6;

import com.synectiks.process.server.indexer.counts.CountsAdapter;
import com.synectiks.process.server.indexer.counts.CountsIT;
import org.junit.Rule;

public class CountsES6IT extends CountsIT {
    @Rule
    public final ElasticsearchInstance elasticsearch = ElasticsearchInstanceES6.create();

    @Override
    protected ElasticsearchInstance elasticsearch() {
        return this.elasticsearch;
    }

    @Override
    protected CountsAdapter countsAdapter() {
        return new CountsAdapterES6(jestClient(elasticsearch));
    }
}
