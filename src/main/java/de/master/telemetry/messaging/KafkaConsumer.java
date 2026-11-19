
package de.master.telemetry.messaging;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import de.master.telemetry.telemetry.TelemetryService;
import de.master.telemetry.telemetry.dto.TelemetryMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final TelemetryService telemetryService;

    public KafkaConsumer(
            ObjectMapper objectMapper,
            TelemetryService telemetryService
    ) {
        this.objectMapper = objectMapper;
        this.telemetryService = telemetryService;
    }

    @KafkaListener(
            topics = "vehicle-telemetry",
            groupId = "telemetry-backend"
    )
    public void consume(String message) {
        try {
            TelemetryMessage telemetryMessage = objectMapper.readValue(message, TelemetryMessage.class);

            telemetryService.saveTelemetry(telemetryMessage);

        } catch (JacksonException e) {
            throw new IllegalArgumentException(
                    "Could not deserialize telemetry message", e
            );
        }
    }
}
