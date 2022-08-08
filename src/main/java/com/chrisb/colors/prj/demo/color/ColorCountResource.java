package com.chrisb.colors.prj.demo.color;

import io.swagger.annotations.ApiModelProperty;

import java.util.HashMap;
import java.util.Map;

public class ColorCountResource {
    @ApiModelProperty(notes = "Color Count Results", example = "{'BLACK': 6, 'GREEN': 3, 'RED': 1}")
    private Map<String, Long> results = new HashMap<>();

    public Map<String, Long> getResults() {
        return results;
    }

    public void setResults(Map<String, Long> results) {
        this.results = results;
    }
}
