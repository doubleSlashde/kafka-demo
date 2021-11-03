
package de.doubleslash.demo.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

@SpringBootApplication
public class KafkaTemplateProducerDemoApp {

    @Bean
    CommandLineRunner commandLineRunner(KafkaTemplate<String, String> kafkaTemplate) {
        return args -> new KafkaProducerDemo(kafkaTemplate).publishRandomMessages();
    }

    public static void main(String[] args) {
        SpringApplication.run(KafkaTemplateProducerDemoApp.class);
    }
}

class KafkaProducerDemo {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaProducerDemo.class);

    private static final Lorem LOREM = LoremIpsum.getInstance();

    private boolean running = false;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerDemo(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    void publishRandomMessages() {
        this.running = true;

        while(running) {
            String message = randomMessage();

            publish(message);

            LOG.info("Message '{}' was published to Kafka.", message);
            sleep();
        }
    }

    private String randomMessage() {
        String msg = LOREM.getWords(5, 10) + ".";
        msg = msg.substring(0, 1).toUpperCase() + msg.substring(1);
        return msg;
    }

    void publish(String message) {
        kafkaTemplate.send("kafka-demo", null, message);
    }

    private void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
