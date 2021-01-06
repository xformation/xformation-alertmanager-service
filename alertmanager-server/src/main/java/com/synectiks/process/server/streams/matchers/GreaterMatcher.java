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
package com.synectiks.process.server.streams.matchers;

import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.streams.StreamRule;

import static com.synectiks.process.server.plugin.Tools.getDouble;

/**
 * @author Lennart Koopmann <lennart@socketfeed.com>
 */
public class GreaterMatcher implements StreamRuleMatcher {

	@Override
	public boolean match(Message msg, StreamRule rule) {
        Double msgVal = getDouble(msg.getField(rule.getField()));
        if (msgVal == null) {
            return false;
        }

        Double ruleVal = getDouble(rule.getValue());
        if (ruleVal == null) {
            return false;
        }

        return rule.getInverted() ^ (msgVal > ruleVal);
	}

}
