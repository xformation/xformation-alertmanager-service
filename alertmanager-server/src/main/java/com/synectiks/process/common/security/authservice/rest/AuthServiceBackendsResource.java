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
package com.synectiks.process.common.security.authservice.rest;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.synectiks.process.common.security.SecurityAuditEventTypes;
import com.synectiks.process.common.security.authservice.AuthServiceBackendDTO;
import com.synectiks.process.common.security.authservice.AuthServiceBackendUsageCheck;
import com.synectiks.process.common.security.authservice.DBAuthServiceBackendService;
import com.synectiks.process.common.security.authservice.GlobalAuthServiceConfig;
import com.synectiks.process.server.audit.jersey.AuditEvent;
import com.synectiks.process.server.database.PaginatedList;
import com.synectiks.process.server.plugin.rest.ValidationFailureException;
import com.synectiks.process.server.plugin.rest.ValidationResult;
import com.synectiks.process.server.rest.PaginationParameters;
import com.synectiks.process.server.rest.models.PaginatedResponse;
import com.synectiks.process.server.search.SearchQuery;
import com.synectiks.process.server.search.SearchQueryField;
import com.synectiks.process.server.search.SearchQueryParser;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;
import com.synectiks.process.server.users.PaginatedUserService;
import com.synectiks.process.server.users.RoleService;
import com.synectiks.process.server.users.UserOverviewDTO;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

@Path("/system/authentication/services/backends")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "System/Authentication/Services/Backends", description = "Manage authentication service backends")
@RequiresAuthentication
public class AuthServiceBackendsResource extends RestResource {
    private static final ImmutableMap<String, SearchQueryField> SEARCH_FIELD_MAPPING = ImmutableMap.<String, SearchQueryField>builder()
            .put(UserOverviewDTO.FIELD_USERNAME, SearchQueryField.create(UserOverviewDTO.FIELD_USERNAME))
            .put(UserOverviewDTO.FIELD_FULL_NAME, SearchQueryField.create(UserOverviewDTO.FIELD_FULL_NAME))
            .put(UserOverviewDTO.FIELD_EMAIL, SearchQueryField.create(UserOverviewDTO.FIELD_EMAIL))
            .build();

    private final DBAuthServiceBackendService dbService;
    private final GlobalAuthServiceConfig globalAuthServiceConfig;
    private final PaginatedUserService userService;
    private final RoleService roleService;
    private final AuthServiceBackendUsageCheck usageCheck;
    private final SearchQueryParser userSearchQueryParser;

    @Inject
    public AuthServiceBackendsResource(DBAuthServiceBackendService dbService,
                                       GlobalAuthServiceConfig globalAuthServiceConfig,
                                       PaginatedUserService userService,
                                       RoleService roleService,
                                       AuthServiceBackendUsageCheck usageCheck) {
        this.dbService = dbService;
        this.globalAuthServiceConfig = globalAuthServiceConfig;
        this.userService = userService;
        this.roleService = roleService;
        this.usageCheck = usageCheck;
        this.userSearchQueryParser = new SearchQueryParser(UserOverviewDTO.FIELD_FULL_NAME, SEARCH_FIELD_MAPPING);
    }

    @GET
    @ApiOperation("Returns available authentication service backends")
    public PaginatedResponse<AuthServiceBackendDTO> list(@ApiParam(name = "pagination parameters") @BeanParam PaginationParameters paginationParameters) {
        final AuthServiceBackendDTO activeBackendConfig = globalAuthServiceConfig.getActiveBackendConfig()
                .filter(this::checkReadPermission)
                .orElse(null);
        final PaginatedList<AuthServiceBackendDTO> list = dbService.findPaginated(paginationParameters, this::checkReadPermission);

        return PaginatedResponse.create(
                "backends",
                list,
                Collections.singletonMap("active_backend", activeBackendConfig)
        );
    }

    @GET
    @Path("{backendId}")
    @ApiOperation("Returns the authentication service backend for the given ID")
    public Response get(@ApiParam(name = "backendId", required = true) @PathParam("backendId") @NotBlank String backendId) {
        checkPermission(RestPermissions.AUTH_SERVICE_BACKEND_READ, backendId);

        return toResponse(loadConfig(backendId));
    }

    @POST
    @ApiOperation("Creates a new authentication service backend")
    @RequiresPermissions(RestPermissions.AUTH_SERVICE_BACKEND_CREATE)
    @AuditEvent(type = SecurityAuditEventTypes.AUTH_SERVICE_BACKEND_CREATE)
    public Response create(@ApiParam(name = "JSON body", required = true) @NotNull AuthServiceBackendDTO newConfig) {
        validateConfig(newConfig);

        return toResponse(dbService.save(newConfig));
    }

