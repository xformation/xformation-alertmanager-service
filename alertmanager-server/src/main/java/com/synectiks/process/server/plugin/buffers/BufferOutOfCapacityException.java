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
package com.synectiks.process.server.plugin.buffers;

/**
 *
 * @author Lennart Koopmann <lennart@socketfeed.com>
 */
public class BufferOutOfCapacityException extends Exception {
    
    /**
	 * Re-generate if you modify the class structure.
	 */
	private static final long serialVersionUID = -2497994875518554556L;

	public BufferOutOfCapacityException() {
        super();
    }
    
    public BufferOutOfCapacityException(String msg) {
        super(msg);
    }
    
}
