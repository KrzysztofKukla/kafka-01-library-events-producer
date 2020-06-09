package pl.kukla.krzys.kafka01libraryeventsproducer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author Krzysztof Kukla
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LibraryEvent {
    private Long id;
    private LibraryEventType libraryEventType;
    @NotNull
    @Valid
    private Book book;

}
