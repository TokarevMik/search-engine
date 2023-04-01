package searchengine.services;

import java.io.IOException;
import java.util.Map;

public interface LemmaService {
    public Map<String,Double> LemmaMap(String s) throws IOException;
}
