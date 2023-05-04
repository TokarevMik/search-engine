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
import searchengine.services.parsing.StopParsing;

import java.util.Date;
import java.util.LinkedList;

@Service
@Getter
@RequiredArgsConstructor
public class ParsingServiceImpl implements ParsingService {
    private static boolean isStarted = false;
    @Autowired
    SiteRepo siteRepo;
    //    IndexingStatus status;
    @Autowired
    PageRepo pageRepo;
    private final SitesList sites;
    private LinkedList<NewThreadParser> parserList = new LinkedList<>();
    private LinkedList<Thread> threadsList = new LinkedList<>();

    @Override
    @Async
    public void startParsing() {
        isStarted = true;
        siteRepo.deleteAll();
        pageRepo.deleteAll();
        for (Site site : sites.getSites()) {
            StopParsing.implNewStop();
            site.setStatus_time(new Date());
            site.setStatus(Status.INDEXING);
            siteRepo.save(site);
            threadsList.add(new Thread(new NewThreadParser(new Node(site.getUrl(), site.getUrl(), site, pageRepo, siteRepo), pageRepo)));
            threadsList.getLast().start(); //запуск каждого нового потока после добавления в список
            site.setStatus_time(new Date());
            site.setStatus(Status.INDEXED);
            siteRepo.changeStatus(site.getId(), site.getStatus(), site.getStatus_time()); //Parameter value [1] did not match expected type Parameter value [2] did not match expected type
        }
    }

    public void stopParsing() {
        for (Thread p : threadsList) {
            StopParsing.stop();
            p.interrupt();//проверить убрать
        }
    }
    public boolean isStarted() {
        return isStarted;
    }

    public boolean isNotShutdown() {
        for (NewThreadParser p : parserList) {
            if (!p.isShutdown()) return true;
        }
        return false;
    }
}
