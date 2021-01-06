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
package com.synectiks.process.server.bootstrap.commands;

import com.github.rvesse.airline.annotations.Command;
import com.synectiks.process.server.bootstrap.CliCommand;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.Version;

@Command(name = "version", description = "Show the Graylog and JVM versions")
public class ShowVersion implements CliCommand {
    private final Version version = Version.CURRENT_CLASSPATH;

    @Override
    public void run() {
        System.out.println("Graylog " + version);
        System.out.println("JRE: " + Tools.getSystemInformation());
    }
}
