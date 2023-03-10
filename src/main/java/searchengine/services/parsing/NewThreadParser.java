package searchengine.services.parsing;

import searchengine.repositoryes.PageRepo;

import java.util.concurrent.ForkJoinPool;

public class NewThreadParser implements Runnable {

    private final PageRepo pageRepo;
    private boolean isRunning = true;

    public NewThreadParser(Node node, PageRepo pageRepo) {
        this.node = node;
        this.pageRepo = pageRepo;
    }

    ForkJoinPool pool = ForkJoinPool.commonPool();
    Node node;

    @Override
    public void run() {
//        while (isRunning) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ParseNode task = new ParseNode(node, pageRepo);
                pool.invoke(task);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void shutdown() {
//        isRunning = false;
        Thread.currentThread().interrupt();
        pool.shutdownNow();
    }

    public boolean isShutdown() {
        return pool.isShutdown();
    }
//    public void stop(){
//        isRunning = false;
//    }
}
