/*
 * */
package com.synectiks.process.server.rest.resources.system.indexer.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.plugin.indexer.retention.RetentionStrategyConfig;
import com.synectiks.process.server.plugin.indexer.rotation.RotationStrategyConfig;

import org.joda.time.Duration;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AutoValue
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class IndexSetUpdateRequest {
    @JsonProperty("title")
    @NotBlank
    public abstract String title();

    @JsonProperty("description")
    @Nullable
    public abstract String description();

    @JsonProperty("writable")
    public abstract boolean isWritable();

    @JsonProperty("shards")
    @Min(1)
    public abstract int shards();

    @JsonProperty("replicas")
    @Min(0)
    public abstract int replicas();

    @JsonProperty("rotation_strategy_class")
    @NotNull
    public abstract String rotationStrategyClass();

    @JsonProperty("rotation_strategy")
    @NotNull
    public abstract RotationStrategyConfig rotationStrategy();

    @JsonProperty("retention_strategy_class")
    @NotNull
    public abstract String retentionStrategyClass();

    @JsonProperty("retention_strategy")
    @NotNull
    public abstract RetentionStrategyConfig retentionStrategy();

    @JsonProperty("index_optimization_max_num_segments")
    @Min(1L)
    public abstract int indexOptimizationMaxNumSegments();

    @JsonProperty("index_optimization_disabled")
    public abstract boolean indexOptimizationDisabled();

    @JsonProperty("field_type_refresh_interval")
    public abstract Duration fieldTypeRefreshInterval();

    @JsonCreator
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static IndexSetUpdateRequest create(@JsonProperty("title") @NotBlank String title,
                                               @JsonProperty("description") @Nullable String description,
                                               @JsonProperty("writable") boolean isWritable,
                                               @JsonProperty("shards") @Min(1) int shards,
                                               @JsonProperty("replicas") @Min(0) int replicas,
                                               @JsonProperty("rotation_strategy_class") @NotNull String rotationStrategyClass,
                                               @JsonProperty("rotation_strategy") @NotNull RotationStrategyConfig rotationStrategy,
                                               @JsonProperty("retention_strategy_class") @NotNull String retentionStrategyClass,
                                               @JsonProperty("retention_strategy") @NotNull RetentionStrategyConfig retentionStrategy,
                                               @JsonProperty("index_optimization_max_num_segments") @Min(1L) int indexOptimizationMaxNumSegments,
                                               @JsonProperty("index_optimization_disabled") boolean indexOptimizationDisabled,
                                               @JsonProperty("field_type_refresh_interval") Duration fieldTypeRefreshInterval) {
        return new AutoValue_IndexSetUpdateRequest(title, description, isWritable, shards, replicas,
                rotationStrategyClass, rotationStrategy, retentionStrategyClass, retentionStrategy,
                indexOptimizationMaxNumSegments, indexOptimizationDisabled, fieldTypeRefreshInterval);
    }

    public static IndexSetUpdateRequest fromIndexSetConfig(IndexSetConfig indexSet) {
        return create(
                indexSet.title(),
                indexSet.description(),
                indexSet.isWritable(),
                indexSet.shards(),
                indexSet.replicas(),
                indexSet.rotationStrategyClass(),
                indexSet.rotationStrategy(),
                indexSet.retentionStrategyClass(),
                indexSet.retentionStrategy(),
                indexSet.indexOptimizationMaxNumSegments(),
                indexSet.indexOptimizationDisabled(),
                indexSet.fieldTypeRefreshInterval());

    }

    public IndexSetConfig toIndexSetConfig(String id, IndexSetConfig oldConfig) {
        return IndexSetConfig.builder()
                .id(id)
                .title(title())
                .description(description())
                .isWritable(isWritable())
                .indexPrefix(oldConfig.indexPrefix())
                .indexMatchPattern(oldConfig.indexMatchPattern())
                .indexWildcard(oldConfig.indexWildcard())
                .shards(shards())
                .replicas(replicas())
                .rotationStrategyClass(rotationStrategyClass())
                .rotationStrategy(rotationStrategy())
                .retentionStrategyClass(retentionStrategyClass())
                .retentionStrategy(retentionStrategy())
                .creationDate(oldConfig.creationDate())
                .indexAnalyzer(oldConfig.indexAnalyzer())
                .indexTemplateName(oldConfig.indexTemplateName())
                .indexTemplateType(oldConfig.indexTemplateType().orElse(null))
                .indexOptimizationMaxNumSegments(indexOptimizationMaxNumSegments())
                .indexOptimizationDisabled(indexOptimizationDisabled())
                .fieldTypeRefreshInterval(fieldTypeRefreshInterval())
                .build();
    }
}
