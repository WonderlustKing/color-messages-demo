package com.chrisb.colors.prj.demo.kafka;

import com.chrisb.colors.prj.demo.color.Color;
import com.chrisb.colors.prj.demo.color.ColorService;
import com.chrisb.colors.prj.demo.redis.RedisUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
public class Listener {
    private static final Logger LOG = LoggerFactory.getLogger(Listener.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private CountDownLatch latch = new CountDownLatch(1);
    public CountDownLatch getLatch() {
        return latch;
    }

    private final RedisUtility redisUtility;

    private final ColorService colorService;

    @Autowired
    public Listener(RedisUtility redisUtility, ColorService colorService) {
        this.redisUtility = redisUtility;
        this.colorService = colorService;
    }

    @KafkaListener(id = "listener-batch", topics = "${app.topic.batch}")
    public void receive(@Payload List<String> messages,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        LOG.info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        LOG.info("Starting the process to receive batch color-messages");

        String now = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        for (int i = 0; i < messages.size(); i++) {
            LOG.info("received message='{}' with partition-offset='{}'",
                    messages.get(i), partitions.get(i) + "-" + offsets.get(i));

            String message = messages.get(i);
            if (Color.isValidColor(message)) {
                addColorToRedisAndMongo(now, message);
            }
        }
        LOG.info("all batch messages are consumed");
        latch.countDown();
    }

    @Transactional
    private void addColorToRedisAndMongo(String time, String color) {
        redisUtility.setValue(time, color);
        colorService.addColor(color);
        LOG.info("Color '{}' saved successfully into Redis & MongoDB", color);
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }
}
