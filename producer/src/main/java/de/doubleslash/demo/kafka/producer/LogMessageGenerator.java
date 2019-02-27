package de.doubleslash.demo.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.doubleslash.demo.kafka.avro.LogMessage;

public class LogMessageGenerator {

    private Logger log = LoggerFactory.getLogger(LogMessageGenerator.class);

    private LogMessageKafkaProducer logMessageKafkaProducer;

    public LogMessageGenerator(LogMessageKafkaProducer logMessageKafkaProducer) {
        this.logMessageKafkaProducer = logMessageKafkaProducer;
    }

    void start() {
        int count = 0;
        while (true) {
            final LogMessage logMessage = RandomLogMessage.next();

            log.info("Writing log message nr. {} to Kafka:\n{}", ++count, logMessage);

            logMessageKafkaProducer.produce(logMessage);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
