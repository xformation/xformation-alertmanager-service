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
package com.synectiks.process.server.rest.resources.system.inputs;

import com.synectiks.process.server.rest.models.system.inputs.responses.InputCreated;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputDeleted;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputStatesList;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RemoteInputStatesResource {
    @GET("system/inputstates")
    Call<InputStatesList> list();

    @PUT("system/inputstates/{inputId}")
    Call<InputCreated> start(@Path("inputId") String inputId);

    @DELETE("system/inputstates/{inputId}")
    Call<InputDeleted> stop(@Path("inputId") String inputId);
}
