package fr.kissy.calog;

import com.beust.jcommander.internal.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

class StatisticsRecorder {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsRecorder.class);
    private final Map<String, List<Date>> sectionEvents = Maps.newHashMap();
    private final List<Date> events = new ArrayList<>();
    private final Integer warnThreshold;
    private boolean thresholdReached = false;

    StatisticsRecorder(Integer warnThreshold) {
        this.warnThreshold = warnThreshold;
    }

    void record(Date date, String url) {
        String section = url.split("/")[1];
        if (!sectionEvents.containsKey(section)) {
            sectionEvents.put(section, new ArrayList<>());
        }
        sectionEvents.get(section).add(date);
        events.add(date);
    }

    void monitorEvents() {
        removeOldEvents();

        int currentHitCount = events.size();
        if (thresholdReached && currentHitCount < warnThreshold) {
            thresholdReached = false;
            LOGGER.warn("Traffic is back at standard level - hits = {}, resolved at {}", currentHitCount, new Date());
        } else if (!thresholdReached && currentHitCount >= warnThreshold) {
            thresholdReached = true;
            LOGGER.warn("High traffic generated an alert - hits = {}, triggered at {}", currentHitCount, new Date());
        }
    }

    private void removeOldEvents() {
        if (events.isEmpty()) {
            return;
        }

        Instant limit = LocalDateTime.now().minus(2, ChronoUnit.MINUTES)
                .atZone(ZoneId.systemDefault()).toInstant();
        events.removeIf(date -> date.toInstant().isBefore(limit));
    }

    void printSectionsStatistics() {
        String statistics = sectionEvents.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<String, List<Date>> entry) -> entry.getValue().size()).reversed())
                .limit(3)
                .map(entry -> String.format("  %s: %d hits", entry.getKey(), entry.getValue().size()))
                .collect(Collectors.joining("\n"));
        if (statistics.isEmpty()) {
            statistics = "  No log received during that period";
        }
        LOGGER.info("Last top 3 sections at {} \n{}", new Date(), statistics);
        sectionEvents.clear();
    }

    boolean isThresholdReached() {
        return thresholdReached;
    }
}
