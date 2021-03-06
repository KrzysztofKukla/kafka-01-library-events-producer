package pl.kukla.krzys.kafka01libraryeventsproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.Book;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.LibraryEvent;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.LibraryEventType;

/**
 * @author Krzysztof Kukla
 */
@ExtendWith(MockitoExtension.class)
class LibraryEventProducerServiceImplTest {

    @Mock
    private KafkaTemplate<Long, String> kafkaTemplate;

    //allows to create instance of ObjectMapper
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private LibraryEventProducerServiceImpl libraryEventProducerService;

    @Test
    void sendLibraryEventWithTopicFailure() throws Exception {
        LibraryEvent libraryEvent = createLibraryEvent(createBook());
        String topic = "some_topic";

        //here we try to simulate Exception thrown - call onFailure method
        SettableListenableFuture<ListenableFuture> listenableFuture = new SettableListenableFuture<>();
        listenableFuture.setException(new RuntimeException("Exception calling Kafka"));
        BDDMockito.when(kafkaTemplate.send(ArgumentMatchers.any(ProducerRecord.class))).thenReturn(listenableFuture);

        Assertions.assertThrows(Exception.class, () -> libraryEventProducerService.sendLibraryEventWithTopic(libraryEvent, topic).get());
    }

    @Test
    void sendLibraryEventWithTopicSuccess() throws Exception {
        LibraryEvent libraryEvent = createLibraryEvent(createBook());
        String topic = "some_topic";
        SettableListenableFuture<SendResult<Long, String>> settableListenableFuture = createSettableListenableFuture(libraryEvent, topic);

        //here we try to simulate onSuccess method
        BDDMockito.when(kafkaTemplate.send(ArgumentMatchers.any(ProducerRecord.class))).thenReturn(settableListenableFuture);

        ListenableFuture<SendResult<Long, String>> listenableFuture = libraryEventProducerService.sendLibraryEventWithTopic(libraryEvent, topic);
        SendResult<Long, String> sendResult = listenableFuture.get();

        Assertions.assertEquals(1, sendResult.getRecordMetadata().partition());
    }

    private SettableListenableFuture<SendResult<Long, String>> createSettableListenableFuture(LibraryEvent libraryEvent, String topic) throws JsonProcessingException {
        String jsonMessage = objectMapper.writeValueAsString(libraryEvent);
        SettableListenableFuture<SendResult<Long, String>> listenableFuture = new SettableListenableFuture<>();
        ProducerRecord<Long, String> producerRecord = new ProducerRecord<Long, String>(topic, libraryEvent.getId(), jsonMessage);
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition(topic, 1),
            1, 1, 342, System.currentTimeMillis(), 1, 2);
        SendResult<Long, String> sendResult = new SendResult<>(producerRecord, recordMetadata);

        listenableFuture.set(sendResult);

        return listenableFuture;
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

}