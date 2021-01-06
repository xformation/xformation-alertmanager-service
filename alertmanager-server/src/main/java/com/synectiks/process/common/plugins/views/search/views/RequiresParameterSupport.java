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
package com.synectiks.process.common.plugins.views.search.views;

import com.synectiks.process.common.plugins.views.Requirement;
import com.synectiks.process.common.plugins.views.search.Search;
import com.synectiks.process.common.plugins.views.search.SearchRequiresParameterSupport;
import com.synectiks.process.common.plugins.views.search.db.SearchDbService;
import com.synectiks.process.common.plugins.views.Requirement;
import com.synectiks.process.common.plugins.views.search.Search;
import com.synectiks.process.common.plugins.views.search.SearchRequiresParameterSupport;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

public class RequiresParameterSupport implements Requirement<ViewDTO> {
    private final SearchDbService searchDbService;
    private final SearchRequiresParameterSupport searchRequiresParameterSupport;

    @Inject
    public RequiresParameterSupport(SearchDbService searchDbService, SearchRequiresParameterSupport searchRequiresParameterSupport) {
        this.searchDbService = searchDbService;
        this.searchRequiresParameterSupport = searchRequiresParameterSupport;
    }

    @Override
    public Map<String, PluginMetadataSummary> test(ViewDTO view) {
        final Optional<Search> optionalSearch = searchDbService.get(view.searchId());
        return optionalSearch.map(searchRequiresParameterSupport::test)
                .orElseThrow(() -> new IllegalStateException("Search " + view.searchId() + " for view " + view + " is missing."));
    }
}
