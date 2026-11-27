package de.master.telemetry.telemetry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TelemetryGapScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(TelemetryGapScheduler.class);

    private final TelemetrySequenceGapRepository sequenceGapRepository;

    public TelemetryGapScheduler(
            TelemetrySequenceGapRepository sequenceGapRepository) {
        this.sequenceGapRepository = sequenceGapRepository;
    }

    @Scheduled(fixedDelay = 5000)
    public void expireOldGaps() {

        try {
            int expiredGaps = sequenceGapRepository.expireOldGaps();

            if (expiredGaps > 0) {
                log.info("Expired {} telemetry sequence gaps.", expiredGaps);
            }

        } catch (CannotGetJdbcConnectionException e) {
            log.warn(
                    "Cannot expire telemetry sequence gaps: database unavailable."
            );
        }
    }
}
