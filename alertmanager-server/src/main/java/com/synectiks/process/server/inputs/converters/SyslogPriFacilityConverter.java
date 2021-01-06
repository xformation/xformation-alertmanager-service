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
package com.synectiks.process.server.inputs.converters;

import com.google.common.primitives.Ints;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.inputs.Converter;

import java.util.Map;

public class SyslogPriFacilityConverter extends Converter {
    public SyslogPriFacilityConverter(Map<String, Object> config) {
        super(Type.SYSLOG_PRI_FACILITY, config);
    }

    @Override
    public Object convert(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        final Integer priority = Ints.tryParse(value);
        if (priority == null) {
            return value;
        }

        return Tools.syslogFacilityToReadable(SyslogPriUtilities.facilityFromPriority(priority));
    }

    @Override
    public boolean buildsMultipleFields() {
        return false;
    }
}
