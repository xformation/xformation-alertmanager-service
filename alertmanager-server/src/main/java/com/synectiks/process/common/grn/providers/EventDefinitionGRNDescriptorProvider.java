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
package com.synectiks.process.common.grn.providers;

import com.synectiks.process.common.events.processor.DBEventDefinitionService;
import com.synectiks.process.common.events.processor.EventDefinitionDto;
import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.grn.GRNDescriptor;
import com.synectiks.process.common.grn.GRNDescriptorProvider;

import javax.inject.Inject;
import java.util.Optional;

public class EventDefinitionGRNDescriptorProvider implements GRNDescriptorProvider {
    private final DBEventDefinitionService dbEventDefinitionService;

    @Inject
    public EventDefinitionGRNDescriptorProvider(DBEventDefinitionService dbEventDefinitionService) {
        this.dbEventDefinitionService = dbEventDefinitionService;
    }

    @Override
    public GRNDescriptor get(GRN grn) {
        final Optional<String> title = dbEventDefinitionService.get(grn.entity()).map(EventDefinitionDto::title);
        return GRNDescriptor.create(grn, title.orElse("ERROR: EventDefinition for <" + grn.toString() + "> not found!"));
    }
}
