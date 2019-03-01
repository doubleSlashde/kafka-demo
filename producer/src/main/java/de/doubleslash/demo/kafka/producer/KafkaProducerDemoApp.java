package de.doubleslash.demo.kafka.producer;

import static io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

import java.util.Properties;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import io.confluent.kafka.serializers.KafkaAvroSerializer;

@SpringBootApplication
public class KafkaProducerDemoApp {

	private static final String LOG_MSG_GENERATOR_BEAN_NAME = "generator";

	@Value("${kafka.bootstrap.servers}")
	private String kafkaBootstrapServers;

	@Value("${kafka.schema.registry.urls}")
	private String schemaRegistryUrls;

	@Bean
	Properties producerProperties() {
		Properties properties = new Properties();
		properties.put(BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
		properties.put(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrls);
		properties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.put(VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
		return properties;
	}

	@Bean
	LogMessageKafkaProducer logMessageKafkaProducer(Properties producerProperties) {
		return new LogMessageKafkaProducer(producerProperties);
	}

	@Bean(LOG_MSG_GENERATOR_BEAN_NAME)
	LogMessageGenerator logMessageGenerator(LogMessageKafkaProducer producer) {
		return new LogMessageGenerator(producer);
	}

	@Bean
	CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			LogMessageGenerator generator = ctx.getBean(LOG_MSG_GENERATOR_BEAN_NAME, LogMessageGenerator.class);
			generator.start();
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(KafkaProducerDemoApp.class, args);
	}

}
