package searchengine.dto.statistics;

import searchengine.model.Page;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ParsedPages {
    public static Set<String> parsed;
    public  static  void setInit() {
        parsed = new CopyOnWriteArraySet<>();
    }
    public  static  boolean contain(String url) {
        return parsed.contains(url);
    }
    public static void addPage(String url) {
        parsed.add(url);
    }
    public  static  Set<String> get(){
        return parsed;
    }
}
