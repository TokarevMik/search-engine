package searchengine.dto.statistics;

import searchengine.model.Page;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SetOfPage {

    private Set<Page> records;
    public SetOfPage() {
        newRecords();
    }

    public void addPage(Page page) {
        records.add(page);
    }

    public Set<Page> getRecords() {
        return records;
    }

    public int getCount() {
        return records.size();
    }

    public void newRecords() {
        records = ConcurrentHashMap.newKeySet();
    }
}
