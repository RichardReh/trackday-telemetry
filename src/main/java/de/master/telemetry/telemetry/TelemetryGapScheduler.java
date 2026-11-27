package de.master.telemetry.telemetry;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TelemetryGapScheduler {

    private final TelemetrySequenceGapRepository sequenceGapRepository;

    public TelemetryGapScheduler(
            TelemetrySequenceGapRepository sequenceGapRepository) {
        this.sequenceGapRepository = sequenceGapRepository;
    }

    @Scheduled(fixedRate = 1000)
    public void expireOldGaps() {
        int expiredGaps = sequenceGapRepository.expireOldGaps();

        if (expiredGaps > 0) {
            System.out.println(
                    "Expired " + expiredGaps + " telemetry sequence gaps."
            );
        }
    }
}
