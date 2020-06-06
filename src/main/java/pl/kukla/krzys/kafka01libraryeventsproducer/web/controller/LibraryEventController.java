package pl.kukla.krzys.kafka01libraryeventsproducer.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.LibraryEvent;

/**
 * @author Krzysztof Kukla
 */
@RestController
@RequestMapping(LibraryEventController.V1_LIBRARY_EVENT_URL)
public class LibraryEventController {

    static final String V1_LIBRARY_EVENT_URL = "/v1/libraryevent";

    @PostMapping
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody LibraryEvent libraryEvent) {

        //TODO invoke KafkaProducer

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

}
