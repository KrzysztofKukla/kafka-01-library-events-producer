package pl.kukla.krzys.kafka01libraryeventsproducer.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.Book;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.LibraryEvent;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.LibraryEventType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Krzysztof Kukla
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-event"}, partitions = 3)
public class LibraryEventControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ObjectMapper objectMapper;

    private Consumer<Long, String> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> configs = getKafkaProps();
        consumer = new DefaultKafkaConsumerFactory<>(configs, new LongDeserializer(), new StringDeserializer()).createConsumer();
        //in this case we have only one 'library-event' topic
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        //very good practice
        consumer.close();
        ;
    }

    @Test
    void postLibraryEventWithTopicTest() throws Exception {
        String libraryEventsTopic = "library-event";
        //given
        HttpHeaders headers = createHeaders();
        LibraryEvent libraryEvent = createLibraryEvent(createBook());
        HttpEntity<LibraryEvent> libraryEventRequest = new HttpEntity<>(libraryEvent, headers);

        //when
        Map<String, Object> variables = new HashMap<>();
        variables.put("topic", libraryEventsTopic);
        ResponseEntity<LibraryEvent> responseEntity =
            testRestTemplate.postForEntity(LibraryEventController.V1_LIBRARY_EVENT_URL + "/{topic}", libraryEventRequest, LibraryEvent.class,
                variables);

        //then
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        ConsumerRecord<Long, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, libraryEventsTopic);
        String message = consumerRecord.value();
        LibraryEvent libraryEventFromJson = objectMapper.readValue(message, LibraryEvent.class);
        Assertions.assertEquals(libraryEvent, libraryEventFromJson);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

    private LibraryEvent createLibraryEvent(Book book) {
        return LibraryEvent.builder()
            .id(null)
            .libraryEventType(LibraryEventType.NEW)
            .book(book)
            .build();

    }

    private Book createBook() {
        return Book.builder()
            .id(1L)
            .author("test author")
            .name("test name book")
            .build();
    }

    private Map<String, Object> getKafkaProps() {
        return new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
    }

}
