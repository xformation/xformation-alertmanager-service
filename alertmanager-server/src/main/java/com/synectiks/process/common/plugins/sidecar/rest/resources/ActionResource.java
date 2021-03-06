/*
 * */
package com.synectiks.process.common.plugins.sidecar.rest.resources;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.common.plugins.sidecar.audit.SidecarAuditEventTypes;
import com.synectiks.process.common.plugins.sidecar.permissions.SidecarRestPermissions;
import com.synectiks.process.common.plugins.sidecar.rest.models.CollectorAction;
import com.synectiks.process.common.plugins.sidecar.rest.models.CollectorActions;
import com.synectiks.process.common.plugins.sidecar.services.ActionService;
import com.synectiks.process.server.audit.jersey.AuditEvent;
import com.synectiks.process.server.plugin.rest.PluginRestResource;
import com.synectiks.process.server.shared.rest.resources.RestResource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.hibernate.validator.constraints.NotEmpty;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Api(value = "Sidecar/Collector/Actions", description = "Manage Collector actions")
@Path("/sidecar/action")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiresAuthentication
public class ActionResource extends RestResource implements PluginRestResource {
    private final ActionService actionService;

    @Inject
    public ActionResource(ActionService actionService) {
        this.actionService = actionService;
    }

    @GET
    @Timed
    @Path("/{sidecarId}")
    @ApiOperation(value = "Returns queued actions for the specified Sidecar id")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "No actions found for specified id")
    })
    @RequiresPermissions(SidecarRestPermissions.SIDECARS_READ)
    public List<CollectorAction> getAction(@ApiParam(name = "sidecarId", required = true)
                                           @PathParam("sidecarId") @NotEmpty String sidecarId) {
        final CollectorActions collectorActions = actionService.findActionBySidecar(sidecarId, false);
        if (collectorActions != null) {
            return collectorActions.action();
        }
        return new ArrayList<>();
    }

    @PUT
    @Timed
    @Path("/{sidecarId}")
    @RequiresPermissions(SidecarRestPermissions.SIDECARS_UPDATE)
    @ApiOperation(value = "Set a collector action")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "The supplied action is not valid.")})
    @AuditEvent(type = SidecarAuditEventTypes.ACTION_UPDATE)
    public Response setAction(@ApiParam(name = "sidecarId", value = "The id this Sidecar is registering as.", required = true)
                              @PathParam("sidecarId") @NotEmpty String sidecarId,
                              @ApiParam(name = "JSON body", required = true)
                              @Valid @NotNull List<CollectorAction> request) {
        final CollectorActions collectorActions = actionService.fromRequest(sidecarId, request);
        actionService.saveAction(collectorActions);

        return Response.accepted().build();
    }
}
