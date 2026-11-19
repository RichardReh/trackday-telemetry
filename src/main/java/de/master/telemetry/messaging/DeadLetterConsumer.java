
package de.master.telemetry.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DeadLetterConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(DeadLetterConsumer.class);

    @KafkaListener(
            topics = "vehicle-telemetry.DLT",
            groupId = "telemetry-dlt-monitor"
    )
    public void consumeDeadLetter(String message) {
        log.error("Dead Letter received: {}", message);
    }
}
