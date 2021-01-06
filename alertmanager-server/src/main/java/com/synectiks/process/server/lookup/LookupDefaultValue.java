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
package com.synectiks.process.server.lookup;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface LookupDefaultValue {
    String FIELD_VALUE_STRING = "value_string";
    String FIELD_VALUE_TYPE = "value_type";
    String FIELD_VALUE = "value";

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    enum Type {
        STRING, NUMBER, OBJECT, BOOLEAN, NULL
    }

    String valueString();
    LookupDefaultValue.Type valueType();
    Object value();
    boolean isSet();
}
