package searchengine.services.parsing;

import lombok.Data;

import java.util.concurrent.atomic.AtomicBoolean;
@Data
public class StopParsing {
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
