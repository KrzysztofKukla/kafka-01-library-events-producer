package pl.kukla.krzys.kafka01libraryeventsproducer.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
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