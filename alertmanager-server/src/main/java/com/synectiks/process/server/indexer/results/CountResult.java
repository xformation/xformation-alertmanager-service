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
package com.synectiks.process.server.indexer.results;

import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
public abstract class CountResult {
    public abstract long count();

    public abstract long tookMs();

    public static CountResult create(long count, long tookMs) {
        return new AutoValue_CountResult(count, tookMs);
    }

    public static CountResult empty() {
        return create(0, 0);
    }
}
