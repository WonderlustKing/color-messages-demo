package com.chrisb.colors.prj.demo.color;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

public class CustomColorRepositoryImpl implements CustomColorRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public CustomColorRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public AggregationResults<ColorCount> getColorCounts() {
        GroupOperation groupByColor = group("color").count().as("count");
        Aggregation aggregation = newAggregation(groupByColor);
        return mongoTemplate.aggregate(aggregation, "colors", ColorCount.class);
    }
}
