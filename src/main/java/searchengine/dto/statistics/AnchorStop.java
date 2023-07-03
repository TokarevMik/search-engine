package searchengine.dto.statistics;

import java.util.concurrent.atomic.AtomicBoolean;

public class AnchorStop {
    private static AtomicBoolean stop;
    public static boolean getStop() {
        return stop.get();
    }

    public static void stop() {
        stop.set(true);
    }

    public static void implNewStop() {
        stop = new AtomicBoolean(false);
    }
}
