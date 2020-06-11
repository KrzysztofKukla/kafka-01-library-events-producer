package pl.kukla.krzys.kafka01libraryeventsproducer.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.Book;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.LibraryEvent;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.LibraryEventType;
import pl.kukla.krzys.kafka01libraryeventsproducer.producer.LibraryEventProducerService;

/**
 * @author Krzysztof Kukla
 */
@WebMvcTest(controllers = LibraryEventController.class)
@AutoConfigureMockMvc
class LibraryEventControllerTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LibraryEventProducerService libraryEventProducerService;

    @Test
    void postLibraryEvent() throws Exception {
        LibraryEvent libraryEvent = createLibraryEvent(createBook());
        BDDMockito.when(libraryEventProducerService.sendLibraryEvent(ArgumentMatchers.any(LibraryEvent.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post(LibraryEventController.V1_LIBRARY_EVENT_URL)
            .content(objectMapper.writeValueAsString(libraryEvent))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated());

    }

    @Test
    void postLibraryEventInvalidBook() throws Exception {
        LibraryEvent libraryEvent = createLibraryEvent(null);
        BDDMockito.when(libraryEventProducerService.sendLibraryEvent(ArgumentMatchers.any(LibraryEvent.class))).thenReturn(null);

        String expectedErrorMessage = "book - must not be null";
        mockMvc.perform(MockMvcRequestBuilders.post(LibraryEventController.V1_LIBRARY_EVENT_URL)
            .content(objectMapper.writeValueAsString(libraryEvent))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.content().string(expectedErrorMessage));

    }

    @Test
    void sendLibraryEventToTopic() {
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