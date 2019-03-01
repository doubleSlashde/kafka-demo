package de.doubleslash.demo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * Kafka Consumer listening to the logging-counts topic.
 * <p>
 * This class uses the kafka integration of Spring Boot.
 */
@EnableKafka
@SpringBootApplication
public class KafkaSpringConsumerDemoApp {

    @Bean
    LogMessageCountConsumer consumer() {
        return new LogMessageCountConsumer();
    }

    public static void main(String[] args) {
        SpringApplication.run(KafkaSpringConsumerDemoApp.class);
    }

}

class LogMessageCountConsumer {

    Logger LOG = LoggerFactory.getLogger(LogMessageCountConsumer.class);

    @KafkaListener(topics = "logging-counts")
    public void consumeMessage(ConsumerRecord<String, Long> record) {
        LOG.info("{} => {}", record.key(), record.value());
    }

}
