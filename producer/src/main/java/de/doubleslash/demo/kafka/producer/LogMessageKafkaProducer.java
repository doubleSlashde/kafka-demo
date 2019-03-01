package de.doubleslash.demo.kafka.producer;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import de.doubleslash.demo.kafka.avro.LogMessage;

/**
 * Writes log messages to Kafka.
 */
public class LogMessageKafkaProducer {

    private static final String TOPIC_LOGGING = "logging";

    private final Producer<String, LogMessage> producer;

    private final CountDownLatch latch = new CountDownLatch(1);

    public LogMessageKafkaProducer(Properties producerProperties) {
        this.producer = new KafkaProducer<>(producerProperties);
    }

    void produce(LogMessage logMessage) {
        producer.send(new ProducerRecord<>(TOPIC_LOGGING, UUID.randomUUID().toString(), logMessage));
    }

    public void shutdown() {
        this.producer.close(3, SECONDS);
        latch.countDown();
    }

}
