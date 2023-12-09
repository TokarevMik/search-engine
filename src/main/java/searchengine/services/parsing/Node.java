package searchengine.services.parsing;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
    @Autowired
    public Node(String url, String domain, Site site, PageRepo pageRepo, SiteRepo siteRepo,String path) {
        this.url = url;
        Node.domain = domain;
        this.site = site;
        this.pageRepo = pageRepo;
        this.siteRepo = siteRepo;
        this.path = path;
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
            contentOfPage = doc.html();//содержимое body (ссылки)
            Element content = doc.body();
            bodyText = content.text(); //текстовое содержимое
            page.setContent(contentOfPage);
            Elements links = content.select("a[href]");
            //TODO проверить заполнение path
            page.setPath(checkPath(url,domain));
            page.setSite(site);
            for (Element link : links) {
                String linkHref = link.attr("abs:href");
                if (linkCheck(linkHref)) {
                    if (!checkDomain(linkHref, domain)) linkHref = domain.concat(linkHref);
                    String childPath = checkPath(linkHref,domain);
                    if(childPath==null) System.out.println("Null path !!!!" +linkHref );
                    if(page.getPath()==null){
                        System.out.println("2) Null path !!!! - " +linkHref);}
                    nodes.add(new Node(linkHref, domain, site, pageRepo, siteRepo, childPath));   // добавление дочерней ссылки в список , но уровнем не ниже 1 от родительской
                }
                ////////
                /*if (!linkHref.contains("tel:") && !linkHref.contains("callto:")) {
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
                }*/
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

    private boolean linkCheck(String linkHref) {
        if (checkDomain(linkHref, domain)) {
            return forbiddenSymbol(linkHref);
        } if (linkHref.startsWith("/")) {
            return forbiddenSymbol(linkHref);
        } else
            return false; //если нет домена и ссылка не локальная - выход
    }
    private String checkPath(String url, String domain){
        String path = null;
        if (url.equals(domain)) {
            path = domain;
        } else {
            if (checkDomain(url, domain)) {
                path = url.replaceFirst("[\\w]+://[\\w\\.]+", "");
            }
        }
        return path;
    }

    private boolean checkDomain(String url, String domain) {
        return url.contains(domain) || url.contains(domain.substring(12)); //проверка наличия домена в ссылке с www и без
    }

    boolean forbiddenSymbol(String linkHref) {
        for (String fbSym : EXTENSIONS_LIST) {
            if (linkHref.contains(fbSym)) {
                return false;
            }
        }
        return true;
    }

    private static final String[] EXTENSIONS_LIST = {"redirect", "php", "js",
            "#", ".jpg", ".jpeg", ".png", ".gif", ".pdf",
            ".php", "mailto:", "resolve?", "go?", "callto", "tel:"};

    @Transactional
    private void savePage(Page p) {
        if (pageRepo.findDistinctByPath(p.getPath()).isEmpty()) {
            pageRepo.save(p);
        }
    }
}