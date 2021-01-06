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
package com.synectiks.process.server.system.activities;

import com.google.common.collect.Maps;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.shared.system.activities.Activity;
import com.synectiks.process.server.shared.system.activities.ActivityWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author Lennart Koopmann <lennart@socketfeed.com>
 */
public class SystemMessageActivityWriter implements ActivityWriter {

    private static final Logger LOG = LoggerFactory.getLogger(SystemMessageActivityWriter.class);
    private final SystemMessageService systemMessageService;
    private final ServerStatus serverStatus;

    @Inject
    public SystemMessageActivityWriter(SystemMessageService systemMessageService, ServerStatus serverStatus) {
        this.systemMessageService = systemMessageService;
        this.serverStatus = serverStatus;
    }
    
    @Override
    public void write(Activity activity) {
        try {
            Map<String, Object> entry = Maps.newHashMap();
            entry.put("timestamp", Tools.nowUTC());
            entry.put("content", activity.getMessage());
            entry.put("caller", activity.getCaller().getCanonicalName());
            entry.put("node_id", serverStatus.getNodeId().toString());

            final SystemMessage sm = systemMessageService.create(entry);
            systemMessageService.save(sm);
        } catch (ValidationException e) {
            LOG.error("Could not write activity.", e);
        }
    }
    
}
