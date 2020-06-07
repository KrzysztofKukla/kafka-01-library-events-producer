package pl.kukla.krzys.kafka01libraryeventsproducer.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.LibraryEvent;
import pl.kukla.krzys.kafka01libraryeventsproducer.producer.LibraryEventProducerService;

/**
 * @author Krzysztof Kukla
 */
@RestController
@RequestMapping(LibraryEventController.V1_LIBRARY_EVENT_URL)
@RequiredArgsConstructor
public class LibraryEventController {

    static final String V1_LIBRARY_EVENT_URL = "/v1/libraryevent";

    private final LibraryEventProducerService libraryEventProducerService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException {

        libraryEventProducerService.sendLibraryEvent(libraryEvent);

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

}
