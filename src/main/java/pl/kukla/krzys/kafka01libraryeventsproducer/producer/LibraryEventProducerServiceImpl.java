package pl.kukla.krzys.kafka01libraryeventsproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import pl.kukla.krzys.kafka01libraryeventsproducer.domain.LibraryEvent;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Krzysztof Kukla
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LibraryEventProducerServiceImpl implements LibraryEventProducerService {

    //key is Long from key-serializer: org.apache.kafka.common.serialization.LongSerializer
    //value is String as message from value-serializer: org.apache.kafka.common.serialization.StringSerializer
    private final KafkaTemplate<Long, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    //it allows asynchronous call
    @Override
    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        log.info("Sending message to Kafka");
        Long key = libraryEvent.getId();

        String message = objectMapper.writeValueAsString(libraryEvent);

        //sendDefault allows to automatically reads topic defined in application.yml
        //ListenableFuture allows to send message to Kafka when batch will be full ( it will happen in future )
        ListenableFuture<SendResult<Long, String>> listenableFuture = kafkaTemplate.sendDefault(key, message);

        //callback added
        listenableFuture.addCallback(listenableFutureCallback(key, message));
    }

    //it allows asynchronous call
    @Override
    public void sendLibraryEventWithTopic(LibraryEvent libraryEvent, String topic) throws JsonProcessingException {
        log.info("Sending message to Kafka");
        Long key = libraryEvent.getId();

        String message = objectMapper.writeValueAsString(libraryEvent);

        ProducerRecord<Long, String> producerRecord = buildProducerRecord(topic, key, message);
        ListenableFuture<SendResult<Long, String>> listenableFuture = kafkaTemplate.send(producerRecord);

        //callback added
        listenableFuture.addCallback(listenableFutureCallback(key, message));
    }

    private ProducerRecord<Long, String> buildProducerRecord(String topic, Long key, String message) {

        List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));

        return new ProducerRecord<Long, String>(topic, null, key, message, recordHeaders);
    }

    //synchronous call
    @Override
    public SendResult<Long, String> sendLibraryEventSynchronous(LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException,
        InterruptedException {
        log.info("Sending message to Kafka");
        Long key = libraryEvent.getId();

        String message = objectMapper.writeValueAsString(libraryEvent);

        //get() method wait until the message is sent successfully or onFailure
        SendResult<Long, String> sendResult = null;
        try {
            sendResult = kafkaTemplate.sendDefault(key, message).get(1, TimeUnit.SECONDS); //wait max 1 sec then throw TimeoutException
        } catch (InterruptedException | ExecutionException ex) {
            log.error("Error ExecutionException during sending message: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error ExecutionException during sending message: {}", ex.getMessage());
        }
        return sendResult;
    }

    //this is called asynchronous ( in other Thread ) that means can be finished after than last code line
    private ListenableFutureCallback<SendResult<Long, String>> listenableFutureCallback(Long key, String message) {
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
        log.error("Error exception message: {}", ex.getMessage());
        try {
            throw ex;
        } catch (Throwable e) {
            log.error("Error in onFailure: {}", ex.getMessage());
        }
    }

}
