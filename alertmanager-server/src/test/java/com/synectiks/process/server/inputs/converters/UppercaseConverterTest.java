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
package com.synectiks.process.server.inputs.converters;

import com.synectiks.process.server.plugin.inputs.Converter;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class UppercaseConverterTest {

    @Test
    public void testConvert() throws Exception {
        Converter c = new UppercaseConverter(new HashMap<String, Object>());

        assertNull(c.convert(null));
        assertEquals("", c.convert(""));
        assertEquals("FOOBAR", c.convert("foobar"));
        assertEquals("FOO BAR", c.convert("foo BAR"));
        assertEquals("FOOBAR", c.convert("FooBar"));
        assertEquals("FOOBAR ", c.convert("foobar "));
        assertEquals(" FOOBAR", c.convert(" foobar"));
        assertEquals("FOOBAR", c.convert("FOOBAR"));
    }

}
