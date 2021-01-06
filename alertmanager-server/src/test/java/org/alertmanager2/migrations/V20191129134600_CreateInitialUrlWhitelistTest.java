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
package com.synectiks.process.server.migrations;

import com.synectiks.process.common.events.notifications.DBNotificationService;
import com.synectiks.process.server.lookup.adapters.HTTPJSONPathDataAdapter;
import com.synectiks.process.server.lookup.db.DBDataAdapterService;
import com.synectiks.process.server.lookup.dto.DataAdapterDto;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.system.urlwhitelist.RegexHelper;
import com.synectiks.process.server.system.urlwhitelist.UrlWhitelist;
import com.synectiks.process.server.system.urlwhitelist.UrlWhitelistService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class V20191129134600_CreateInitialUrlWhitelistTest {
    @Mock
    private ClusterConfigService configService;
    @Mock
    private UrlWhitelistService whitelistService;
    @Mock
    private DBDataAdapterService dataAdapterService;
    @Mock
    private DBNotificationService notificationService;
    @Spy
    private RegexHelper regexHelper;

    @InjectMocks
    private V20191129134600_CreateInitialUrlWhitelist migration;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createQuotedRegexEntry() {
        final HTTPJSONPathDataAdapter.Config config = mock(HTTPJSONPathDataAdapter.Config.class);
        when(config.url()).thenReturn("https://www.graylog.com/${key}/test.json/${key}");
        final DataAdapterDto dataAdapterDto = mock(DataAdapterDto.class);
        when(dataAdapterDto.config()).thenReturn(config);
        when(dataAdapterService.findAll()).thenReturn(Collections.singleton(dataAdapterDto));

        migration.upgrade();

        final ArgumentCaptor<UrlWhitelist> captor = ArgumentCaptor.forClass(UrlWhitelist.class);
        verify(whitelistService).saveWhitelist(captor.capture());

        final UrlWhitelist whitelist = captor.getValue();

        final String whitelisted = "https://www.graylog.com/message/test.json/message";
        final String notWhitelisted = "https://wwwXgraylogXcom/message/testXjson/messsage";

        assertThat(whitelist.isWhitelisted(whitelisted)).withFailMessage(
                "Whitelist " + whitelist + " is expected to consider url <" + whitelisted + "> whitelisted.")
                .isTrue();
        assertThat(whitelist.isWhitelisted(notWhitelisted)).withFailMessage(
                "Whitelist " + whitelist + " is expected to consider url <" + notWhitelisted + "> not whitelisted.")
                .isFalse();
        assertThat(whitelist.entries()
                .size()).isEqualTo(1);
        assertThat(whitelist.entries()
                .get(0)
                .value()).isEqualTo("^\\Qhttps://www.graylog.com/\\E.*?\\Q/test.json/\\E.*?$");
    }
}
