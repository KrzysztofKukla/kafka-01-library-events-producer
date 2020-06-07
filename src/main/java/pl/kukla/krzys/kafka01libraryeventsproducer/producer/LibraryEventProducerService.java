package pl.kukla.krzys.kafka01libraryeventsproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.kafka.support.SendResult;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.LibraryEvent;

import java.util.concurrent.ExecutionException;

/**
 * @author Krzysztof Kukla
 */
public interface LibraryEventProducerService {

    void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException;

    //synchronous call
    SendResult<Long, String> sendLibraryEventSynchronous(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException;

}
