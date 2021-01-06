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
package com.synectiks.process.server.users;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.synectiks.process.server.dashboards.events.DashboardDeletedEvent;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.rest.models.users.requests.Startpage;
import com.synectiks.process.server.shared.users.UserService;
import com.synectiks.process.server.streams.events.StreamDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class StartPageCleanupListener {
    private static final Logger LOG = LoggerFactory.getLogger(StartPageCleanupListener.class);

    private final UserService userService;

    @Inject
    public StartPageCleanupListener(EventBus serverEventBus,
                                    UserService userService) {
        this.userService = userService;
        serverEventBus.register(this);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void removeStartpageReferencesIfStreamDeleted(StreamDeletedEvent streamDeletedEvent) {
        final Startpage deletedStartpage = Startpage.create("stream", streamDeletedEvent.streamId());
        resetReferencesToStartpage(deletedStartpage);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void removeStartpageReferencesIfDashboardDeleted(DashboardDeletedEvent dashboardDeletedEvent) {
        final Startpage deletedStartpage = Startpage.create("dashboard", dashboardDeletedEvent.dashboardId());
        resetReferencesToStartpage(deletedStartpage);
    }

    private void resetReferencesToStartpage(Startpage deletedStartpage) {
        this.userService.loadAll()
            .stream()
            .filter(user -> user.getStartpage() != null && user.getStartpage().equals(deletedStartpage))
            .forEach(user -> {
                user.setStartpage(null);
                try {
                    this.userService.save(user);
                } catch (ValidationException e) {
                    LOG.error("Unable to reset start page for user which references deleted start page: ", e);
                }
            });
    }
}
