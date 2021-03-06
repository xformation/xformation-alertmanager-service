/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synectiks.process.common.plugins.pipelineprocessor.rest.PipelineSource;
import com.synectiks.process.common.plugins.pipelineprocessor.rest.StageSource;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class PipelineSourceTest {
    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Test
    public void testSerialization() throws Exception {
        final StageSource stageSource = StageSource.create(23, true, Collections.singletonList("some-rule"));
        final PipelineSource pipelineSource = PipelineSource.create(
                "id",
                "title",
                "description",
                "source",
                Collections.singletonList(stageSource),
                new DateTime(2017, 7, 4, 15, 0, DateTimeZone.UTC),
                new DateTime(2017, 7, 4, 15, 0, DateTimeZone.UTC)
        );
        final JsonNode json = objectMapper.convertValue(pipelineSource, JsonNode.class);

        assertThat(json.path("id").asText()).isEqualTo("id");
        assertThat(json.path("title").asText()).isEqualTo("title");
        assertThat(json.path("description").asText()).isEqualTo("description");
        assertThat(json.path("source").asText()).isEqualTo("source");
        assertThat(json.path("created_at").asText()).isEqualTo("2017-07-04T15:00:00.000Z");
        assertThat(json.path("modified_at").asText()).isEqualTo("2017-07-04T15:00:00.000Z");
        assertThat(json.path("stages").isArray()).isTrue();
        assertThat(json.path("stages")).hasSize(1);

        final JsonNode stageNode = json.path("stages").get(0);
        assertThat(stageNode.path("stage").asInt()).isEqualTo(23);
        assertThat(stageNode.path("match_all").asBoolean()).isTrue();
        assertThat(stageNode.path("rules").isArray()).isTrue();
        assertThat(stageNode.path("rules")).hasSize(1);
        assertThat(stageNode.path("rules").get(0).asText()).isEqualTo("some-rule");
    }

    @Test
    public void testDeserialization() throws Exception {
        final String json = "{"
                + "\"id\":\"id\","
                + "\"title\":\"title\","
                + "\"description\":\"description\","
                + "\"source\":\"source\","
                + "\"created_at\":\"2017-07-04T15:00:00.000Z\","
                + "\"modified_at\":\"2017-07-04T15:00:00.000Z\","
                + "\"stages\":[{\"stage\":23,\"match_all\":true,\"rules\":[\"some-rule\"]}]"
                + "}";

        final PipelineSource pipelineSource = objectMapper.readValue(json, PipelineSource.class);
        assertThat(pipelineSource.id()).isEqualTo("id");
        assertThat(pipelineSource.title()).isEqualTo("title");
        assertThat(pipelineSource.description()).isEqualTo("description");
        assertThat(pipelineSource.source()).isEqualTo("source");
        assertThat(pipelineSource.createdAt()).isEqualTo(new DateTime(2017, 7, 4, 15, 0, DateTimeZone.UTC));
        assertThat(pipelineSource.modifiedAt()).isEqualTo(new DateTime(2017, 7, 4, 15, 0, DateTimeZone.UTC));
        assertThat(pipelineSource.stages())
                .hasSize(1)
                .containsOnly(StageSource.create(23, true, Collections.singletonList("some-rule")));
    }
}
