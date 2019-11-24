package fr.kissy.calog;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsRecorder_Should {

    @Test
    void not_reach_threshold_without_enough_logs() {
        StatisticsRecorder recorder = new StatisticsRecorder(4);

        recorder.record(datePastNSeconds(30), "/api");
        recorder.record(datePastNSeconds(60), "/api");
        recorder.record(datePastNSeconds(120), "/api");
        recorder.record(datePastNSeconds(150), "/api");

        recorder.monitorEvents();

        assertFalse(recorder.isThresholdReached());
    }

    @Test
    void not_reach_threshold_with_old_logs() {
        StatisticsRecorder recorder = new StatisticsRecorder(4);

        recorder.record(datePastNSeconds(130), "/api");
        recorder.record(datePastNSeconds(140), "/api");
        recorder.record(datePastNSeconds(150), "/api");
        recorder.record(datePastNSeconds(160), "/api");

        recorder.monitorEvents();

        assertFalse(recorder.isThresholdReached());
    }

    @Test
    void reach_threshold_with_exactly_4_events() {
        StatisticsRecorder recorder = new StatisticsRecorder(4);

        recorder.record(datePastNSeconds(30), "/api");
        recorder.record(datePastNSeconds(60), "/api");
        recorder.record(datePastNSeconds(90), "/api");
        recorder.record(datePastNSeconds(110), "/api");

        recorder.monitorEvents();

        assertTrue(recorder.isThresholdReached());
    }

    @Test
    void reach_threshold_with_more_than_4_events() {
        StatisticsRecorder recorder = new StatisticsRecorder(4);

        recorder.record(datePastNSeconds(10), "/api");
        recorder.record(datePastNSeconds(30), "/api");
        recorder.record(datePastNSeconds(60), "/api");
        recorder.record(datePastNSeconds(90), "/api");
        recorder.record(datePastNSeconds(110), "/api");

        recorder.monitorEvents();

        assertTrue(recorder.isThresholdReached());
    }

    private Date datePastNSeconds(int seconds) {
        return Date.from(LocalDateTime.now().minus(seconds, ChronoUnit.SECONDS)
                .atZone(ZoneId.systemDefault()).toInstant());
    }

}