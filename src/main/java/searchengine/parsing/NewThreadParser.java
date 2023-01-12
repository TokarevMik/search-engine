package searchengine.parsing;

import org.springframework.beans.factory.annotation.Autowired;
import searchengine.repositoryes.PageRepo;

public class NewThreadParser implements Runnable {
    @Autowired
    private PageRepo pageRepo;
    public NewThreadParser(Node node,PageRepo pageRepo) {
        this.node = node;
        this.pageRepo = pageRepo;
    }

    Node node;
    @Override
    public void run() {
        ParseNode task = new ParseNode(node,pageRepo);
        task.invoke();
    }
}
