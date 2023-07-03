package searchengine.services.parsing;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repositoryes.PageRepo;
import searchengine.repositoryes.SiteRepo;
import searchengine.services.ConnectSiteService;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
public class Node {
    static volatile boolean isCanceled = false;
    private PageRepo pageRepo;
    private SiteRepo siteRepo;
    private String url;
    private Site site;
    private String path; // адрес для бд
    private static String domain;
    String bodyText = "";
    private Integer statusCode;
    private String contentOfPage = "";
    Page page;

    public Node(String url) {
        this.url = url;
        page = new Page();
    }

    @Autowired
    public Node(String url, String domain, Site site, PageRepo pageRepo, SiteRepo siteRepo) {
        this.url = url;
        Node.domain = domain;
        this.site = site;
        this.pageRepo = pageRepo;
        this.siteRepo = siteRepo;
        page = new Page();
    }

    private Collection<Node> nodes = new HashSet<>();

    public Collection<Node> getChildren() {
        return nodes;
    }

    public void getParseNode() {
        try {
            Thread.sleep(200);
            Connection.Response response = new ConnectSiteService(url).getResponse();
            statusCode = response.statusCode();
            page.setCode(statusCode);
            Document doc = response.parse();
            contentOfPage = doc.html();//содержимое страницы
            Element content = doc.body();
            bodyText = content.text(); //
            page.setContent(contentOfPage);
            Elements links = content.select("a[href]");
            if (url.equals(domain)) {
                path = domain;
            } else {
                if (url.contains(domain)) {
                    path = url.replace(domain, "");
                }
            }
            page.setPath(path);
            page.setSite(site);
            for (Element link : links) {
                String linkHref = link.attr("abs:href");
                if (!linkHref.contains("tel:") && !linkHref.contains("callto:")) {
                    if (!linkHref.contains("http")) {
                        linkHref = domain.concat(linkHref);
                        nodes.add(new Node(linkHref, domain, site, pageRepo, siteRepo));
                    }
                    if (linkHref.contains(domain)) {
                        Pattern pattern = Pattern.compile("^(https?://)?/{0,1}([\\w\\.\\-&&[^@]]+/?)*([/\\w\\.\\-&&[^@]])*[^(.pdf),^(.jpg)]/?$");
                        Matcher matcher = pattern.matcher(linkHref);
                        if (matcher.matches()) {
                            nodes.add(new Node(linkHref, domain, site, pageRepo, siteRepo));
                        }
                    }
                }
            }
        } catch (HttpStatusException se) {
            path = url.replace(domain, "");
            contentOfPage = "";
            statusCode = se.getStatusCode();
        } catch (UnknownHostException | IllegalArgumentException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    private void savePage(Page p) {
        if (pageRepo.findDistinctByPath(p.getPath()).isEmpty()) {
            pageRepo.save(p);
        }
    }
}