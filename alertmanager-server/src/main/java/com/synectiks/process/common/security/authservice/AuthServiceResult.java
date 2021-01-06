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
package com.synectiks.process.common.security.authservice;

import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;

import static com.google.common.base.Strings.isNullOrEmpty;

@AutoValue
public abstract class AuthServiceResult {
    public abstract String username();

    @Nullable
    public abstract String userProfileId();

    public abstract String backendId();

    public abstract String backendType();

    public abstract String backendTitle();

    public boolean isSuccess() {
        return !isNullOrEmpty(userProfileId());
    }

    public static Builder builder() {
        return new AutoValue_AuthServiceResult.Builder();
    }

    public static AuthServiceResult failed(String username, AuthServiceBackend backend) {
        return builder()
                .username(username)
                .backendId(backend.backendId())
                .backendType(backend.backendType())
                .backendTitle(backend.backendTitle())
                .build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder username(String username);

        public abstract Builder userProfileId(String userProfileId);

        public abstract Builder backendId(String backendId);

        public abstract Builder backendType(String backendType);

        public abstract Builder backendTitle(String backendTitle);

        public abstract AuthServiceResult build();
    }
}
