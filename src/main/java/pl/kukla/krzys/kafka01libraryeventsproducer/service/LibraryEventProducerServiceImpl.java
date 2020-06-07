package pl.kukla.krzys.kafka01libraryeventsproducer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.LibraryEvent;

/**
 * @author Krzysztof Kukla
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LibraryEventProducerServiceImpl {

    private final KafkaTemplate<Long, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        Long key = libraryEvent.getId();

        String message = objectMapper.writeValueAsString(libraryEvent);

        //sendDefault allows to automatically reads topic defined in application.yml
        //ListenableFuture ( it will happen in future ) allows to send message to Kafka when batch will be full
        ListenableFuture<SendResult<Long, String>> listenableFuture = kafkaTemplate.sendDefault(key, message);
    }

    private ListenableFutureCallback<SendResult<Long, String>> ListenableFutureCallback(Long key, String message,) {
        return new ListenableFutureCallback<SendResult<Long, String>>() {

            //called if publish message is failed
            @Override
            public void onFailure(Throwable throwable) {
                handleFailure(key, message, throwable);
            }

            //called if publish message is successful
            @Override
            public void onSuccess(SendResult<Long, String> result) {
                handleSuccess(key, message, result);
            }
        };
    }

    //result gives us bunch of information like partition, offsets etc.
    private void handleSuccess(Long key, String message, SendResult<Long, String> result) {

        log.info("Message sent successfully for key: {}, value is: {}, partition is: {}", key, message, result.getRecordMetadata().partition());
    }

    private void handleFailure(Long key, String message, Throwable ex) {
        log.error("Error exception message: {}",ex.getMessage());
        try {
            throw ex;
        } catch (Throwable e) {
            log.error("Error in onFailure: {}",ex.getMessage());
        }
    }

}
