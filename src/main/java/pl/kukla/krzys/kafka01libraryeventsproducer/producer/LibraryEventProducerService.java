package pl.kukla.krzys.kafka01libraryeventsproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.LibraryEvent;

import java.util.concurrent.ExecutionException;

/**
 * @author Krzysztof Kukla
 */
public interface LibraryEventProducerService {

    ListenableFuture<SendResult<Long, String>> sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException;

    //it allows asynchronous call
    ListenableFuture<SendResult<Long, String>> sendLibraryEventWithTopic(LibraryEvent libraryEvent, String topic) throws JsonProcessingException;

    //synchronous call
    SendResult<Long, String> sendLibraryEventSynchronous(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException;

}
