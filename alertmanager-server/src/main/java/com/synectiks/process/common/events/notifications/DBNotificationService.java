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
package com.synectiks.process.common.events.notifications;

import com.synectiks.process.common.security.entities.EntityOwnershipService;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.database.PaginatedDbService;
import com.synectiks.process.server.database.PaginatedList;
import com.synectiks.process.server.plugin.database.users.User;
import com.synectiks.process.server.search.SearchQuery;

import javax.inject.Inject;
import java.util.function.Predicate;

public class DBNotificationService extends PaginatedDbService<NotificationDto> {
    private static final String NOTIFICATION_COLLECTION_NAME = "event_notifications";

    private final EntityOwnershipService entityOwnerShipService;

    @Inject
    public DBNotificationService(MongoConnection mongoConnection,
                                 MongoJackObjectMapperProvider mapper,
                                 EntityOwnershipService entityOwnerShipService) {
        super(mongoConnection, mapper, NotificationDto.class, NOTIFICATION_COLLECTION_NAME);
        this.entityOwnerShipService = entityOwnerShipService;
    }

    public PaginatedList<NotificationDto> searchPaginated(SearchQuery query, Predicate<NotificationDto> filter,
                                                          String sortByField, int page, int perPage) {
        return findPaginatedWithQueryFilterAndSort(query.toDBQuery(), filter,
                getSortBuilder("asc", sortByField), page, perPage);
    }

    public NotificationDto saveWithOwnership(NotificationDto notificationDto, User user) {
        final NotificationDto dto = super.save(notificationDto);
        entityOwnerShipService.registerNewEventNotification(dto.id(), user);
        return dto;
    }

    @Override
    public int delete(String id) {
        entityOwnerShipService.unregisterEventNotification(id);
        return super.delete(id);
    }
}
