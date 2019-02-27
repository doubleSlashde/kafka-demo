package de.doubleslash.demo.kafka.streams;

import static de.doubleslash.demo.kafka.avro.Level.ERROR;
import static io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static java.util.Collections.singletonMap;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.doubleslash.demo.kafka.avro.LogMessage;
import de.doubleslash.demo.kafka.streams.config.KafkaStreamsConfiguration;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;

/**
 * Kafka Streams based class that reads log messages from the logging topic. Adds a logMessageId to each message.
 * Log messages with level ERROR are routed to the Kafka topic "logging-alerts", all other messages are routed
 */
public class LogMessageProcessor implements AutoCloseable {

    private Logger log = LoggerFactory.getLogger(LogMessageProcessor.class);

    private final KafkaStreamsConfiguration kafkaStreamsConfig;

    private CountDownLatch latch;

    private KafkaStreams streams;

    public LogMessageProcessor(KafkaStreamsConfiguration kafkaStreamsConfig) {
        this.kafkaStreamsConfig = kafkaStreamsConfig;
    }

    void start() {
        log.info("Starting LogMessageProcessor.");

        initStreams();
        initLatch();

        streams.start();
    }

    private void initStreams() {
        final StreamsBuilder builder = new StreamsBuilder();

        final SpecificAvroSerde<LogMessage> logMessageSerde = logMessageSerde();
        KStream<String, LogMessage> loggingStream = builder.stream(
                "logging",
                Consumed.with(Serdes.String(), logMessageSerde)
        );

        // suppress warning that is induced by Kafka Streams API,
        // which we cannot control
        @SuppressWarnings("unchecked")
        KStream<String, LogMessage>[] loggingStreams = loggingStream
                .mapValues(this::addLogMessageId)
                .branch( (key, logMsg) -> logMsg.getLogLevel() == ERROR,
                         (key, logMsg) -> true); // includes only messages with level != ERROR (

        loggingStreams[1].to("logging-processed", Produced.with(Serdes.String(), logMessageSerde));
        loggingStreams[0].to("logging-alerts", Produced.with(Serdes.String(), logMessageSerde));

        final Topology topology = builder.build();

        streams = new KafkaStreams(topology, streamsProperties());
    }

    private LogMessage addLogMessageId(LogMessage logMsg) {
        logMsg.setMessageId(UUID.randomUUID().toString());
        return logMsg;
    }

    private Properties streamsProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-demo-streams");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaStreamsConfig.getKafkaBootstrapServers());
        props.put(SCHEMA_REGISTRY_URL_CONFIG, kafkaStreamsConfig.getSchemaRegistryUrls());

        return props;
    }

    @SuppressWarnings("unchecked")
    private SpecificAvroSerde<LogMessage> logMessageSerde() {
        final SpecificAvroSerde logMessageSerde = new SpecificAvroSerde<>();
        logMessageSerde.configure(serdeConfig(), false);
        return logMessageSerde;
    }

    private Map<String, String> serdeConfig() {
        return singletonMap(SCHEMA_REGISTRY_URL_CONFIG, kafkaStreamsConfig.getSchemaRegistryUrls());
    }

    private void initLatch() {
        latch = new CountDownLatch(1);
    }

    @Override
    public void close() {
        log.info("Shutting down LogMessageProcessor.");
        streams.close();
        latch.countDown();
    }

}

