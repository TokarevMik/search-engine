package searchengine.parsing;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import searchengine.repositoryes.PageRepo;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.RecursiveAction;
@Getter
@Setter
public class ParseNode extends RecursiveAction {
    @Autowired
    private PageRepo pageRepo;
    private Node node;
    public Node getNode() {
        return node;
    }
    public ParseNode(Node node,PageRepo pageRepo) {
        this.node = node;
        this.pageRepo = pageRepo;
    }
    @Override
    protected void compute() {
//        String url = node.getUrl(); //текущий адрес
        node.getParseNode();

        Set<ParseNode> taskList = new CopyOnWriteArraySet<>();

        for (Node child : node.getChildren()) {
            if (pageRepo.findDistinctByPath(node.getUrl()).isEmpty()) {
                ParseNode parseNodeTask = new ParseNode(child,pageRepo);
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

}

