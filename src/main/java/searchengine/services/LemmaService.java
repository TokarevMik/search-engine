package searchengine.services;

import java.io.IOException;
import java.util.Map;

public interface LemmaService {
    public Map<String,Integer> LemmaMap(String s) throws IOException;
}