    @PUT
    @Path("{backendId}")
    @ApiOperation("Updates an existing authentication service backend")
    @AuditEvent(type = SecurityAuditEventTypes.AUTH_SERVICE_BACKEND_UPDATE)
    public Response update(@ApiParam(name = "backendId", required = true) @PathParam("backendId") @NotBlank String backendId,
                           @ApiParam(name = "JSON body", required = true) @NotNull AuthServiceBackendDTO updatedConfig) {
        checkPermission(RestPermissions.AUTH_SERVICE_BACKEND_EDIT, backendId);
        validateConfig(updatedConfig);

        final AuthServiceBackendDTO currentConfig = loadConfig(backendId);

        return toResponse(dbService.save(updatedConfig.withId(currentConfig.id())));
    }

    @DELETE
    @Path("{backendId}")
    @ApiOperation("Delete authentication service backend")
    @AuditEvent(type = SecurityAuditEventTypes.AUTH_SERVICE_BACKEND_DELETE)
    public void delete(@ApiParam(name = "backendId", required = true) @PathParam("backendId") @NotBlank String backendId) {
        checkPermission(RestPermissions.AUTH_SERVICE_BACKEND_DELETE, backendId);

        final AuthServiceBackendDTO config = loadConfig(backendId);

        if (usageCheck.isAuthServiceInUse(backendId)) {
            throw new BadRequestException("Authentication service backend <" + backendId + "> is still in use");
        }
        dbService.delete(config.id());
    }

    @GET
    @Path("{backendId}/users")
    @ApiOperation("Get paginated users for an authentication service backend")
    @RequiresPermissions({RestPermissions.AUTH_SERVICE_GLOBAL_CONFIG_READ, RestPermissions.USERS_READ})
    public PaginatedResponse<UserOverviewDTO> getUsers(
            @ApiParam(name = "page") @QueryParam("page") @DefaultValue("1") int page,
            @ApiParam(name = "per_page") @QueryParam("per_page") @DefaultValue("50") int perPage,
            @ApiParam(name = "query") @QueryParam("query") @DefaultValue("") String query,
            @ApiParam(name = "sort", value = "The field to sort the result on", required = true, allowableValues = "username,full_name,email")
            @DefaultValue(UserOverviewDTO.FIELD_FULL_NAME) @QueryParam("sort") String sort,
            @ApiParam(name = "order", value = "The sort direction", allowableValues = "asc, desc")
            @DefaultValue("asc") @QueryParam("order") String order,
            @ApiParam(name = "backendId", required = true) @PathParam("backendId") @NotBlank String backendId
    ) {
        final AuthServiceBackendDTO activeConfig = loadConfig(backendId);

        final PaginatedList<UserOverviewDTO> userList = userService.findPaginatedByAuthServiceBackend(
                parseSearchQuery(query), page, perPage, sort, order, activeConfig.id());

        return PaginatedResponse.create(
                "users",
                userList,
                query,
                Collections.singletonMap("roles", createRoleContext(userList.delegate()))
        );
    }

    private Map<String, Object> createRoleContext(List<UserOverviewDTO> userList) {
        final Set<String> roleIds = userList.stream()
                .flatMap(user -> user.roles().stream())
                .collect(Collectors.toSet());
        try {
            return roleService.findIdMap(roleIds).values()
                    .stream()
                    .map(role -> {
                        final String roleName = isPermitted(RestPermissions.ROLES_READ, role.getId()) ? role.getName() : "unknown";
                        return Maps.immutableEntry(role.getId(), Collections.singletonMap("title", roleName));
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (com.synectiks.process.server.database.NotFoundException e) {
            throw new NotFoundException("Couldn't find roles: " + roleIds);
        }
    }

    private SearchQuery parseSearchQuery(String query) {
        try {
            return userSearchQueryParser.parse(query);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid argument in search query: " + e.getMessage());
        }
    }

    private boolean checkReadPermission(AuthServiceBackendDTO config) {
        return isPermitted(RestPermissions.AUTH_SERVICE_BACKEND_READ, config.id());
    }

    private AuthServiceBackendDTO loadConfig(String backendId) {
        checkArgument(!isNullOrEmpty(backendId), "backendId cannot be null or empty");

        return dbService.get(backendId)
                .orElseThrow(() -> new NotFoundException("Couldn't find auth service backend " + backendId));
    }

    private void validateConfig(AuthServiceBackendDTO config) {
        final ValidationResult result = config.validate();

        if (result.failed()) {
            throw new ValidationFailureException(result);
        }
    }

    private Response toResponse(Object entity) {
        return Response.ok(Collections.singletonMap("backend", entity)).build();
    }
}

