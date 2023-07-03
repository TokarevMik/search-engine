package searchengine.services.parsing;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import searchengine.dto.statistics.AnchorStop;
import searchengine.dto.statistics.ParsedPages;
import searchengine.dto.statistics.SetOfPage;
import searchengine.model.Page;
import searchengine.repositoryes.PageRepo;
import searchengine.services.ParsingServiceImpl;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

@Getter
@Setter
@Slf4j
public class ParseNode extends RecursiveAction {
    @Autowired
    private PageRepo pageRepo;
    private Node node;
    private SetOfPage currentSetOfPage;

    public Node getNode() {
        return node;
    }

    public ParseNode(Node node, PageRepo pageRepo) {
        this.node = node;
        this.pageRepo = pageRepo;
        currentSetOfPage = ParsingServiceImpl.getSetOfPage();
    }

    public ParseNode(List<ParseNode> parseNodeList, PageRepo pageRepo) {
        startParsing(parseNodeList);
        currentSetOfPage = ParsingServiceImpl.getSetOfPage();

    }//прописать

    private void startParsing(List<ParseNode> parseNodeList) {
        ForkJoinTask.invokeAll(parseNodeList);
    }

    @Override
    protected void compute() {
        System.out.printf("%s -- sent( %d )%n", Thread.currentThread().getName(),
                ForkJoinTask.getPool() == null ? 0 : ForkJoinTask.getPool().getQueuedSubmissionCount());
        if (AnchorStop.getStop()) {
            ForkJoinPool pool = ForkJoinTask.getPool();
            if (pool != null) {
                pool.shutdownNow();
            }
        } else {
            try {
                node.getParseNode(); //составление списка дочерних ссылок
            } catch (Exception e) {
                log.error("NullPointerException: The 'node' parameter must not be empty. URL: {}", node);
                e.printStackTrace();
            }
            Set<ParseNode> taskList = new CopyOnWriteArraySet<>();

            for (Node child : node.getChildren()) { //вызврат списка дочерних ссылок
                 if (!ParsedPages.contain(child.getPath())) {  //проверка есть ли запись в бд
                    ParseNode parseNodeTask = new ParseNode(child, pageRepo); //06.06
                    ParsedPages.addPage(child.getPath()); //добавить логику записи в бд (сейчас в node)
                    WritePage(node.getPage());
                    parseNodeTask.fork();
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    taskList.add(parseNodeTask);
                }
            }
            for (ParseNode task : taskList) {
                try {
                    task.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    } //end of compute

    private void WritePage(Page page) {
        if (currentSetOfPage.getCount() < 300) {
            currentSetOfPage.addPage(page);
        } else {
            pageRepo.saveAll(currentSetOfPage.getRecords());
        }

    }
}