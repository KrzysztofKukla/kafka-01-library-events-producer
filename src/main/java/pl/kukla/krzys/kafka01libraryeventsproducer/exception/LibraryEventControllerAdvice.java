package pl.kukla.krzys.kafka01libraryeventsproducer.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Krzysztof Kukla
 */
//it captures any errors thrown in Controller
@ControllerAdvice
@Slf4j
public class LibraryEventControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> bookValidation(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
            .map(fieldError -> fieldError.getField() + " - " + fieldError.getDefaultMessage())
            .sorted()
            .collect(Collectors.joining(","));
        log.warn("error message: {}", errorMessage);
        return ResponseEntity.badRequest().body(errorMessage);
    }

}
