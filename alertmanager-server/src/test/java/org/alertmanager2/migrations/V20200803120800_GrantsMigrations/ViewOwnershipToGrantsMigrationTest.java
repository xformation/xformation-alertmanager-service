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
package com.synectiks.process.server.migrations.V20200803120800_GrantsMigrations;

import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.grn.GRNRegistry;
import com.synectiks.process.common.grn.GRNTypes;
import com.synectiks.process.common.plugins.views.search.views.ViewRequirements;
import com.synectiks.process.common.plugins.views.search.views.ViewService;
import com.synectiks.process.common.security.Capability;
import com.synectiks.process.common.security.DBGrantService;
import com.synectiks.process.common.security.entities.EntityOwnershipService;
import com.synectiks.process.common.testing.GRNExtension;
import com.synectiks.process.common.testing.mongodb.MongoDBExtension;
import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.common.testing.mongodb.MongoDBTestService;
import com.synectiks.process.common.testing.mongodb.MongoJackExtension;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.database.users.User;
import com.synectiks.process.server.shared.users.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MongoDBExtension.class)
@ExtendWith(MongoJackExtension.class)
@ExtendWith(GRNExtension.class)
@ExtendWith(MockitoExtension.class)
@MongoDBFixtures("view-ownership.json")
class ViewOwnershipToGrantsMigrationTest {
    private ViewOwnerShipToGrantsMigration migration;
    private DBGrantService grantService;
    private UserService userService;

    @BeforeEach
    void setUp(MongoDBTestService mongodb,
               MongoJackObjectMapperProvider objectMapperProvider,
               GRNRegistry grnRegistry,
               @Mock ClusterConfigService clusterConfigService,
               @Mock UserService userService) {

        this.userService = userService;
        this.grantService = new DBGrantService(mongodb.mongoConnection(), objectMapperProvider, grnRegistry);

        final EntityOwnershipService entityOwnershipService = new EntityOwnershipService(grantService, grnRegistry);
        final TestViewService viewService = new TestViewService(mongodb.mongoConnection(), objectMapperProvider, clusterConfigService, entityOwnershipService);

        this.migration = new ViewOwnerShipToGrantsMigration(userService, grantService, "admin", viewService, grnRegistry);
    }

    @Test
    @DisplayName("migrate existing owner")
    void migrateExistingOwner() {

        final GRN testuserGRN = GRNTypes.USER.toGRN("testuser");
        final GRN dashboard = GRNTypes.DASHBOARD.toGRN("54e3deadbeefdeadbeef0002");

        final User testuser = mock(User.class);
        when(testuser.getName()).thenReturn("testuser");
        when(testuser.getId()).thenReturn("testuser");

        final User adminuser = mock(User.class);
        when(adminuser.isLocalAdmin()).thenReturn(true);

        when(userService.load("testuser")).thenReturn(testuser);
        when(userService.load("admin")).thenReturn(adminuser);

        migration.upgrade();
        assertThat(grantService.hasGrantFor(testuserGRN, Capability.OWN, dashboard)).isTrue();
    }

    @Test
    @DisplayName("don't migrate non-existing owner")
    void dontmigrateNonExistingOwner() {
        final GRN testuserGRN = GRNTypes.USER.toGRN("olduser");
        final GRN dashboard = GRNTypes.DASHBOARD.toGRN("54e3deadbeefdeadbeef0003");
        when(userService.load(anyString())).thenReturn(null);

        migration.upgrade();
        assertThat(grantService.hasGrantFor(testuserGRN, Capability.OWN, dashboard)).isFalse();
    }

    @Test
    @DisplayName("dont migrate admin owners")
    void dontMigrateAdminOwners() {

        final GRN testuserGRN = GRNTypes.USER.toGRN("testuser");
        final GRN search = GRNTypes.SEARCH.toGRN("54e3deadbeefdeadbeef0001");

        final User testuser = mock(User.class);
        when(testuser.getName()).thenReturn("testuser");
        when(testuser.getId()).thenReturn("testuser");

        final User adminuser = mock(User.class);
        when(adminuser.isLocalAdmin()).thenReturn(true);

        when(userService.load("testuser")).thenReturn(testuser);
        when(userService.load("admin")).thenReturn(adminuser);

        migration.upgrade();
        assertThat(grantService.hasGrantFor(testuserGRN, Capability.OWN, search)).isFalse();
    }

    public static class TestViewService extends ViewService {
        public TestViewService(MongoConnection mongoConnection,
                               MongoJackObjectMapperProvider mapper,
                               ClusterConfigService clusterConfigService,
                               EntityOwnershipService entityOwnerShipService) {
            super(mongoConnection, mapper, clusterConfigService, view -> new ViewRequirements(Collections.emptySet(), view), entityOwnerShipService);
        }
    }
}