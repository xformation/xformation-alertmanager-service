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
package com.synectiks.process.server.search;

public class SearchQueryField {
    public enum Type {
        STRING, DATE, INT, LONG;
    }

    private final String dbField;
    private final Type fieldType;

    public static SearchQueryField create(String dbField) {
        return new SearchQueryField(dbField, Type.STRING);
    }

    public static SearchQueryField create(String dbField, Type fieldType) {
        return new SearchQueryField(dbField, fieldType);
    }

    public SearchQueryField(String dbField, Type fieldType) {
        this.dbField = dbField;
        this.fieldType = fieldType;
    }

    public String getDbField() {
        return dbField;
    }

    public Type getFieldType() {
        return fieldType;
    }
}
