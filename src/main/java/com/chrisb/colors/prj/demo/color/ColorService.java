package com.chrisb.colors.prj.demo.color;

import com.chrisb.colors.prj.demo.redis.RedisUtility;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ColorService {

    private static final String ALL_TIME_DATA = "allTime";
    private final ColorRepository colorRepository;
    private final RedisUtility redisUtility;

    public ColorService(ColorRepository colorRepository, RedisUtility redisUtility) {
        this.colorRepository = colorRepository;
        this.redisUtility = redisUtility;
    }

    public List<ColorResource> getAllColors() {
        return colorRepository.findAll()
                .stream()
                .map(colorDomain -> new ColorResource(colorDomain.getColor()))
                .collect(Collectors.toList());
    }

    public ColorResource addColor(String color) {
        Color actualColor = Color.valueOf(color.toUpperCase());
        ColorDomain colorDomain = new ColorDomain();
        colorDomain.setColor(actualColor.name());
        colorDomain.setTimestamp(LocalDateTime.now());

        ColorDomain savedColor = colorRepository.save(colorDomain);
        return new ColorResource(savedColor.getColor());
    }

    public ColorCountResource getColorCounts(String data) {
        if (ALL_TIME_DATA.equalsIgnoreCase(data)) {
            return getColorCountsFromDB();
        }
        return getColorCountsFromCache();
    }

    private ColorCountResource getColorCountsFromDB() {
        ColorCountResource colorCountResource = new ColorCountResource();
        colorCountResource.setResults(colorRepository.getColorCounts().getMappedResults()
                                        .stream()
                                        .collect(Collectors.toMap(ColorCount::getId, ColorCount::getCount)));
        return colorCountResource;
    }

    private ColorCountResource getColorCountsFromCache() {
        List<String> colors = redisUtility.getAllValues();
        Map<String, Long> result = colors.stream()
                .collect(Collectors.groupingBy(color -> color, Collectors.counting()));

        ColorCountResource colorCountResource = new ColorCountResource();
        colorCountResource.setResults(result);
        return colorCountResource;
    }
}
