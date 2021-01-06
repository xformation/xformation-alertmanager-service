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
package com.synectiks.process.common.plugins.pipelineprocessor.functions.lookup;

import com.google.inject.Inject;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;
import com.synectiks.process.server.lookup.LookupTableService;
import com.synectiks.process.server.plugin.lookup.LookupResult;

import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.object;
import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.string;

public class LookupValue extends AbstractFunction<Object> {

    public static final String NAME = "lookup_value";

    private final ParameterDescriptor<String, LookupTableService.Function> lookupTableParam;
    private final ParameterDescriptor<Object, Object> keyParam;
    private final ParameterDescriptor<Object, Object> defaultParam;

    @Inject
    public LookupValue(LookupTableService lookupTableService) {
        lookupTableParam = string("lookup_table", LookupTableService.Function.class)
                .description("The existing lookup table to use to lookup the given key")
                .transform(tableName -> lookupTableService.newBuilder().lookupTable(tableName).build())
                .build();
        keyParam = object("key")
                .description("The key to lookup in the table")
                .build();
        defaultParam = object("default")
                .description("The default single value that should be used if there is no lookup result")
                .optional()
                .build();
    }

    @Override
    public Object evaluate(FunctionArgs args, EvaluationContext context) {
        Object key = keyParam.required(args, context);
        if (key == null) {
            return defaultParam.optional(args, context).orElse(null);
        }
        LookupTableService.Function table = lookupTableParam.required(args, context);
        if (table == null) {
            return defaultParam.optional(args, context).orElse(null);
        }
        LookupResult result = table.lookup(key);
        if (result == null || result.isEmpty()) {
            return defaultParam.optional(args, context).orElse(null);
        }
        return result.singleValue();
    }

    @Override
    public FunctionDescriptor<Object> descriptor() {
        //noinspection unchecked
        return FunctionDescriptor.builder()
                .name(NAME)
                .description("Looks up a single value in the named lookup table.")
                .params(lookupTableParam, keyParam, defaultParam)
                .returnType(Object.class)
                .build();
    }
}
