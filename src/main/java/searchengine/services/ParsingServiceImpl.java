package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.statistics.IndexingStatus;
import searchengine.model.Site;
import searchengine.config.SitesList;
import searchengine.model.Status;
import searchengine.services.parsing.NewThreadParser;
import searchengine.services.parsing.Node;
import searchengine.repositoryes.PageRepo;
import searchengine.repositoryes.SiteRepo;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ParsingServiceImpl implements ParsingService {
    @Autowired
    SiteRepo siteRepo;
//    IndexingStatus status;
    @Autowired
    PageRepo pageRepo;
    private final SitesList sites;

    @Override
    public void startParsing() {
        siteRepo.deleteAll();
        pageRepo.deleteAll();
        for (Site site : sites.getSites()) {
            site.setStatus_time(new Date());
            site.setStatus(Status.INDEXING);
            siteRepo.save(site);
            new NewThreadParser(new Node(site.getUrl(),site.getUrl(),site,pageRepo,siteRepo),pageRepo).run();
            site.setStatus_time(new Date());
            site.setStatus(Status.INDEXED);
            siteRepo.changeStatus(site.getId(),site.getStatus(),site.getStatus_time());
        }

    }
}
