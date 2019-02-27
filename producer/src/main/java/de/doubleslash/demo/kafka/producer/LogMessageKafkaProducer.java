package de.doubleslash.demo.kafka.producer;

import static io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import de.doubleslash.demo.kafka.avro.LogMessage;
import de.doubleslash.demo.kafka.producer.config.KafkaProducerConfiguration;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

/**
 * Writes log messages to Kafka.
 */
public class LogMessageKafkaProducer {

    private static final String TOPIC_LOGGING = "logging";

    private final Producer<String, LogMessage> producer;

    public LogMessageKafkaProducer(KafkaProducerConfiguration kafkaConfig) {
        this.producer = new KafkaProducer<>(createProperties(kafkaConfig));
    }

    private Properties createProperties(KafkaProducerConfiguration kafkaConfig) {
        Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getKafkaBootstrapServers());
        properties.put(SCHEMA_REGISTRY_URL_CONFIG, kafkaConfig.getSchemaRegistryUrls());
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        return properties;
    }

    void produce(LogMessage logMessage) {
        producer.send(new ProducerRecord<>(TOPIC_LOGGING, UUID.randomUUID().toString(), logMessage));
    }

}
