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
package com.synectiks.process.common.events.notifications;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.events.event.Event;
import com.synectiks.process.common.events.event.EventDto;
import com.synectiks.process.common.events.processor.DBEventDefinitionService;
import com.synectiks.process.common.events.processor.EventDefinition;
import com.synectiks.process.common.events.processor.EventProcessor;
import com.synectiks.process.common.events.processor.EventProcessorException;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.plugin.MessageSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

public class EventBacklogService {
    private static final Logger LOG = LoggerFactory.getLogger(EventBacklogService.class);

    private final Map<String, EventProcessor.Factory> eventProcessorFactories;
    private final DBEventDefinitionService eventDefinitionService;

    @Inject
    public EventBacklogService(Map<String, EventProcessor.Factory> eventProcessorFactories, DBEventDefinitionService eventDefinitionService) {
        this.eventProcessorFactories = eventProcessorFactories;
        this.eventDefinitionService = eventDefinitionService;
    }

    public ImmutableList<MessageSummary> getMessagesForEvent(EventDto eventDto, long backlogSize) throws NotFoundException {
        if (backlogSize <= 0) {
            return ImmutableList.of();
        }
        final EventProcessor.Factory factory = eventProcessorFactories.get(eventDto.eventDefinitionType());
        if (factory == null) {
            throw new NotFoundException("Couldn't find event processor factory for type " +
                    eventDto.eventDefinitionType());
        }
        final EventDefinition eventDefinition = eventDefinitionService.get(eventDto.eventDefinitionId()).orElseThrow(() ->
                new NotFoundException("Could not find event definintion <" +
                        eventDto.eventDefinitionId() + ">"));
        final EventProcessor eventProcessor = factory.create(eventDefinition);

        final ImmutableList.Builder<MessageSummary> backlogBuilder = ImmutableList.builder();
        try {
            eventProcessor.sourceMessagesForEvent(Event.fromDto(eventDto), backlogBuilder::addAll, backlogSize);
        } catch (EventProcessorException e) {
            // TODO return this error, so it can be included in the notification message?
            LOG.error("Failed to query backlog messages for Event {}", eventDto.id(), e);
        }
        return backlogBuilder.build();
    }
}
