package com.chrisb.colors.prj.demo;

import com.chrisb.colors.prj.demo.color.Color;
import com.chrisb.colors.prj.demo.color.ColorRepository;
import com.chrisb.colors.prj.demo.kafka.Listener;
import com.chrisb.colors.prj.demo.redis.RedisUtility;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class TestContainers {

    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));
    static {
        kafkaContainer.start();
    }

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
    static {
        mongoDBContainer.start();
    }

    private GenericContainer redisContainer;

    private KafkaEventProducer kafkaEventProducer;

    @Autowired
    private Listener kafkaListener;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private RedisUtility redisUtility;

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @BeforeAll
    public void setup() {
        String bootstrapServers = kafkaContainer.getBootstrapServers();
        kafkaEventProducer = new KafkaEventProducer(bootstrapServers);

        redisContainer = new GenericContainer<>(DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379);
        redisContainer.start();
        System.setProperty("spring.redis.host", redisContainer.getHost());
        System.setProperty("spring.redis.port", redisContainer.getMappedPort(6379).toString());
    }

    @AfterAll
    public void finish() {
        redisUtility.deleteAll();
        colorRepository.deleteAll();
    }

    @Test
    public void sendTwoColorMessages_andExpect_twoColorsBothInCacheAndDB() throws Exception {
        kafkaEventProducer.send(Color.GREEN.toString());
        kafkaEventProducer.send(Color.BLACK.toString());

        boolean messageConsumed = kafkaListener.getLatch().await(10, TimeUnit.SECONDS);
        assertTrue(messageConsumed);

        assertEquals(2, redisUtility.getAllValues().size());
        assertEquals(2, colorRepository.findAll().size());

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/colors/count"));
        response.andExpect(MockMvcResultMatchers.status().isOk());
        response.andExpect(MockMvcResultMatchers.jsonPath("$.results.size()", CoreMatchers.is(2)));

    }

}
