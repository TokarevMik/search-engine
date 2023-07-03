package searchengine.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import searchengine.dto.statistics.AnchorStop;
import searchengine.dto.statistics.ParsedPages;
import searchengine.dto.statistics.SetOfPage;
import searchengine.model.Site;
import searchengine.config.SitesList;
import searchengine.model.Status;
import searchengine.repositoryes.*;
import searchengine.services.parsing.Node;
import searchengine.services.parsing.ParseNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Service
@Getter
@RequiredArgsConstructor
@Slf4j
public class ParsingServiceImpl implements ParsingService {
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
    private static SetOfPage setOfPage;
    private static ForkJoinPool forkJoinPool;
//    private final IndexRepo indexRepo;

    @Override
    @Async
    public void startParsing() {
        preparationDB();
        setOfPage = new SetOfPage(); //множество страниц для выгрузки в bd
        AnchorStop.implNewStop();
        forkJoinPool = new ForkJoinPool();  // определить task
        ParsedPages.setInit(); // задать список(хранилище) просмотренных страниц
        List<ParseNode> tasks = new ArrayList<>();
        for (Site site : checkSites()) {
            site.setStatus_time(new Date());
            site.setStatus(Status.INDEXING);
            siteRepo.save(site);
            Node node = new Node(site.getUrl(), site.getUrl(),
                    site, pageRepo, siteRepo);
            if(node==null){
                log.error("NullPointerException: The 'node2' parameter must not be empty. URL: {}", node);
            }
            tasks.add(new ParseNode(node, pageRepo));
        }
        forkJoinPool.invoke(new ParseNode(tasks, pageRepo));
        if(setOfPage.getCount()>0){
            pageRepo.saveAll(setOfPage.getRecords());
            setOfPage.newRecords();
        }
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
            catch (NullPointerException en){
                log.error("NullPointerException: The 'null' parameter must not be empty. URL: {}", site.getUrl());
                log.error("responceCode: code: {}", responceCode);
                log.error("Site length: {}", sites.getSites().size());
            }
        }
        return sitesList;
    }

    private void preparationDB() {
        siteRepo.deleteAll();
        siteRepo.resetAutoIncrement();
        siteRepo.dropTables();
        pageRepo.deletePageTableIfExists();
        pageRepo.createTablePage();
        lemmaRepo.createTableLemma();
        indexRepo.createTableIndex();
        lemmaRawRepo.createTableLemmaRaw();
    }


    public void stopParsing() {
        AnchorStop.stop();
    }
    public static SetOfPage getSetOfPage() {
        return setOfPage;
    }
}
