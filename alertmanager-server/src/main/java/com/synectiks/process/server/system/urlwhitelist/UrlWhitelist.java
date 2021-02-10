/*
 * */
package com.synectiks.process.server.system.urlwhitelist;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class UrlWhitelist {

    @JsonProperty("entries")
    public abstract List<WhitelistEntry> entries();

    @JsonProperty("disabled")
    public abstract boolean disabled();

    @JsonCreator
    public static UrlWhitelist create(@JsonProperty("entries") List<WhitelistEntry> entries,
            @JsonProperty("disabled") boolean disabled) {
        return builder().entries(entries)
                .disabled(disabled)
                .build();
    }

    public static UrlWhitelist createEnabled(List<WhitelistEntry> entries) {
        return builder().entries(entries)
                .disabled(false)
                .build();
    }

    public abstract Builder toBuilder();

    /**
     * Checks if a URL is whitelisted by looking for a whitelist entry matching the given url.
     *
     * @param url The URL to check.
     * @return {@code false} if the whitelist is enabled and no whitelist entry matches the given url. {@code true} if
     *         there is a whitelist entry matching the given url or if the whitelist is disabled.
     */
    public boolean isWhitelisted(String url) {
        if (disabled()) {
            return true;
        }
        return entries().stream()
                .anyMatch(e -> e.isWhitelisted(url));
    }

    public static Builder builder() {
        return new AutoValue_UrlWhitelist.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder entries(List<WhitelistEntry> entries);

        public abstract Builder disabled(boolean disabled);

        public abstract UrlWhitelist autoBuild();

        public UrlWhitelist build() {
            final UrlWhitelist whitelist = autoBuild();
            checkForDuplicateIds(whitelist.entries());
            return whitelist;
        }

        private void checkForDuplicateIds(List<WhitelistEntry> entries) {
            Set<String> ids = new HashSet<>();
            entries.forEach(entry -> {
                if (ids.contains(entry.id())) {
                    throw new IllegalArgumentException(
                            "Found duplicate ID '" + entry.id() + "' in whitelist entries.");
                }
                ids.add(entry.id());
            });
        }
    }
}
