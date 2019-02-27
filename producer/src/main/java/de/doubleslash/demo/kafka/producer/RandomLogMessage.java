package de.doubleslash.demo.kafka.producer;

import static de.doubleslash.demo.kafka.avro.Level.DEBUG;
import static de.doubleslash.demo.kafka.avro.Level.ERROR;
import static de.doubleslash.demo.kafka.avro.Level.INFO;
import static de.doubleslash.demo.kafka.avro.Level.WARN;

import java.util.Random;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

import de.doubleslash.demo.kafka.avro.Level;
import de.doubleslash.demo.kafka.avro.LogMessage;

/**
 * Class for generating RANDOM log messages.
 */
final class RandomLogMessage {

    private RandomLogMessage() {
        // do not instantiate; just use static methods.
    }

    private static final Lorem LOREM = LoremIpsum.getInstance();

    private static final Random RANDOM = new Random(System.currentTimeMillis());


    /**
     * Creates and returns a RANDOM log message.
     * @return a RANDOM log message.
     */
    static LogMessage next() {
        LogMessage logMessage = new LogMessage();
        logMessage.setSystemId("kafka-demo");
        logMessage.setLogLevel(getLogLevel());
        logMessage.setMessage(createMessage());
        logMessage.setMessage(logMessage.getLogLevel() + " - " + logMessage.getMessage());

        return logMessage;
    }

    private static Level getLogLevel() {
        int i = RANDOM.nextInt(100) + 1;
        if (i < 5) {
            return ERROR;
        } else if (i < 10) {
            return WARN;
        } else if (i < 80) {
            return INFO;
        } else if (i < 95) {
            return WARN;
        }
        return DEBUG;
    }

    private static String createMessage() {
        String msg = LOREM.getWords(7, 15);
        msg = capitalizeFirstLetter(msg) + ".";

        return msg;
    }

    private static String capitalizeFirstLetter(String msg) {
        return msg.substring(0, 1).toUpperCase() + msg.substring(1);
    }

}
