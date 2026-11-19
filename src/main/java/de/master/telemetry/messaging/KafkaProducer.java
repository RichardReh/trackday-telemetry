
package de.master.telemetry.messaging;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import de.master.telemetry.telemetry.dto.TelemetryMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private static final String TOPIC = "vehicle-telemetry";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendTelemetry(TelemetryMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(TOPIC, message.vehicleId().toString(), json);
        } catch (JacksonException e) {
            throw new IllegalArgumentException(
                    "Could not serialize telemetry message", e
            );
        }
    }
}
