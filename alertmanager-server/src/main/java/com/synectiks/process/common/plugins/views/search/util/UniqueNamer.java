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
package com.synectiks.process.common.plugins.views.search.util;

/**
 * Utility class to generate unique names.
 *
 * Not threadsafe, you need to lock externally.
 */
public class UniqueNamer {

    private final String prefix;

    private long number = 0;

    public UniqueNamer() {
        this("name-");
    }

    public UniqueNamer(String prefix) {
        this.prefix = prefix;
    }

    public String nextName() {
        return prefix + ++number;
    }

    public String currentName() {
        return prefix + number;
    }

}