package searchengine.services.parsing;

import lombok.Data;

import java.util.concurrent.atomic.AtomicBoolean;
@Data
public class StopParsing {
    private static AtomicBoolean stop;
    private static AtomicBoolean stopped;

}
