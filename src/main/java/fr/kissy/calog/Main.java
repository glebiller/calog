package fr.kissy.calog;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.PathConverter;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    @Parameter(names = "--file", description = "File path to watch.",
            converter = PathConverter.class)
    private Path file = Path.of("/tmp/access.log");

    @Parameter(names = "--warn-threshold", description = "Minimum average number of requests per" +
            " seconds in 2 minutes to generate an alert.")
    private Integer warnThreshold = 10;

    @Parameter(names = "--help", help = true)
    private boolean help = false;

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    public static void main(String[] argv) {
        Main main = new Main();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(main)
                .build();

        jCommander.setProgramName("calog");
        jCommander.parse(argv);
        if (main.help) {
            jCommander.usage();
            return;
        }

        main.run();
    }

    private void run() {
        if (!Files.exists(file)) {
            LOGGER.error("File {} does not exists, please specify a valid file path to monitor", file.toAbsolutePath());
            return;
        }

        StatisticsRecorder recorder = new StatisticsRecorder(warnThreshold);
        TailerListener listener = new W3cAccessLogListener(recorder);
        Tailer tailer = new Tailer(file.toFile(), listener, 50);

        executorService.execute(tailer);
        executorService.scheduleAtFixedRate(recorder::monitorEvents,
                0, 1, TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(recorder::printSectionsStatistics,
                0, 10, TimeUnit.SECONDS);
    }
}
