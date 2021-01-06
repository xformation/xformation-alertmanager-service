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
package com.synectiks.process.common.plugins.pipelineprocessor.functions.urls;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.google.common.collect.ImmutableList.of;

public class UrlEncode extends AbstractFunction<String> {
    public static final String NAME = "urlencode";

    private final ParameterDescriptor<String, String> valueParam;
    private final ParameterDescriptor<String, Charset> charsetParam;

    public UrlEncode() {
        valueParam = ParameterDescriptor.string("value").description("The string to encode").build();
        charsetParam = ParameterDescriptor.type("charset", String.class, Charset.class).optional()
                .description("The name of a supported character encoding such as \"UTF-8\" or \"US-ASCII\". Default: \"UTF-8\"")
                .transform(Charset::forName)
                .build();
    }

    @Override
    public String evaluate(FunctionArgs args, EvaluationContext context) {
        final String value = valueParam.required(args, context);
        final Charset charset = charsetParam.optional(args, context).orElse(StandardCharsets.UTF_8);

        if (value == null) {
            return null;
        }

        try {
            return URLEncoder.encode(value, charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unsupported character encoding", e);
        }
    }

    @Override
    public FunctionDescriptor<String> descriptor() {
        return FunctionDescriptor.<String>builder()
                .name(NAME)
                .returnType(String.class)
                .params(of(valueParam, charsetParam))
                .description("Translates a string into application/x-www-form-urlencoded format using a specific encoding scheme.")
                .build();
    }
}
