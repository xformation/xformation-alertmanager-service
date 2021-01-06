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
package com.synectiks.process.server.rest.resources.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.synectiks.process.server.cluster.Node;
import com.synectiks.process.server.cluster.NodeNotFoundException;
import com.synectiks.process.server.cluster.NodeService;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.cluster.ClusterId;
import com.synectiks.process.server.plugin.system.NodeId;
import com.synectiks.process.server.rest.models.system.cluster.responses.NodeSummary;
import com.synectiks.process.server.rest.models.system.cluster.responses.NodeSummaryList;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.uuid.UUID;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "System/Cluster", description = "Node discovery")
@RequiresAuthentication
@Path("/system/cluster")
@Produces(MediaType.APPLICATION_JSON)
public class ClusterResource extends RestResource {
    private final NodeService nodeService;
    private final NodeId nodeId;
    private final ClusterId clusterId;

    @Inject
    public ClusterResource(NodeService nodeService,
                           ClusterConfigService clusterConfigService,
                           NodeId nodeId) {
        this.nodeService = nodeService;
        this.nodeId = nodeId;
        this.clusterId = clusterConfigService.getOrDefault(ClusterId.class, ClusterId.create(UUID.nilUUID().toString()));
    }

    @GET
    @Timed
    @Path("/nodes")
    @ApiOperation(value = "List all active nodes in this cluster.")
    public NodeSummaryList nodes() {
        final Map<String, Node> nodes = nodeService.allActive(Node.Type.SERVER);
        final List<NodeSummary> nodeList = new ArrayList<>(nodes.size());
        for (Node node : nodes.values()) {
            nodeList.add(nodeSummary(node));
        }

        return NodeSummaryList.create(nodeList);
    }

    @GET
    @Timed
    @Path("/node")
    @ApiOperation(value = "Information about this node.",
            notes = "This is returning information of this node in context to its state in the cluster. " +
                    "Use the system API of the node itself to get system information.")
    public NodeSummary node() throws NodeNotFoundException {
        return nodeSummary(nodeService.byNodeId(nodeId));
    }

    @GET
    @Timed
    @Path("/nodes/{nodeId}")
    @ApiOperation(value = "Information about a node.",
            notes = "This is returning information of a node in context to its state in the cluster. " +
                    "Use the system API of the node itself to get system information.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Node not found.")
    })
    public NodeSummary node(@ApiParam(name = "nodeId", required = true) @PathParam("nodeId") @NotEmpty String nodeId) throws NodeNotFoundException {
        return nodeSummary(nodeService.byNodeId(nodeId));
    }

    private NodeSummary nodeSummary(Node node) {
        return NodeSummary.create(
                clusterId.clusterId(),
                node.getNodeId(),
                node.getType().toString().toLowerCase(Locale.ENGLISH),
                node.isMaster(),
                node.getTransportAddress(),
                Tools.getISO8601String(node.getLastSeen()),
                node.getShortNodeId(),
                node.getHostname()
        );
    }
}
