package de.doubleslash.demo.kafka.streams.table;

import static de.doubleslash.demo.kafka.streams.table.KafkaStreamsTableDemoApp.STORE_NAME;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.StreamsMetadata;
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

    @GetMapping(path="logging/store", produces = APPLICATION_JSON_VALUE)
    Collection<Map<String, String>> getStoreMetadata() {
        KafkaStreams streams = factoryBean.getKafkaStreams();

        Collection<StreamsMetadata> metadata = streams.allMetadataForStore(STORE_NAME);

        final List<Map<String, String>> result = metadata.stream()
                .map(this::toMap)
                .collect(toList());

        return result;
    }

    private Map<String, String> toMap(StreamsMetadata metaData) {
        Map<String, String> map = new TreeMap<>();
        map.put("host", metaData.host());
        map.put("port", Integer.toString(metaData.port()));
        map.put("stores", metaData.stateStoreNames().toString());
        return map;
    }

}
