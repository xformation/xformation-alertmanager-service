/*
 * */
package com.synectiks.process.server.messageprocessors;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.synectiks.process.server.cluster.ClusterConfigChangedEvent;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.messageprocessors.MessageProcessor;
import com.synectiks.process.server.utilities.LenientExplicitOrdering;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents the current MessageProcessor ordering in the system.
 *
 * The order is configurable by writing a new MessageProcessorOrder into the ClusterConfig
 */
public class OrderedMessageProcessors implements Iterable<MessageProcessor> {

    private static final Logger LOG = LoggerFactory.getLogger(OrderedMessageProcessors.class);

    private final Set<MessageProcessor> processors;
    private final ClusterConfigService clusterConfigService;
    private final AtomicReference<List<MessageProcessor>> sortedProcessors =
            new AtomicReference<>(Collections.emptyList());
    private Ordering<String> classNameOrdering;

    @Inject
    public OrderedMessageProcessors(Set<MessageProcessor> processors,
                                    ClusterConfigService clusterConfigService,
                                    EventBus eventBus) {
        this.processors = processors;
        this.clusterConfigService = clusterConfigService;
        eventBus.register(this);
        // TODO by default sort on class name this is probably not the best idea, but for now works.
        this.classNameOrdering = Ordering.from(String.CASE_INSENSITIVE_ORDER);

        // Initial sort.
        sortProcessorChain();
    }

    private void sortProcessorChain() {
        final MessageProcessorsConfig config = clusterConfigService.get(MessageProcessorsConfig.class);

        if (config != null) {
            // if we have an explicit ordering use that (unknown last, partial ordering over the given list)
            classNameOrdering = new LenientExplicitOrdering<>(config.processorOrder());
        }
        final ImmutableList<MessageProcessor> sortedCopy =
                classNameOrdering.onResultOf(mp -> mp.getClass().getCanonicalName()).immutableSortedCopy(processors);

        final Collection<MessageProcessor> enabledMessageProcessors =
                Collections2.filter(sortedCopy,
                                    mp -> config == null || !config.disabledProcessors().contains(mp.getClass().getCanonicalName()));
        LOG.debug("New active message processors: {}", enabledMessageProcessors);
        sortedProcessors.set(ImmutableList.copyOf(enabledMessageProcessors));
    }

    @Subscribe
    public void handleOrderingUpdate(ClusterConfigChangedEvent event) {
        if (!MessageProcessorsConfig.class.getCanonicalName().equals(event.type())) {
            return;
        }

        sortProcessorChain();
    }

    @Override
    public Iterator<MessageProcessor> iterator() {
        return sortedProcessors.get().iterator();
    }

}
