package fr.kissy.calog;

import org.apache.commons.io.input.TailerListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class W3cAccessLogListener extends TailerListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(W3cAccessLogListener.class);
    private static final Pattern LOG_PATTERN = Pattern.compile("\\S+ - \\S+ \\[([^\\]]+)\\] \"\\S+ (\\S+) HTTP/1\\.0\" \\d+ \\d+");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
    private final StatisticsRecorder recorder;

    W3cAccessLogListener(StatisticsRecorder recorder) {
        this.recorder = recorder;
    }

    public void handle(String line) {
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (matcher.matches()) {
            try {
                Date eventDate = DATE_FORMAT.parse(matcher.group(1));
                recorder.record(eventDate, matcher.group(2));
            } catch (ParseException e) {
                LOGGER.warn("Impossible to parse log date \"{}\"", matcher.group(1), e);
            }
        } else {
            LOGGER.warn("Incompatible log format found \"{}\"", line);
        }
    }
}