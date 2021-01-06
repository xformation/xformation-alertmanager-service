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
package com.synectiks.process.common.plugins.pipelineprocessor.ast.functions;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.Expression;

/**
 * Helper Function implementation which evaluates and memoizes all constant FunctionArgs.
 *
 * @param <T> the return type
 */
public abstract class AbstractFunction<T> implements Function<T> {

    @Override
    public Object preComputeConstantArgument(FunctionArgs args, String name, Expression arg) {
        return arg.evaluateUnsafe(EvaluationContext.emptyContext());
    }
}
