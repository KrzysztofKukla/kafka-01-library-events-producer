package pl.kukla.krzys.kafka01libraryeventsproducer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Krzysztof Kukla
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {

    private Long id;
    private String author;
    private String name;

}
