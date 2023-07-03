package searchengine.services.parsing;

import searchengine.repositoryes.PageRepo;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
//TODO - проверить использование класса и удалитьесли не используется
public class NewThreadParser implements Runnable {
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private final PageRepo pageRepo;

    public NewThreadParser(Node node, PageRepo pageRepo) {
        this.node = node;
        this.pageRepo = pageRepo;
    }

    ForkJoinPool pool = ForkJoinPool.commonPool();
    Node node;

    @Override
    public void run() {
        isRunning.set(true);
        while (isRunning.get()) {
            ParseNode task = new ParseNode(node, pageRepo);
//            pool.invoke(task);
            pool.execute(task);
        }
        }

    public void interrupt() {
        isRunning.set(false);
        pool.shutdownNow();
    }

    public boolean isShutdown() {
        return pool.isShutdown();
    }
/*//    public void stop(){
//        isRunning = false;
//    }*/
}
