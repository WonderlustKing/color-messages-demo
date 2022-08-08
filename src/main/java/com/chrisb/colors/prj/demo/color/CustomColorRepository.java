package com.chrisb.colors.prj.demo.color;

import org.springframework.data.mongodb.core.aggregation.AggregationResults;

public interface CustomColorRepository {
    AggregationResults<ColorCount> getColorCounts();
}
