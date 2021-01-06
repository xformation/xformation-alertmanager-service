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
package com.synectiks.process.common.plugins.pipelineprocessor.simulator;

import com.synectiks.process.common.plugins.pipelineprocessor.ast.Pipeline;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.Rule;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.Stage;
import com.synectiks.process.common.plugins.pipelineprocessor.processors.listeners.InterpreterListener;
import com.synectiks.process.server.plugin.Message;

import java.util.Set;

class SimulatorInterpreterListener implements InterpreterListener {
    private final PipelineInterpreterTracer executionTrace;

    SimulatorInterpreterListener(PipelineInterpreterTracer executionTrace) {
        this.executionTrace = executionTrace;
    }

    @Override
    public void startProcessing() {
        executionTrace.startProcessing("Starting message processing");
    }

    @Override
    public void finishProcessing() {
        executionTrace.finishProcessing("Finished message processing");
    }

    @Override
    public void processStreams(Message message, Set<Pipeline> pipelines, Set<String> streams) {
        executionTrace.addTrace("Message " + message.getId() + " running " + pipelines + " for streams " + streams);
    }

    @Override
    public void enterStage(Stage stage) {
        executionTrace.addTrace("Enter " + stage);
    }

    @Override
    public void exitStage(Stage stage) {
        executionTrace.addTrace("Exit " + stage);
    }

    @Override
    public void evaluateRule(Rule rule, Pipeline pipeline) {
        executionTrace.addTrace("Evaluate " + rule + " in " + pipeline);
    }

    @Override
    public void failEvaluateRule(Rule rule, Pipeline pipeline) {
        executionTrace.addTrace("Failed evaluation " + rule + " in " + pipeline);
    }

    @Override
    public void satisfyRule(Rule rule, Pipeline pipeline) {
        executionTrace.addTrace("Evaluation satisfied " + rule + " in " + pipeline);
    }

    @Override
    public void dissatisfyRule(Rule rule, Pipeline pipeline) {
        executionTrace.addTrace("Evaluation not satisfied " + rule + " in " + pipeline);
    }

    @Override
    public void executeRule(Rule rule, Pipeline pipeline) {
        executionTrace.addTrace("Execute " + rule + " in " + pipeline);
    }

    @Override
    public void finishExecuteRule(Rule rule, Pipeline pipeline) {
        executionTrace.addTrace("Finished execution " + rule + " in " + pipeline);
    }

    @Override
    public void failExecuteRule(Rule rule, Pipeline pipeline) {
        executionTrace.addTrace("Failed execution " + rule + " in " + pipeline);
    }

    @Override
    public void continuePipelineExecution(Pipeline pipeline, Stage stage) {
        executionTrace.addTrace("Completed " + stage + " for " + pipeline + ", continuing to next stage");
    }

    @Override
    public void stopPipelineExecution(Pipeline pipeline, Stage stage) {
        executionTrace.addTrace("Completed " + stage + " for " + pipeline + ", NOT continuing to next stage");
    }
}
