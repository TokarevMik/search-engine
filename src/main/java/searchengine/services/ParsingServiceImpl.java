package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.model.Status;
import searchengine.parsing.NewThreadParser;
import searchengine.parsing.Node;
import searchengine.repositoryes.PageRepo;
import searchengine.repositoryes.SiteRepo;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ParsingServiceImpl implements ParsingService {
    @Autowired
    SiteRepo siteRepo;
    @Autowired
    PageRepo pageRepo;
    private final SitesList sites;

    @Override
    public void startParsing() {
        for (Site site : sites.getSites()) {
            site.setStatus_time(new Date());
            site.setStatus(Status.INDEXING);
            siteRepo.save(site);
            new NewThreadParser(new Node(site.getUrl(),site.getUrl(),site,pageRepo,siteRepo),pageRepo).run();
        }

    }
}
