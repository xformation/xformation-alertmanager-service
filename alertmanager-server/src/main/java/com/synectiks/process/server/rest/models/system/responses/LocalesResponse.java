/*
 * */
package com.synectiks.process.server.rest.models.system.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@JsonAutoDetect
@AutoValue
public abstract class LocalesResponse {
    @JsonProperty("locales")
    public abstract ImmutableMap<String, LocaleDescription> locales();

    public static LocalesResponse create(Locale[] locales) {
        final Map<String, LocaleDescription> localeMap = new HashMap<>();
        Arrays.stream(locales)
                .map(LocaleDescription::create)
                .forEach(localeDescription -> localeMap.put(localeDescription.languageTag(), localeDescription));
        return new AutoValue_LocalesResponse(ImmutableMap.copyOf(localeMap));
    }

    @JsonAutoDetect
    @AutoValue
    public static abstract class LocaleDescription {
        @JsonProperty("language_tag")
        public abstract String languageTag();

        @JsonProperty("display_name")
        public abstract String displayName();

        public static LocaleDescription create(Locale locale) {
            return create(locale.toLanguageTag(), locale.getDisplayName(Locale.ENGLISH));
        }

        private static LocaleDescription create(String languageTag, String displayName) {
            return new AutoValue_LocalesResponse_LocaleDescription(languageTag, displayName);
        }
    }
}
