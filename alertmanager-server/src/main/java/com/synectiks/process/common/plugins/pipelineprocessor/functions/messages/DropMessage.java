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
package com.synectiks.process.common.plugins.pipelineprocessor.functions.messages;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;
import com.synectiks.process.server.plugin.Message;

import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.type;

public class DropMessage extends AbstractFunction<Void> {

    public static final String NAME = "drop_message";
    public static final String MESSAGE_ARG = "message";
    private final ParameterDescriptor<Message, Message> messageParam;

    public DropMessage() {
        messageParam = type(MESSAGE_ARG, Message.class).optional().description("The message to drop, defaults to '$message'").build();
    }

    @Override
    public Void evaluate(FunctionArgs args, EvaluationContext context) {
        final Message message = messageParam.optional(args, context).orElse(context.currentMessage());
        message.setFilterOut(true);
        return null;
    }

    @Override
    public FunctionDescriptor<Void> descriptor() {
        return FunctionDescriptor.<Void>builder()
                .name(NAME)
                .pure(true)
                .returnType(Void.class)
                .params(ImmutableList.of(
                        messageParam
                ))
                .description("Discards a message from further processing")
                .build();
    }
}
