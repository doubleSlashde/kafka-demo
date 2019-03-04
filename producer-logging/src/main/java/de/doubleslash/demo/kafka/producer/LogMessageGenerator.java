package de.doubleslash.demo.kafka.producer;

import java.io.Closeable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.doubleslash.demo.kafka.avro.LogMessage;

public class LogMessageGenerator implements Closeable {

    private Logger log = LoggerFactory.getLogger(LogMessageGenerator.class);

    private LogMessageKafkaProducer logMessageKafkaProducer;

    private boolean running;

    public LogMessageGenerator(LogMessageKafkaProducer logMessageKafkaProducer) {
        this.logMessageKafkaProducer = logMessageKafkaProducer;
    }

    void start() {
        running = true;
        int count = 0;
        while (running) {
            final LogMessage logMessage = RandomLogMessage.next();

            logMessageKafkaProducer.produce(logMessage);

            log.info("Log message nr. {} was written to Kafka:\n{}", ++count, logMessage);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void close() {
        running = false;
        logMessageKafkaProducer.shutdown();
    }
}
