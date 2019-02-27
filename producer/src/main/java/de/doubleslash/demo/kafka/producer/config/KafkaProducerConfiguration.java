package de.doubleslash.demo.kafka.producer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration for the Kafka Producer.
 */
@Component
public class KafkaProducerConfiguration {

    private String kafkaBootstrapServers;
    private String schemaRegistryUrls;

    /**
     * Getter for <code>kafkaBootstrapServers</code>.
     *
     * @return Returns <code>kafkaBootstrapServers</code>
     */
    public String getKafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    /**
     * Setter for <code>kafkaBootstrapServers</code>.
     *
     * @param kafkaBootstrapServers New value used for <code>kafkaBootstrapServers</code>
     */
    @Value("${kafka.bootstrap.servers}")
    public void setKafkaBootstrapServers(String kafkaBootstrapServers) {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
    }

    /**
     * Getter for <code>schemaRegistryUrls</code>.
     *
     * @return Returns <code>schemaRegistryUrls</code>
     */
    public String getSchemaRegistryUrls() {
        return schemaRegistryUrls;
    }

    /**
     * Setter for <code>schemaRegistryUrls</code>.
     *
     * @param schemaRegistryUrls New value used for <code>schemaRegistryUrls</code>
     */
    @Value("${kafka.schema.registry.urls}")
    public void setSchemaRegistryUrls(String schemaRegistryUrls) {
        this.schemaRegistryUrls = schemaRegistryUrls;
    }

}
