package de.doubleslash.demo.kafka.streams.table;

import static de.doubleslash.demo.kafka.streams.table.KafkaStreamsTableDemoApp.STORE_NAME;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST service which queries the store holding number of log messages per log level, and returns it
 * as JSON.
 */
@RestController()
public class LogMessageCounterRestController {

    StreamsBuilderFactoryBean factoryBean;

    public LogMessageCounterRestController(StreamsBuilderFactoryBean factoryBean) {
        this.factoryBean = factoryBean;
    }

    @GetMapping(path = "/logging/counts", produces = APPLICATION_JSON_VALUE)
    Map getLogCounts() {
        KafkaStreams streams = factoryBean.getKafkaStreams();
        ReadOnlyKeyValueStore<String, Long> store = streams.store(STORE_NAME, QueryableStoreTypes.keyValueStore());

        KeyValueIterator<String, Long> countIterator = store.all();

        Map<String, Long> result = new TreeMap<>();

        while(countIterator.hasNext()) {
            KeyValue<String, Long> kv = countIterator.next();
            result.put(kv.key, kv.value);
        }

        return result;
    }

}
