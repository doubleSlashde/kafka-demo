package de.doubleslash.demo.kafka.streams.table;

import static io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static java.util.Collections.singletonMap;
import static org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import de.doubleslash.demo.kafka.avro.LogMessage;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;

/**
 * Kafka Streams demo showing a {@link KTable} that counts the number of log messages for each log level.
 * <p>
 * This class utilizes the kafka integration for spring (start and stop/close of
 * {@link org.apache.kafka.streams.KafkaStreams} transparently managed by the Spring Boot Lifecycle).
 */
@EnableKafkaStreams
@SpringBootApplication
public class KafkaStreamsTableDemoApp {

    @Value("${kafka.bootstrap.servers}")
    private String kafkaBootstrapServers;

    @Value("${kafka.schema.registry.urls}")
    private String schemaRegistryUrls;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration streamsConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(APPLICATION_ID_CONFIG, "kafka-streams-table-demo");
        config.put(BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GenericAvroSerde.class.getName());
        config.put(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrls);
        return new KafkaStreamsConfiguration(config);
    }

    @Bean
    KStream kafkaStream(StreamsBuilder streamsBuilder, SpecificAvroSerde<LogMessage> logMessageSerde) {
        KTable<String, Long> countTable = streamsBuilder.stream("logging",
                Consumed.with(Serdes.String(), logMessageSerde))
                .groupBy( (key, value) -> value.getLogLevel().toString() )
                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"));

        KStream<String, Long> countStream = countTable.toStream();
        countStream.to("logging-count", Produced.with(Serdes.String(), Serdes.Long()));

        return countStream;
    }

    @Bean
    @SuppressWarnings("unchecked")
    SpecificAvroSerde<LogMessage> logMessageSerde() {
        Map<String, String> serdeConfig =
                singletonMap(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrls);
        final SpecificAvroSerde logMessageSerde = new SpecificAvroSerde<>();
        logMessageSerde.configure(serdeConfig, false);
        return logMessageSerde;
    }

    public static void main(String[] args) {
        SpringApplication.run(KafkaStreamsTableDemoApp.class, args);
    }

}
