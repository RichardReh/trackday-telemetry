package de.master.telemetry.messaging;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class DeadLetterConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(DeadLetterConsumer.class);

    @KafkaListener(
            topics = "vehicle-telemetry.DLT",
            groupId = "telemetry-dlt-monitor"
    )
    public void consumeDeadLetter(ConsumerRecord<String, String> record) {

        String exceptionMessage =
                getHeader(record, "kafka_dlt-exception-message");

        String exceptionClass =
                getHeader(record, "kafka_dlt-exception-fqcn");

        String originalTopic =
                getHeader(record, "kafka_dlt-original-topic");

        log.error("""
                Dead Letter received!
                Original topic: {}
                DLT partition: {}
                DLT offset: {}
                Key: {}
                Exception: {}
                Exception message: {}
                Payload: {}
                """,
                originalTopic,
                record.partition(),
                record.offset(),
                record.key(),
                exceptionClass,
                exceptionMessage,
                record.value()
        );
    }

    private String getHeader(
            ConsumerRecord<String, String> record,
            String headerName) {

        Header header = record.headers().lastHeader(headerName);

        if (header == null) {
            return "Unknown";
        }

        return new String(
                header.value(),
                StandardCharsets.UTF_8
        );
    }
}
