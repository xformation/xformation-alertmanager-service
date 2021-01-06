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
package com.synectiks.process.server.commands;

import com.github.rvesse.airline.builder.CliBuilder;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.bootstrap.CliCommand;
import com.synectiks.process.server.bootstrap.CliCommandsProvider;
import com.synectiks.process.server.commands.journal.JournalDecode;
import com.synectiks.process.server.commands.journal.JournalShow;
import com.synectiks.process.server.commands.journal.JournalTruncate;

public class ServerCommandsProvider implements CliCommandsProvider {
    @Override
    public void addTopLevelCommandsOrGroups(CliBuilder<CliCommand> builder) {

        builder.withCommand(Server.class);

        builder.withGroup("journal")
                .withDescription("Manage the persisted message journal")
                .withDefaultCommand(JournalShow.class)
                .withCommands(
                        ImmutableSet.of(
                                JournalShow.class,
                                JournalTruncate.class,
                                JournalDecode.class
                        ));

    }
}
