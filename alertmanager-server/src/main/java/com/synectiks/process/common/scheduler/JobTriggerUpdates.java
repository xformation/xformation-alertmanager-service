/*
 * */
package com.synectiks.process.common.scheduler;

import com.google.inject.assistedinject.Assisted;
import com.synectiks.process.common.scheduler.clock.JobSchedulerClock;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * Convenience factory to create {@link JobTriggerUpdate} objects.
 */
public class JobTriggerUpdates {
    public interface Factory {
        JobTriggerUpdates create(JobTriggerDto trigger);
    }

    private final JobSchedulerClock clock;
    private final JobScheduleStrategies scheduleStrategies;
    private final JobTriggerDto trigger;

    @Inject
    public JobTriggerUpdates(JobSchedulerClock clock,
                             JobScheduleStrategies scheduleStrategies,
                             @Assisted JobTriggerDto trigger) {
        this.clock = clock;
        this.scheduleStrategies = scheduleStrategies;
        this.trigger = trigger;
    }

    /**
     * Returns a job trigger update that instructs the scheduler to execute the trigger again based on its schedule
     * configuration.
     *
     * @return the job trigger update
     */
    public JobTriggerUpdate scheduleNextExecution() {
        return JobTriggerUpdate.withNextTime(scheduleStrategies.nextTime(trigger).orElse(null));
    }

    /**
     * Returns a job trigger update that instructs the scheduler to execute the trigger again based on its schedule
     * configuration. It also includes the given {@link JobTriggerData} object in the trigger update.
     *
     * @return the job trigger update
     */
    public JobTriggerUpdate scheduleNextExecution(JobTriggerData data) {
        return JobTriggerUpdate.withNextTimeAndData(scheduleStrategies.nextTime(trigger).orElse(null), data);
    }

    /**
     * Returns a job trigger update that instructs the scheduler to execute the trigger again in the future after
     * the given duration. (basically "time now" + duration)
     *
     * @param duration the duration to wait until executing the trigger again
     * @param unit     the duration unit
     * @return the job trigger update
     */
    public JobTriggerUpdate retryIn(long duration, TimeUnit unit) {
        return JobTriggerUpdate.withNextTime(clock.nowUTC().plus(unit.toMillis(duration)));
    }
}
