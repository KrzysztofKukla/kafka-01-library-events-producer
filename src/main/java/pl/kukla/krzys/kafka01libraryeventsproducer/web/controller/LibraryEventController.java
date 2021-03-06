package pl.kukla.krzys.kafka01libraryeventsproducer.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.LibraryEvent;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.LibraryEventType;
import pl.kukla.krzys.kafka01libraryeventsproducer.producer.LibraryEventProducerService;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;

/**
 * @author Krzysztof Kukla
 */
@RestController
@RequestMapping(LibraryEventController.V1_LIBRARY_EVENT_URL)
@RequiredArgsConstructor
@Slf4j
public class LibraryEventController {

    static final String V1_LIBRARY_EVENT_URL = "/v1/libraryevent";

    private final LibraryEventProducerService libraryEventProducerService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException,
        ExecutionException, InterruptedException {

        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        log.info("before sendLibraryEvent");
        //asynchronous call
        libraryEventProducerService.sendLibraryEvent(libraryEvent);

        //synchronous call
//        SendResult<Long, String> sendResult = libraryEventProducerService.sendLibraryEventSynchronous(libraryEvent);
//        log.info("SendResult: {}", sendResult.toString());

        log.info("after sendLibraryEvent");

        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{topic}")
    public LibraryEvent sendLibraryEventToTopic(@RequestBody @Valid LibraryEvent libraryEvent, @PathVariable String topic) throws JsonProcessingException {
        log.info("Sending libraryEvent to {} topic", topic);
        libraryEventProducerService.sendLibraryEventWithTopic(libraryEvent, topic);

        return libraryEvent;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{topic}/{id}")
    public ResponseEntity<?> updateLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent,
                                                @PathVariable String topic,
                                                @PathVariable Long id) throws JsonProcessingException {
        if (libraryEvent.getId() == null) {
            log.warn("Cannot update Library event without libraryId");
            return ResponseEntity.badRequest().body("Wrong libraryId");
        }
        log.info("Updating libraryEvent for id={}", libraryEvent.getId());

        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        libraryEventProducerService.sendLibraryEventWithTopic(libraryEvent, topic);
        return ResponseEntity.noContent().build();
    }

}
