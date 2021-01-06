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
package com.synectiks.process.server.database.validators;

import com.synectiks.process.server.plugin.database.validators.Validator;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class DateValidatorTest {

    @Test
    public void testValidate() throws Exception {
        Validator v = new DateValidator();

        assertFalse(v.validate(null).passed());
        assertFalse(v.validate(9001).passed());
        assertFalse(v.validate("").passed());
        assertFalse(v.validate(new java.util.Date()).passed());

        // Only joda datetime.
        assertTrue(v.validate(new org.joda.time.DateTime(DateTimeZone.UTC)).passed());

        // Only accepts UTC.
        assertFalse(v.validate(new org.joda.time.DateTime(DateTimeZone.forID("+09:00"))).passed());
    }
}
