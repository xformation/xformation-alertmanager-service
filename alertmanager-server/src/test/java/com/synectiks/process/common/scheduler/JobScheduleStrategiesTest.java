/*
 * */
package com.synectiks.process.common.scheduler;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import com.synectiks.process.common.events.JobSchedulerTestClock;
import com.synectiks.process.common.scheduler.JobScheduleStrategies;
import com.synectiks.process.common.scheduler.JobTriggerDto;
import com.synectiks.process.common.scheduler.schedule.IntervalJobSchedule;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class JobScheduleStrategiesTest {
    private JobSchedulerTestClock clock;
    private JobScheduleStrategies strategies;

    @Before
    public void setUp() throws Exception {
        this.clock = new JobSchedulerTestClock(DateTime.now(DateTimeZone.UTC));
        this.strategies = new JobScheduleStrategies(clock);
    }

    @Test
    public void nextTime() {
        final JobTriggerDto trigger = JobTriggerDto.builderWithClock(clock)
                .jobDefinitionId("abc-123")
                .schedule(IntervalJobSchedule.builder()
                        .interval(1)
                        .unit(TimeUnit.SECONDS)
                        .build())
                .build();

        final DateTime nextFutureTime1 = strategies.nextTime(trigger).orElse(null);

        assertThat(nextFutureTime1)
                .isNotNull()
                .isGreaterThanOrEqualTo(clock.nowUTC())
                .isEqualByComparingTo(clock.nowUTC().plusSeconds(1));

        clock.plus(10, TimeUnit.SECONDS);

        final DateTime nextFutureTime2 = strategies.nextTime(trigger).orElse(null);

        assertThat(nextFutureTime2)
                .isNotNull()
                .isEqualByComparingTo(trigger.nextTime().plusSeconds(1));
    }

    @Test
    public void nextFutureTime() {
        final JobTriggerDto trigger = JobTriggerDto.builderWithClock(clock)
                .jobDefinitionId("abc-123")
                .schedule(IntervalJobSchedule.builder()
                        .interval(1)
                        .unit(TimeUnit.SECONDS)
                        .build())
                .build();

        final DateTime nextFutureTime1 = strategies.nextFutureTime(trigger).orElse(null);

        assertThat(nextFutureTime1)
                .isNotNull()
                .isGreaterThanOrEqualTo(clock.nowUTC())
                .isEqualByComparingTo(clock.nowUTC().plusSeconds(1));

        clock.plus(10, TimeUnit.SECONDS);

        final DateTime nextFutureTime2 = strategies.nextFutureTime(trigger).orElse(null);

        assertThat(nextFutureTime2)
                .isNotNull()
                .isGreaterThanOrEqualTo(clock.nowUTC())
                .isEqualByComparingTo(clock.nowUTC().plusSeconds(1));
    }
}
