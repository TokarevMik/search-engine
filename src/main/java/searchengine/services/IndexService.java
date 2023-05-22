package searchengine.services;
import searchengine.dto.statistics.PageIndResponse;

public interface IndexService {
    public PageIndResponse processIndex(String page);

}
