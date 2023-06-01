package searchengine.dto.statistics;

import java.util.concurrent.atomic.AtomicBoolean;

public class AnchorStop {
    private static AtomicBoolean stop;
    private static AtomicBoolean stopped;

    public static boolean getStop() {
        return stop.get();
    }

    public static boolean getStopped() {
        return stopped.get();
    }

    public static void stop() {
        stop.set(true);
    }

    public static void stopped() {
        stopped.set(true);
    }

    public static void implNewStop() {
        stop = new AtomicBoolean(false);
        stopped = new AtomicBoolean(false);
    }
}
