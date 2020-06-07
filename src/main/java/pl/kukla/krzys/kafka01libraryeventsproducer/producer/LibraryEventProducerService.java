package pl.kukla.krzys.kafka01libraryeventsproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.LibraryEvent;

/**
 * @author Krzysztof Kukla
 */
public interface LibraryEventProducerService {

    void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException;

}
