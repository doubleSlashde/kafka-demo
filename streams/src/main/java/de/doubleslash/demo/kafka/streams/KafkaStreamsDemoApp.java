package de.doubleslash.demo.kafka.streams;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import de.doubleslash.demo.kafka.streams.config.KafkaStreamsConfiguration;

@SpringBootApplication
public class KafkaStreamsDemoApp {

    private static final String ALERT_MANAGER_BEAN_NAME = "alertManager";

    @Bean(ALERT_MANAGER_BEAN_NAME)
    LogMessageProcessor alertManager(KafkaStreamsConfiguration kafkaStreamsConfig) {
        return new LogMessageProcessor(kafkaStreamsConfig);
    }

    @Bean
    CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            final LogMessageProcessor logMessageProcessor = ctx.getBean(ALERT_MANAGER_BEAN_NAME, LogMessageProcessor.class);
            logMessageProcessor.start();
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(KafkaStreamsDemoApp.class, args);
    }

}
