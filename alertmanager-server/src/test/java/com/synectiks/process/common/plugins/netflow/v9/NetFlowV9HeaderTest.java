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
package com.synectiks.process.common.plugins.netflow.v9;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NetFlowV9HeaderTest {
    @Test
    public void prettyHexDump() {
        final NetFlowV9Header header = NetFlowV9Header.create(5, 23, 42L, 1000L, 1L, 1L);
        assertThat(header.prettyHexDump()).isNotEmpty();
    }
}