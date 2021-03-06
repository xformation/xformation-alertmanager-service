/*
 * */
package com.synectiks.process.common.events.processor.aggregation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.events.JobSchedulerTestClock;
import com.synectiks.process.common.events.conditions.Expr;
import com.synectiks.process.common.events.conditions.Expression;
import com.synectiks.process.common.events.fields.providers.TemplateFieldValueProvider;
import com.synectiks.process.common.events.processor.DBEventDefinitionService;
import com.synectiks.process.common.events.processor.DBEventProcessorStateService;
import com.synectiks.process.common.events.processor.EventDefinitionDto;
import com.synectiks.process.common.events.processor.EventProcessorExecutionJob;
import com.synectiks.process.common.events.processor.aggregation.AggregationConditions;
import com.synectiks.process.common.events.processor.aggregation.AggregationEventProcessorConfig;
import com.synectiks.process.common.events.processor.aggregation.AggregationEventProcessorParameters;
import com.synectiks.process.common.events.processor.aggregation.AggregationFunction;
import com.synectiks.process.common.events.processor.aggregation.AggregationSeries;
import com.synectiks.process.common.events.processor.storage.PersistToStreamsStorageHandler;
import com.synectiks.process.common.scheduler.schedule.IntervalJobSchedule;
import com.synectiks.process.common.security.entities.EntityOwnershipService;
import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.common.testing.mongodb.MongoDBInstance;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.AbsoluteRange;
import com.synectiks.process.server.plugin.rest.ValidationResult;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class AggregationEventProcessorConfigTest {
    @Rule
    public final MongoDBInstance mongodb = MongoDBInstance.createForClass();

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private DBEventProcessorStateService stateService;

    private DBEventDefinitionService dbService;
    private JobSchedulerTestClock clock;

    @Before
    public void setUp() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapperProvider().get();
        objectMapper.registerSubtypes(new NamedType(AggregationEventProcessorConfig.class, AggregationEventProcessorConfig.TYPE_NAME));
        objectMapper.registerSubtypes(new NamedType(TemplateFieldValueProvider.Config.class, TemplateFieldValueProvider.Config.TYPE_NAME));
        objectMapper.registerSubtypes(new NamedType(PersistToStreamsStorageHandler.Config.class, PersistToStreamsStorageHandler.Config.TYPE_NAME));

        final MongoJackObjectMapperProvider mapperProvider = new MongoJackObjectMapperProvider(objectMapper);
        this.dbService = new DBEventDefinitionService(mongodb.mongoConnection(), mapperProvider, stateService, mock(EntityOwnershipService.class));
        this.clock = new JobSchedulerTestClock(DateTime.now(DateTimeZone.UTC));
    }

    @Test
    @MongoDBFixtures("aggregation-processors.json")
    public void toJobSchedulerConfig() {
        final EventDefinitionDto dto = dbService.get("54e3deadbeefdeadbeefaffe").orElse(null);

        assertThat(dto).isNotNull();

        assertThat(dto.config().toJobSchedulerConfig(dto, clock)).isPresent().get().satisfies(schedulerConfig -> {
            assertThat(schedulerConfig.jobDefinitionConfig()).satisfies(jobDefinitionConfig -> {
                assertThat(jobDefinitionConfig).isInstanceOf(EventProcessorExecutionJob.Config.class);

                final EventProcessorExecutionJob.Config config = (EventProcessorExecutionJob.Config) jobDefinitionConfig;

                assertThat(config.eventDefinitionId()).isEqualTo(dto.id());
                assertThat(config.processingWindowSize()).isEqualTo(300000);
                assertThat(config.processingHopSize()).isEqualTo(300000);
                assertThat(config.parameters()).isEqualTo(AggregationEventProcessorParameters.builder()
                        .timerange(AbsoluteRange.create(clock.nowUTC().minus(300000), clock.nowUTC()))
                        .build());
            });

            assertThat(schedulerConfig.schedule()).satisfies(schedule -> {
                assertThat(schedule).isInstanceOf(IntervalJobSchedule.class);

                final IntervalJobSchedule config = (IntervalJobSchedule) schedule;

                assertThat(config.interval()).isEqualTo(300000);
                assertThat(config.unit()).isEqualTo(TimeUnit.MILLISECONDS);
            });
        });
    }

    private AggregationEventProcessorConfig getConfig() {
        return AggregationEventProcessorConfig.builder()
            .query("")
            .streams(new HashSet<>())
            .groupBy(new ArrayList<>())
            .series(new ArrayList<>())
            .searchWithinMs(1)
            .executeEveryMs(1)
            .build();
    }

    private AggregationConditions getConditions() {
        final Expression<Boolean> expression = Expr.Greater.create(Expr.NumberReference.create("foo"),
            Expr.NumberValue.create(42.0));
        return AggregationConditions.builder()
            .expression(expression)
            .build();
    }

    private AggregationSeries getSeries() {
        return AggregationSeries.builder()
            .id("123")
            .field("foo")
            .function(AggregationFunction.AVG)
            .build();
    }

    @Test
    public void testValidateWithInvalidTimeRange() {
        final AggregationEventProcessorConfig invalidConfig1 = getConfig().toBuilder()
            .searchWithinMs(-1)
            .build();

        final ValidationResult validationResult1 = invalidConfig1.validate();
        assertThat(validationResult1.failed()).isTrue();
        assertThat(validationResult1.getErrors()).containsOnlyKeys("search_within_ms");

        final AggregationEventProcessorConfig invalidConfig2 = invalidConfig1.toBuilder()
            .searchWithinMs(0)
            .build();

        final ValidationResult validationResult2 = invalidConfig2.validate();
        assertThat(validationResult2.failed()).isTrue();
        assertThat(validationResult2.getErrors()).containsOnlyKeys("search_within_ms");
    }

    @Test
    public void testValidateWithInvalidExecutionTime() {
        final AggregationEventProcessorConfig invalidConfig1 = getConfig().toBuilder()
            .executeEveryMs(-1)
            .build();

        final ValidationResult validationResult1 = invalidConfig1.validate();
        assertThat(validationResult1.failed()).isTrue();
        assertThat(validationResult1.getErrors()).containsOnlyKeys("execute_every_ms");

        final AggregationEventProcessorConfig invalidConfig2 = invalidConfig1.toBuilder()
            .executeEveryMs(0)
            .build();

        final ValidationResult validationResult2 = invalidConfig2.validate();
        assertThat(validationResult2.failed()).isTrue();
        assertThat(validationResult2.getErrors()).containsOnlyKeys("execute_every_ms");
    }

    @Test
    public void testValidateWithIncompleteAggregationOptions() {
        AggregationEventProcessorConfig invalidConfig = getConfig().toBuilder()
            .groupBy(ImmutableList.of("foo"))
            .build();

        ValidationResult validationResult = invalidConfig.validate();
        assertThat(validationResult.failed()).isTrue();
        assertThat(validationResult.getErrors()).containsOnlyKeys("series", "conditions");

        invalidConfig = getConfig().toBuilder()
            .series(ImmutableList.of(this.getSeries()))
            .build();

        validationResult = invalidConfig.validate();
        assertThat(validationResult.failed()).isTrue();
        assertThat(validationResult.getErrors()).containsOnlyKeys("conditions");

        invalidConfig = getConfig().toBuilder()
            .conditions(this.getConditions())
            .build();

        validationResult = invalidConfig.validate();
        assertThat(validationResult.failed()).isTrue();
        assertThat(validationResult.getErrors()).containsOnlyKeys("series");
    }

    @Test
    public void testValidConfiguration() {
        final ValidationResult validationResult = getConfig().validate();
        assertThat(validationResult.failed()).isFalse();
        assertThat(validationResult.getErrors().size()).isEqualTo(0);
    }

    @Test
    public void testValidFilterConfiguration() {
        final AggregationEventProcessorConfig config = getConfig().toBuilder()
            .query("foo")
            .streams(ImmutableSet.of("1", "2"))
            .build();

        final ValidationResult validationResult = config.validate();
        assertThat(validationResult.failed()).isFalse();
        assertThat(validationResult.getErrors().size()).isEqualTo(0);
    }

    @Test
    public void testValidAggregationConfiguration() {
        final AggregationEventProcessorConfig config = getConfig().toBuilder()
            .groupBy(ImmutableList.of("bar"))
            .series(ImmutableList.of(this.getSeries()))
            .conditions(this.getConditions())
            .build();

        final ValidationResult validationResult = config.validate();
        assertThat(validationResult.failed()).isFalse();
        assertThat(validationResult.getErrors().size()).isEqualTo(0);
    }

    @Test
    @MongoDBFixtures("aggregation-processors.json")
    public void requiredPermissions() {
        assertThat(dbService.get("54e3deadbeefdeadbeefaffe")).get().satisfies(definition -> {
            assertThat(definition.config().requiredPermissions()).containsOnly("streams:read:stream-a", "streams:read:stream-b");
        });
    }

    @Test
    @MongoDBFixtures("aggregation-processors.json")
    public void requiredPermissionsWithEmptyStreams() {
        assertThat(dbService.get("54e3deadbeefdeadbeefafff")).get().satisfies(definition -> {
            assertThat(definition.config().requiredPermissions()).containsOnly("streams:read");
        });
    }
}
