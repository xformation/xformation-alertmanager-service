/*
 * */
package com.synectiks.process.server.inputs.transports;

import com.github.joschi.jadconfig.Parameter;
import com.github.joschi.jadconfig.validators.PositiveIntegerValidator;
import com.github.joschi.jadconfig.validators.StringNotBlankValidator;
import com.google.common.annotations.VisibleForTesting;
import com.synectiks.process.server.inputs.transports.netty.NettyTransportType;

import io.netty.channel.epoll.Epoll;
import io.netty.channel.kqueue.KQueue;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class NettyTransportConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(NettyTransportConfiguration.class);
    private static final String PREFIX = "transport_netty_";

    @Parameter(value = PREFIX + "type", required = true, validators = StringNotBlankValidator.class)
    private String type = "auto";

    @Parameter(value = PREFIX + "tls_provider", required = true, validators = StringNotBlankValidator.class)
    private String tlsProvider = "auto";

    @Parameter(value = PREFIX + "num_threads", required = true, validators = PositiveIntegerValidator.class)
    private int numThreads = Runtime.getRuntime().availableProcessors() * 2;

    public NettyTransportConfiguration() {
    }

    @VisibleForTesting
    public NettyTransportConfiguration(String type, String tlsProvider, int numThreads) {
        this.type = type;
        this.tlsProvider = tlsProvider;
        this.numThreads = numThreads;
    }

    public NettyTransportType getType() {
        switch (type.toLowerCase(Locale.ROOT)) {
            case "epoll":
                return NettyTransportType.EPOLL;
            case "kqueue":
                return NettyTransportType.KQUEUE;
            case "nio":
                return NettyTransportType.NIO;
            case "auto":
            default:
                return detectPlatform();
        }
    }

    private NettyTransportType detectPlatform() {
        if (Epoll.isAvailable()) {
            LOG.debug("Using epoll for Netty transport.");
            return NettyTransportType.EPOLL;
        } else if (KQueue.isAvailable()) {
            LOG.debug("Using kqueue for Netty transport.");
            return NettyTransportType.KQUEUE;
        } else {
            LOG.debug("Using NIO for Netty transport.");
            return NettyTransportType.NIO;
        }
    }

    public SslProvider getTlsProvider() {
        switch (tlsProvider.toLowerCase(Locale.ROOT)) {
            case "openssl":
                return SslProvider.OPENSSL;
            case "jdk":
                return SslProvider.JDK;
            case "auto":
            default:
                return detectTlsProvider();
        }
    }

    private SslProvider detectTlsProvider() {
        if (OpenSsl.isAvailable()) {
            LOG.debug("Using OpenSSL for Netty transports.");
            return SslProvider.OPENSSL;
        } else {
            LOG.debug("Using default Java TLS provider for Netty transports.");
            return SslProvider.JDK;
        }
    }

    public int getNumThreads() {
        return numThreads;
    }
}
