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
package com.synectiks.process.server.shared.rest.resources.system;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import com.synectiks.process.server.plugin.Capabilities;
import com.synectiks.process.server.plugin.PluginMetaData;
import com.synectiks.process.server.rest.models.system.plugins.responses.PluginList;
import com.synectiks.process.server.rest.models.system.plugins.responses.PluginMetaDataValue;
import com.synectiks.process.server.shared.rest.resources.RestResource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Set;

@Api(value = "System/Plugins", description = "Plugin information")
@Path("/system/plugins")
@Produces(MediaType.APPLICATION_JSON)
@RequiresAuthentication
public class SystemPluginResource extends RestResource {
    private final Set<PluginMetaData> pluginMetaDataSet;

    @Inject
    public SystemPluginResource(Set<PluginMetaData> pluginMetaDataSet) {
        this.pluginMetaDataSet = pluginMetaDataSet;
    }

    @GET
    @Timed
    @ApiOperation(value = "List all installed plugins on this node.")
    public PluginList list() {
        final List<PluginMetaDataValue> pluginMetaDataValues = Lists.newArrayList();

        for (PluginMetaData pluginMetaData : pluginMetaDataSet) {
            pluginMetaDataValues.add(PluginMetaDataValue.create(
                    pluginMetaData.getUniqueId(),
                    pluginMetaData.getName(),
                    pluginMetaData.getAuthor(),
                    pluginMetaData.getURL(),
                    pluginMetaData.getVersion().toString(),
                    pluginMetaData.getDescription(),
                    pluginMetaData.getRequiredVersion().toString(),
                    Capabilities.toStringSet(pluginMetaData.getRequiredCapabilities())
            ));
        }

        return PluginList.create(pluginMetaDataValues);
    }
}
