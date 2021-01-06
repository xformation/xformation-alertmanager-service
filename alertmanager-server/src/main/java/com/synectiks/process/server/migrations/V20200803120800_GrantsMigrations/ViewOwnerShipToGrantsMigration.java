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
package com.synectiks.process.server.migrations.V20200803120800_GrantsMigrations;

import com.synectiks.process.common.grn.GRN;
import com.synectiks.process.common.grn.GRNRegistry;
import com.synectiks.process.common.grn.GRNType;
import com.synectiks.process.common.grn.GRNTypes;
import com.synectiks.process.common.plugins.views.search.views.ViewDTO;
import com.synectiks.process.common.plugins.views.search.views.ViewService;
import com.synectiks.process.common.security.Capability;
import com.synectiks.process.common.security.DBGrantService;
import com.synectiks.process.server.plugin.database.users.User;
import com.synectiks.process.server.shared.users.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.util.Optional;

public class ViewOwnerShipToGrantsMigration {
    private static final Logger LOG = LoggerFactory.getLogger(ViewOwnerShipToGrantsMigration.class);
    private final UserService userService;
    private final DBGrantService dbGrantService;
    private final String rootUsername;
    private final ViewService viewService;
    private final GRNRegistry grnRegistry;

    private static final Capability CAPABILITY = Capability.OWN;

    public ViewOwnerShipToGrantsMigration(UserService userService,
                                          DBGrantService dbGrantService,
                                          @Named("root_username") String rootUsername,
                                          ViewService viewService,
                                          GRNRegistry grnRegistry) {
        this.userService = userService;
        this.dbGrantService = dbGrantService;
        this.rootUsername = rootUsername;
        this.viewService = viewService;
        this.grnRegistry = grnRegistry;
    }

    public void upgrade() {
        viewService.streamAll().forEach(view -> {
            final Optional<User> user = view.owner().map(userService::load);
            if (user.isPresent() && !user.get().isLocalAdmin()) {
                final GRNType grnType = ViewDTO.Type.DASHBOARD.equals(view.type()) ? GRNTypes.DASHBOARD : GRNTypes.SEARCH;
                final GRN target = grnType.toGRN(view.id());

                ensureGrant(user.get(), target);
            }
        });
    }

    private void ensureGrant(User user, GRN target) {
        final GRN grantee = grnRegistry.ofUser(user);

        LOG.info("Registering user <{}/{}> ownership for <{}>", user.getName(), user.getId(), target);
        dbGrantService.ensure(grantee, CAPABILITY, target, rootUsername);
    }
}
