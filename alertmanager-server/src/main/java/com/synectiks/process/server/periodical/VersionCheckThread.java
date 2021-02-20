/*
 * */
package com.synectiks.process.server.periodical;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.net.HttpHeaders;
import com.synectiks.process.server.configuration.VersionCheckConfiguration;
import com.synectiks.process.server.notifications.Notification;
import com.synectiks.process.server.notifications.NotificationService;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.periodical.Periodical;
import com.synectiks.process.server.plugin.system.NodeId;
import com.synectiks.process.server.shared.ServerVersion;
import com.synectiks.process.server.versioncheck.VersionCheckResponse;
import com.synectiks.process.server.versioncheck.VersionResponse;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.concurrent.TimeUnit.MINUTES;

public class VersionCheckThread extends Periodical {
    private static final Logger LOG = LoggerFactory.getLogger(VersionCheckThread.class);
    private static final String USER_AGENT = String.format(Locale.ENGLISH, "alertmanager2-server (%s, %s, %s, %s)",
            System.getProperty("java.vendor"), System.getProperty("java.version"),
            System.getProperty("os.name"), System.getProperty("os.version"));

    private final NotificationService notificationService;
    private final ServerStatus serverStatus;
    private final VersionCheckConfiguration config;
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient;
    private final URI versionCheckUri;

    @Inject
    public VersionCheckThread(NotificationService notificationService,
                              ServerStatus serverStatus,
                              VersionCheckConfiguration config,
                              ObjectMapper objectMapper,
                              OkHttpClient httpClient) throws URISyntaxException {
        this(
                notificationService,
                serverStatus,
                config,
                objectMapper,
                httpClient,
                buildURI(serverStatus.getNodeId(), config.getUri())
        );
    }

    private static URI buildURI(NodeId nodeId, URI baseUri) throws URISyntaxException {
        final String queryParams = "anonid=" + nodeId.anonymize() + "&version=" + ServerVersion.VERSION.toString();
        return new URI(
                baseUri.getScheme(),
                baseUri.getUserInfo(),
                baseUri.getHost(),
                baseUri.getPort(),
                baseUri.getPath(),
                isNullOrEmpty(baseUri.getQuery()) ? queryParams : baseUri.getQuery() + "&" + queryParams,
                baseUri.getFragment()
        );
    }

    @VisibleForTesting
    VersionCheckThread(NotificationService notificationService,
                       ServerStatus serverStatus,
                       VersionCheckConfiguration config,
                       ObjectMapper objectMapper,
                       OkHttpClient httpClient,
                       URI versionCheckUri) {
        this.notificationService = notificationService;
        this.serverStatus = serverStatus;
        this.config = config;
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
        this.versionCheckUri = versionCheckUri;
    }

    @Override
    public void doRun() {
        final Request request = new Request.Builder()
                .addHeader(HttpHeaders.USER_AGENT, USER_AGENT)
                .get()
                .url(versionCheckUri.toString())
                .build();

        try (final Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                final VersionCheckResponse versionCheckResponse = objectMapper.readValue(response.body().byteStream(), VersionCheckResponse.class);

                final VersionResponse version = versionCheckResponse.version;
                final com.github.zafarkhaja.semver.Version reportedVersion = com.github.zafarkhaja.semver.Version.forIntegers(version.major, version.minor, version.patch);

                LOG.debug("Version check reports current version: " + versionCheckResponse);
                if (reportedVersion.greaterThan(ServerVersion.VERSION.getVersion())) {
                    LOG.debug("Reported version is higher than ours ({}). Writing notification.", ServerVersion.VERSION);

                    Notification notification = notificationService.buildNow()
                            .addSeverity(Notification.Severity.NORMAL)
                            .addType(Notification.Type.OUTDATED_VERSION)
                            .addDetail("current_version", versionCheckResponse.toString());
                    notificationService.publishIfFirst(notification);
                } else {
                    LOG.debug("Reported version is not higher than ours ({}).", ServerVersion.VERSION);
                    notificationService.fixed(Notification.Type.OUTDATED_VERSION);
                }
            } else {
                LOG.error("Version check unsuccessful (response code {}).", response.code());
            }
        } catch (IOException e) {
            LOG.error("Couldn't perform version check", e);
        }
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    public boolean runsForever() {
        return false;
    }

    @Override
    public boolean stopOnGracefulShutdown() {
        return true;
    }

    @Override
    public boolean masterOnly() {
        return true;
    }

    @Override
    public boolean startOnThisNode() {
        return config.isEnabled() && !serverStatus.hasCapability(ServerStatus.Capability.LOCALMODE);
    }

    @Override
    public boolean isDaemon() {
        return true;
    }

    @Override
    public int getInitialDelaySeconds() {
        return (int) MINUTES.toSeconds(5);
    }

    @Override
    public int getPeriodSeconds() {
        return (int) MINUTES.toSeconds(30);
    }
}
