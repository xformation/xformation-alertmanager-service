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
package com.synectiks.process.common.plugins.pipelineprocessor.functions.dates.periods;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;
import org.joda.time.Period;

public class PeriodParseFunction extends AbstractFunction<Period> {

    public static final String NAME = "period";
    private final ParameterDescriptor<String, Period> value =
            ParameterDescriptor
                    .string("value", Period.class)
                    .transform(Period::parse)
                    .build();


    @Override
    public Period evaluate(FunctionArgs args, EvaluationContext context) {
        return value.required(args, context);
    }

    @Override
    public FunctionDescriptor<Period> descriptor() {
        return FunctionDescriptor.<Period>builder()
                .name(NAME)
                .description("Parses a ISO 8601 period from the specified string.")
                .pure(true)
                .returnType(Period.class)
                .params(value)
                .build();
    }
}
