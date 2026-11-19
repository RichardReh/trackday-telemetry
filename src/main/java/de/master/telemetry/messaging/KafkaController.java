package de.master.telemetry.messaging;

import de.master.telemetry.telemetry.dto.TelemetryMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kafka")
public class KafkaController {

    private final KafkaProducer kafkaProducer;

    public KafkaController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/send-telemetry")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendTelemetry(@RequestBody TelemetryMessage message) {
        kafkaProducer.sendTelemetry(message);
    }
}
