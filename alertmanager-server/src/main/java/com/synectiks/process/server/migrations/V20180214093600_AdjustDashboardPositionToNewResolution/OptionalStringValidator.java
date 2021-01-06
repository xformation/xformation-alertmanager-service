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
package com.synectiks.process.server.migrations.V20180214093600_AdjustDashboardPositionToNewResolution;

import com.synectiks.process.server.plugin.database.validators.ValidationResult;
import com.synectiks.process.server.plugin.database.validators.Validator;

class OptionalStringValidator implements Validator {
    /**
     * Validates: Object is null or of type String.
     *
     * @param value The object to check
     * @return validation result
     */
    @Override
    public ValidationResult validate(Object value) {
        if (value == null || value instanceof String) {
            return new ValidationResult.ValidationPassed();
        } else {
            return new ValidationResult.ValidationFailed("Value \"" + value + "\" is not a valid string!");
        }
    }
}
