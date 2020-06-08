package pl.kukla.krzys.kafka01libraryeventsproducer.web.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.Book;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.LibraryEvent;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.LibraryEventType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Krzysztof Kukla
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LibraryEventControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

//    @Autowired
//    private LibraryEventProducerService libraryEventProducerService;
//    @Autowired
//    private MockMvc mockMvc;

    @Test
    void postLibraryEventWithTopicTest() throws Exception {
        //given
        HttpHeaders headers = createHeaders();
        LibraryEvent libraryEvent = createLibraryEvent(createBook());
        HttpEntity<LibraryEvent> libraryEventRequest = new HttpEntity<>(libraryEvent, headers);

        //when
        Map<String, Object> variables = new HashMap<>();
        variables.put("topic", "library-events");
        ResponseEntity<LibraryEvent> responseEntity =
            testRestTemplate.postForEntity(LibraryEventController.V1_LIBRARY_EVENT_URL + "/{topic}", libraryEventRequest, LibraryEvent.class,
                variables);

        //then
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

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

}
