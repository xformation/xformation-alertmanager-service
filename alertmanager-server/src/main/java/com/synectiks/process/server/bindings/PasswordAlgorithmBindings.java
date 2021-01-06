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
package com.synectiks.process.server.bindings;

import com.google.inject.multibindings.MapBinder;
import com.synectiks.process.server.bindings.providers.DefaultPasswordAlgorithmProvider;
import com.synectiks.process.server.plugin.inject.Graylog2Module;
import com.synectiks.process.server.plugin.security.PasswordAlgorithm;
import com.synectiks.process.server.security.hashing.BCryptPasswordAlgorithm;
import com.synectiks.process.server.security.hashing.SHA1HashPasswordAlgorithm;
import com.synectiks.process.server.users.DefaultPasswordAlgorithm;

public class PasswordAlgorithmBindings extends Graylog2Module {
    @Override
    protected void configure() {
        bindPasswordAlgorithms();
    }

    private void bindPasswordAlgorithms() {
        MapBinder<String, PasswordAlgorithm> passwordAlgorithms = MapBinder.newMapBinder(binder(), String.class, PasswordAlgorithm.class);
        passwordAlgorithms.addBinding("sha-1").to(SHA1HashPasswordAlgorithm.class);
        passwordAlgorithms.addBinding("bcrypt").to(BCryptPasswordAlgorithm.class);

        bind(PasswordAlgorithm.class).annotatedWith(DefaultPasswordAlgorithm.class).toProvider(DefaultPasswordAlgorithmProvider.class);
    }
}
