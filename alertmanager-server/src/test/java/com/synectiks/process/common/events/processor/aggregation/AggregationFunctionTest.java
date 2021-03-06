/*
 * */
package com.synectiks.process.common.events.processor.aggregation;

import org.junit.Test;

import com.synectiks.process.common.events.processor.aggregation.AggregationFunction;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.SeriesSpec;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.Average;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.Cardinality;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.Count;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.Max;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.Min;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.StdDev;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.Sum;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.SumOfSquares;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.Variance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class AggregationFunctionTest {
    private void testToSeriesSpec(AggregationFunction function, Class<? extends SeriesSpec> specClass) {
        assertThat(function.toSeriesSpec("a", "b")).isInstanceOf(specClass);
    }

    @Test
    public void testFunctionMapping() {
        testToSeriesSpec(AggregationFunction.AVG, Average.class);
        testToSeriesSpec(AggregationFunction.CARD, Cardinality.class);
        testToSeriesSpec(AggregationFunction.COUNT, Count.class);
        testToSeriesSpec(AggregationFunction.MAX, Max.class);
        testToSeriesSpec(AggregationFunction.MIN, Min.class);
        testToSeriesSpec(AggregationFunction.STDDEV, StdDev.class);
        testToSeriesSpec(AggregationFunction.SUM, Sum.class);
        testToSeriesSpec(AggregationFunction.SUMOFSQUARES, SumOfSquares.class);
        testToSeriesSpec(AggregationFunction.VARIANCE, Variance.class);
    }

    @Test
    public void fieldRequirements() {
        assertThatCode(() -> AggregationFunction.AVG.toSeriesSpec("a", null))
                .hasMessageContaining("<avg>")
                .isInstanceOf(IllegalArgumentException.class);

        assertThatCode(() -> AggregationFunction.CARD.toSeriesSpec("a", null))
                .hasMessageContaining("<card>")
                .isInstanceOf(IllegalArgumentException.class);

        assertThatCode(() -> AggregationFunction.COUNT.toSeriesSpec("a", null))
                .doesNotThrowAnyException();

        assertThatCode(() -> AggregationFunction.MAX.toSeriesSpec("a", null))
                .hasMessageContaining("<max>")
                .isInstanceOf(IllegalArgumentException.class);

        assertThatCode(() -> AggregationFunction.MIN.toSeriesSpec("a", null))
                .hasMessageContaining("<min>")
                .isInstanceOf(IllegalArgumentException.class);

        assertThatCode(() -> AggregationFunction.STDDEV.toSeriesSpec("a", null))
                .hasMessageContaining("<stddev>")
                .isInstanceOf(IllegalArgumentException.class);

        assertThatCode(() -> AggregationFunction.SUM.toSeriesSpec("a", null))
                .hasMessageContaining("<sum>")
                .isInstanceOf(IllegalArgumentException.class);

        assertThatCode(() -> AggregationFunction.SUMOFSQUARES.toSeriesSpec("a", null))
                .hasMessageContaining("<sumofsquares>")
                .isInstanceOf(IllegalArgumentException.class);

        assertThatCode(() -> AggregationFunction.VARIANCE.toSeriesSpec("a", null))
                .hasMessageContaining("<variance>")
                .isInstanceOf(IllegalArgumentException.class);
    }
}