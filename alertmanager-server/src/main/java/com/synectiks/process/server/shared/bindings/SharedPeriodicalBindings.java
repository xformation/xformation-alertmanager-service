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
package com.synectiks.process.server.shared.bindings;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.synectiks.process.server.periodical.ThroughputCalculator;
import com.synectiks.process.server.plugin.periodical.Periodical;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public class SharedPeriodicalBindings extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<Periodical> periodicalBinder = Multibinder.newSetBinder(binder(), Periodical.class);
        periodicalBinder.addBinding().to(ThroughputCalculator.class);

    }
}
