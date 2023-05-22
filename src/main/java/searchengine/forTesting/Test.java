package searchengine.forTesting;

import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.statistics.PageIndResponse;
import searchengine.exceptions.ErrorIndexingException;
import searchengine.model.Site;
import searchengine.services.IndexServiceImpl;
import searchengine.services.parsing.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) {
        List<Site> sitesList = new ArrayList<>();
        Site site1 = new Site();
        site1.setUrl("https://www.playback.ru");
        sitesList.add(site1);
        String page = "https://www.playback.ru/catalog/1304.html";
        doMethod(page, sitesList);
    }

    private static void doMethod(String page, List<Site> sitesList) {
        PageIndResponse pageIndResponse = new PageIndResponse();
        for (Site site : sitesList) {
            String regex = site.getUrl() + "/\\w+";
            Pattern p1 = Pattern.compile(regex);
            Matcher matcher = p1.matcher(page);
            if (matcher.matches()) {
                Node node = new Node(page);
                node.getParseNode();
                System.out.println(node.getStatusCode());
                if (node.getStatusCode() != 200) {
                    System.out.println("not 200");
                    ;
                }
                System.out.println("true");
            } else {
                System.out.println("error");
            }
        }

    }
}