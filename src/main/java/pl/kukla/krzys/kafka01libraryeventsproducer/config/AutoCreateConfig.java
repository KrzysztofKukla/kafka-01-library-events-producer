package pl.kukla.krzys.kafka01libraryeventsproducer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

/**
 * @author Krzysztof Kukla
 */
//NOT USE ON PRODUCTION
@Profile("local")
@Configuration
public class AutoCreateConfig {

    @Bean
    public NewTopic libraryEvents() {
        return TopicBuilder.name("library-events")
            .partitions(3)
            .replicas(3)
            .build();
    }

}
