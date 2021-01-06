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
package com.synectiks.process.server.configuration.converters;

import com.github.joschi.jadconfig.Converter;
import com.synectiks.process.server.plugin.Version;

public class MajorVersionConverter implements Converter<Version> {
    @Override
    public Version convertFrom(String value) {
        final int majorVersion = Integer.parseInt(value);
        return Version.from(majorVersion, 0, 0);
    }

    @Override
    public String convertTo(Version value) {
        return value.toString();
    }
}