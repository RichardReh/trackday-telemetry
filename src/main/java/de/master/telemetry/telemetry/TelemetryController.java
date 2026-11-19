package de.master.telemetry.telemetry;

import de.master.telemetry.telemetry.dto.TelemetryMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/telemetry")
public class TelemetryController {

    private final TelemetryService telemetryService;

    public TelemetryController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Telemetry submitTelemetry(@RequestBody TelemetryMessage message) {
        return telemetryService.saveTelemetry(message);
    }
}
