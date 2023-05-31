package searchengine.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import searchengine.dto.statistics.SetOfPage;
import searchengine.model.Site;
import searchengine.config.SitesList;
import searchengine.model.Status;
import searchengine.repositoryes.LemmaRepository;
import searchengine.services.parsing.NewThreadParser;
import searchengine.services.parsing.Node;
import searchengine.repositoryes.PageRepo;
import searchengine.repositoryes.SiteRepo;
import searchengine.services.parsing.ParseNode;
import searchengine.services.parsing.StopParsing;

import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ForkJoinPool;

@Service
@Getter
@RequiredArgsConstructor
public class ParsingServiceImpl implements ParsingService {
    private static boolean isStarted = false;
    //    IndexingStatus status;
    @Autowired
    PageRepo pageRepo;
    @Autowired
    SiteRepo siteRepo;
    @Autowired
    LemmaRepository lemmaRepository;
    private final SitesList sites;
    private LinkedList<NewThreadParser> parserList = new LinkedList<>();
    private LinkedList<Thread> threadsList = new LinkedList<>();

    @Override
    @Async
    public void startParsing() {
        preparationDB();

        recordPages();
//        IndexingBuilder
        SetOfPage setOfPage;
        isStarted = true;

        for (Site site : sites.getSites()) {
            StopParsing.implNewStop();
            site.setStatus_time(new Date());
            site.setStatus(Status.INDEXING);
            siteRepo.save(site);
            threadsList.add(new Thread(() -> {
                Node node = new Node(site.getUrl(), site.getUrl(),
                        site, pageRepo, siteRepo);
                ForkJoinPool pool = ForkJoinPool.commonPool();
                ParseNode task = new ParseNode(node, pageRepo);
                pool.execute(task);
                Thread.currentThread().setName(site.getName());
                while (Thread.currentThread().isAlive() && pool.getActiveThreadCount() > 0) {
                    if (Thread.currentThread().isInterrupted()) {
                        pool.shutdownNow();
                        System.out.println("Stop while");
                        break;

                    }
                }
                if (!Thread.currentThread().isInterrupted()) {
                    site.setStatus(Status.INDEXED);
                    site.setStatus_time(new Date());
                    siteRepo.changeStatus(site.getId(), site.getStatus(), site.getStatus_time());
                }
            }));
            threadsList.forEach(Thread::start);
        }
    }
    private void preparationDB() {
        siteRepo.deleteAll();
        siteRepo.resetAutoIncrement();
        siteRepo.dropTables();
        pageRepo.createTablePage();
        lemmaRepository.createTableLemma();


    }
    private static void recordPages() {

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
