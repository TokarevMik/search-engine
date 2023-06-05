package searchengine.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import searchengine.dto.statistics.AnchorStop;
import searchengine.dto.statistics.SetOfPage;
import searchengine.model.Site;
import searchengine.config.SitesList;
import searchengine.model.Status;
import searchengine.repositoryes.*;
import searchengine.services.parsing.NewThreadParser;
import searchengine.services.parsing.Node;
import searchengine.services.parsing.ParseNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Service
@Getter
@RequiredArgsConstructor
public class ParsingServiceImpl implements ParsingService {
    //    private static boolean isStarted = false;
    //    IndexingStatus status;
    @Autowired
    PageRepo pageRepo;
    @Autowired
    SiteRepo siteRepo;
    @Autowired
    LemmaRepo lemmaRepo;
    @Autowired
    IndexRepo indexRepo;
    @Autowired
    LemmaRawRepo lemmaRawRepo;
    private final SitesList sites;
//    private LinkedList<NewThreadParser> parserList = new LinkedList<>();
//    private LinkedList<Thread> threadsList = new LinkedList<>();
    private static ForkJoinPool forkJoinPool;
//    private final IndexRepo indexRepo;

    @Override
    @Async
    public void startParsing() {
        preparationDB();
        recordPages();
//        IndexingBuilder
        SetOfPage setOfPage;
//        isStarted = true;
        AnchorStop.implNewStop();
//        List<Site> availableSites = checkSites();
        forkJoinPool = new ForkJoinPool();  // определить task
        List<ParseNode> tasks = new ArrayList<>();

        for (Site site : checkSites()) {
            site.setStatus_time(new Date());
            site.setStatus(Status.INDEXING);
            siteRepo.save(site);
            Node node = new Node(site.getUrl(), site.getUrl(),
                    site, pageRepo, siteRepo);
            tasks.add(new ParseNode(node, pageRepo));

            /*threadsList.add(new Thread(() -> {
                Node node = new Node(site.getUrl(), site.getUrl(),
                        site, pageRepo, siteRepo);
                ForkJoinPool pool = ForkJoinPool.commonPool();
                ParseNode task = new ParseNode(node, pageRepo);
                pool.execute(task);
                Thread.currentThread().setName(site.getName());
                while (Thread.currentThread().isAlive() && pool.getActiveThreadCount() > 0) {
                    if (Thread.currentThread().isInterrupted()) {
                        pool.shutdownNow();
                        break;

                    }
                }
                if (!Thread.currentThread().isInterrupted()) {
                    site.setStatus(Status.INDEXED);
                    site.setStatus_time(new Date());
//                    siteRepo.changeStatus(site.getId(), site.getStatus(), site.getStatus_time()); //Установка статуса сайта
                }
            }));*/
//            threadsList.forEach(Thread::start);
        }
        forkJoinPool.invoke(new ParseNode(tasks, pageRepo));
    }



    private List<Site> checkSites() {
        List<Site> sitesList = new ArrayList<>();

        for (Site site : sites.getSites()) {
            int responceCode = 0;
            try {
                Connection.Response response = new ConnectSiteService(site.getUrl()).getResponse();
                responceCode = response.statusCode();
                if (responceCode != 200) {
                    siteRepo.changeStatus(site.getUrl(), Status.FAILED, site.getStatus_time(), "Ошибка индексации: главная страница сайта не доступна");
                } else {
                    sitesList.add(site);
                }
            } catch (IOException ex) {
                siteRepo.changeStatus(site.getUrl(), Status.FAILED, site.getStatus_time(), "Ошибка индексации: главная страница сайта не доступна");
            }
        }
        return sitesList;
    }

    private void preparationDB() {
        siteRepo.deleteAll();
        siteRepo.resetAutoIncrement();
        siteRepo.dropTables();
        pageRepo.createTablePage();
        lemmaRepo.createTableLemma();
        indexRepo.createTableIndex();
        lemmaRawRepo.createTableLemmaRaw();

    }

    private static void recordPages() {

    }

    public void stopParsing() {
        /*for (Thread p : threadsList) {
            AnchorStop.stop();
            p.interrupt();//проверить убрать
        }*/
    }

//    public boolean isStarted() {
//        return isStarted;
//    }

//    public boolean isNotShutdown() {
//        for (NewThreadParser p : parserList) {
//            if (!p.isShutdown()) return true;
//        }
//        return false;
//    }
}
