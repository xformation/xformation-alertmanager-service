/*
 * */
package com.synectiks.process.common.plugins.views.search.searchtypes.pivot.buckets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;

@AutoValue
@JsonTypeName(TimeUnitInterval.type)
@JsonDeserialize(builder = TimeUnitInterval.Builder.class)
public abstract class TimeUnitInterval implements Interval {
    public static final String type = "timeunit";
    public static final Pattern TIMEUNIT_PATTERN = Pattern.compile("(?<quantity>\\d+)(?<unit>[smhdwM])");

    @JsonProperty
    public abstract String type();

    @JsonProperty
    public abstract String timeunit();

    @Override
    public DateInterval toDateInterval(TimeRange timerange) {
        return adjustUnitsLongerThanDays(timeunit());
    }

    private DateInterval adjustUnitsLongerThanDays(String timeunit) {
        final Matcher matcher = TIMEUNIT_PATTERN.matcher(timeunit());
        checkArgument(matcher.matches(),
                "Time unit must be {quantity}{unit}, where quantity is a positive number and unit [smhdwM].");
        final int quantity = Integer.parseInt(matcher.group("quantity"));
        final String unit = matcher.group("unit");

        switch (unit) {
            case "s":
            case "m":
            case "h":
            case "d": return new DateInterval(quantity, unit);
            case "w": return quantity == 1 ? new DateInterval(quantity, unit) : DateInterval.days(7 * quantity);
            case "M": return quantity == 1 ? new DateInterval(quantity, unit) : DateInterval.days(30 * quantity);
            default: throw new RuntimeException("Invalid time unit: " + timeunit);
        }
    }

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonProperty("type")
        public abstract Builder type(String type);

        @JsonProperty("timeunit")
        public abstract Builder timeunit(String timeunit);

        abstract TimeUnitInterval autoBuild();
        public TimeUnitInterval build() {
            final TimeUnitInterval interval = autoBuild();
            final Matcher matcher = TIMEUNIT_PATTERN.matcher(interval.timeunit());
            checkArgument(matcher.matches(),
                    "Time unit must be {quantity}{unit}, where quantity is a positive number and unit [smhdwM].");

            final int quantity = Integer.parseInt(matcher.group("quantity"), 10);
            checkArgument(quantity > 0,
                    "Time unit's value must be a positive number, greater than zero.");

            return interval;
        }

        @JsonCreator
        public static Builder builder() {
            return new AutoValue_TimeUnitInterval.Builder().type(type);
        }

        @JsonCreator
        public static Builder createForLegacySingleString(String timeunit) {
            return builder().timeunit(timeunit);
        }
    }
}
