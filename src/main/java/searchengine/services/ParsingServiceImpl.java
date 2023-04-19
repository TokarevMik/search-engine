package searchengine.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import searchengine.model.Site;
import searchengine.config.SitesList;
import searchengine.model.Status;
import searchengine.services.parsing.NewThreadParser;
import searchengine.services.parsing.Node;
import searchengine.repositoryes.PageRepo;
import searchengine.repositoryes.SiteRepo;

import java.util.Date;
import java.util.LinkedList;

@Service
@Getter
@RequiredArgsConstructor
public class ParsingServiceImpl implements ParsingService {
    private boolean isStarted = false;
    @Autowired
    SiteRepo siteRepo;
    //    IndexingStatus status;
    @Autowired
    PageRepo pageRepo;
    private final SitesList sites;
    private LinkedList<NewThreadParser> parserList = new LinkedList<>();

    @Override
    @Async
    public void startParsing() {
        isStarted = true;
        siteRepo.deleteAll();
        pageRepo.deleteAll();
        for (Site site : sites.getSites()) {
            site.setStatus_time(new Date());
            site.setStatus(Status.INDEXING);
            siteRepo.save(site);
            parserList.add(new NewThreadParser(new Node(site.getUrl(), site.getUrl(), site, pageRepo, siteRepo), pageRepo));
            parserList.getLast().run();
            site.setStatus_time(new Date());
            site.setStatus(Status.INDEXED);
            siteRepo.changeStatus(site.getId(), site.getStatus(), site.getStatus_time()); //Parameter value [1] did not match expected type Parameter value [2] did not match expected type
        }
    }

    public void stopParsing() {
        for (NewThreadParser p : parserList) {
            p.shutdown();

        }
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isShutdown() {
        for (NewThreadParser p : parserList) {
            if (!p.isShutdown()) return true;
        }
        return false;
    }
}
