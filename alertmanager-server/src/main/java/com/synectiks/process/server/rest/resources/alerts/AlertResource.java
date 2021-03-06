/*
 * */
package com.synectiks.process.server.rest.resources.alerts;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.alerts.Alert;
import com.synectiks.process.server.alerts.AlertService;
import com.synectiks.process.server.alerts.Alert.AlertState;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.plugin.database.Persisted;
import com.synectiks.process.server.rest.models.streams.alerts.AlertListSummary;
import com.synectiks.process.server.rest.models.streams.alerts.AlertSummary;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.streams.StreamService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.inject.Inject;
import javax.validation.constraints.Min;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import static com.synectiks.process.server.shared.security.RestPermissions.STREAMS_READ;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiresAuthentication
@Api(value = "Alerts", description = "Manage stream legacy alerts for all streams")
@Path("/streams/alerts")
@Produces(MediaType.APPLICATION_JSON)
public class AlertResource extends RestResource {
    private final StreamService streamService;
    private final AlertService alertService;

    @Inject
    public AlertResource(StreamService streamService,
                               AlertService alertService) {
        this.streamService = streamService;
        this.alertService = alertService;
    }

    @GET
    @Timed
    @ApiOperation(value = "Get the most recent alarms of all streams.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ObjectId.")
    })
    public AlertListSummary listRecent(@ApiParam(name = "since", value = "Optional parameter to define a lower date boundary. (UNIX timestamp)", required = false)
                                       @QueryParam("since") @DefaultValue("0") @Min(0) int sinceTs,
                                       @ApiParam(name = "limit", value = "Maximum number of alerts to return.", required = false)
                                       @QueryParam("limit") @DefaultValue("300") @Min(1) int limit) throws NotFoundException {
        final DateTime since = new DateTime(sinceTs * 1000L, DateTimeZone.UTC);
        final List<AlertSummary> alerts = getAlertSummaries(alertService.loadRecentOfStreams(getAllowedStreamIds(), since, limit).stream());

        return AlertListSummary.create(alerts.size(), alerts);
    }

    @GET
    @Timed
    @Path("paginated")
    @ApiOperation(value = "Get alarms of all streams, filtered by specifying limit and offset parameters.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ObjectId."),
    })
    public AlertListSummary listPaginated(@ApiParam(name = "skip", value = "The number of elements to skip (offset).", required = true)
                                          @QueryParam("skip") @DefaultValue("0") int skip,
                                          @ApiParam(name = "limit", value = "The maximum number of elements to return.", required = true)
                                          @QueryParam("limit") @DefaultValue("300") int limit,
                                          @ApiParam(name = "state", value = "Alert state (resolved/unresolved)", required = false)
                                          @QueryParam("state") String state) {
        final List<String> allowedStreamIds = getAllowedStreamIds();

        AlertState alertState;
        try {
            alertState = AlertState.fromString(state);
        } catch (IllegalArgumentException e) {
            alertState = AlertState.ANY;
        }

        final Stream<Alert> alertsStream = alertService.listForStreamIds(allowedStreamIds, alertState, skip, limit).stream();
        final List<AlertSummary> alerts = getAlertSummaries(alertsStream);

        return AlertListSummary.create(alertService.totalCountForStreams(allowedStreamIds, alertState), alerts);
    }

    @GET
    @Timed
    @Path("{alertId}")
    @ApiOperation(value = "Get an alert by ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Alert not found."),
            @ApiResponse(code = 400, message = "Invalid ObjectId.")
    })
    public AlertSummary get(@ApiParam(name = "alertId", value = "The alert ID to retrieve.", required = true)
                            @PathParam("alertId") String alertId) throws NotFoundException {
        final Alert alert = alertService.load(alertId, "");

        checkPermission(STREAMS_READ, alert.getStreamId());

        return AlertSummary.create(
                alert.getId(),
                alert.getConditionId(),
                alert.getStreamId(),
                alert.getDescription(),
                alert.getConditionParameters(),
                alert.getTriggeredAt(),
                alert.getResolvedAt(),
                alert.isInterval());
    }

    private List<String> getAllowedStreamIds() {
        return streamService.loadAll().stream()
                .filter(stream -> isPermitted(STREAMS_READ, stream.getId()))
                .map(Persisted::getId)
                .collect(Collectors.toList());
    }

    private List<AlertSummary> getAlertSummaries(Stream<Alert> alertStream) {
        return alertStream
                .map(alert -> AlertSummary.create(
                        alert.getId(),
                        alert.getConditionId(),
                        alert.getStreamId(),
                        alert.getDescription(),
                        alert.getConditionParameters(),
                        alert.getTriggeredAt(),
                        alert.getResolvedAt(),
                        alert.isInterval()))
                .collect(Collectors.toList());
    }
}
