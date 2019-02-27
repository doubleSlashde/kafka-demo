package de.doubleslash.demo.kafka.producer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import de.doubleslash.demo.kafka.producer.config.KafkaProducerConfiguration;

@SpringBootApplication
public class KafkaProducerDemoApp {

	private static final String LOG_MSG_GENERATOR_BEAN_NAME = "generator";

	@Bean
	LogMessageKafkaProducer logMessageKafkaProducer(KafkaProducerConfiguration kafkaConfig) {
		return new LogMessageKafkaProducer(kafkaConfig);
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
