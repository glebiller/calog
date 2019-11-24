package fr.kissy.calog;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class W3cAccessLogListener_Should {
    private StatisticsRecorder recorder = Mockito.mock(StatisticsRecorder.class);;

    @Test
    void correctly_parse_log_timestamp() {
        W3cAccessLogListener listener = new W3cAccessLogListener(recorder);
        listener.handle("127.0.0.1 - james [09/May/2018:16:00:39 +0000] \"GET /report HTTP/1.0\" 200 123");
        Mockito.verify(recorder).record(any(Date.class), eq("/report"));
    }
}