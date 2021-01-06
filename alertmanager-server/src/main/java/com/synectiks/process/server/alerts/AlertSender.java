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

import org.apache.commons.mail.EmailException;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.alarms.AlertCondition;
import com.synectiks.process.server.plugin.alarms.transports.TransportConfigurationException;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.streams.Stream;

import java.util.List;

public interface AlertSender {
    void initialize(Configuration configuration);

    void sendEmails(Stream stream, EmailRecipients recipients, AlertCondition.CheckResult checkResult) throws TransportConfigurationException, EmailException;

    void sendEmails(Stream stream, EmailRecipients recipients, AlertCondition.CheckResult checkResult, List<Message> backlog) throws TransportConfigurationException, EmailException;
}
