package pl.kukla.krzys.kafka01libraryeventsproducer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Krzysztof Kukla
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {

    @NotNull
    private Long id;
    @NotBlank
    private String author;
    @NotBlank
    private String name;

}
