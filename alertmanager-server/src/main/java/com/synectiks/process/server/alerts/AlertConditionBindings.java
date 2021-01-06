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
package com.synectiks.process.server.alerts;

import com.synectiks.process.server.alerts.types.FieldContentValueAlertCondition;
import com.synectiks.process.server.alerts.types.FieldValueAlertCondition;
import com.synectiks.process.server.alerts.types.MessageCountAlertCondition;
import com.synectiks.process.server.plugin.PluginModule;

public class AlertConditionBindings extends PluginModule {
    @Override
    protected void configure() {
        addAlertCondition(AbstractAlertCondition.Type.FIELD_CONTENT_VALUE.toString(),
            FieldContentValueAlertCondition.class,
            FieldContentValueAlertCondition.Factory.class);
        addAlertCondition(AbstractAlertCondition.Type.FIELD_VALUE.toString(),
            FieldValueAlertCondition.class,
            FieldValueAlertCondition.Factory.class);
        addAlertCondition(AbstractAlertCondition.Type.MESSAGE_COUNT.toString(),
            MessageCountAlertCondition.class,
            MessageCountAlertCondition.Factory.class);
    }
}
