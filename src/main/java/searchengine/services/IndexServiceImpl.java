package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.dto.statistics.PageIndResponse;
import searchengine.exceptions.ErrorIndexingException;
import searchengine.model.Site;
import searchengine.services.parsing.Node;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
@RequiredArgsConstructor
public class IndexServiceImpl implements IndexService {
    private final SitesList sites;


    public PageIndResponse processIndex(String page) {
        List<Site> sitesList = sites.getSites();
        PageIndResponse pageIndResponse = new PageIndResponse();
        for (Site site : sitesList) {
            String regex = site.getUrl() + "/\\w+";
            Pattern p1 = Pattern.compile(regex);
            Matcher matcher = p1.matcher(page);
            if (matcher.matches()) {
                Node node = new Node(page);
                node.getParseNode();
                if(node.getStatusCode()!=200){
                    throw new ErrorIndexingException();
                }
                pageIndResponse.setResult("true");
                return pageIndResponse;
            } else {
                throw new ErrorIndexingException();
            }
        }

        return pageIndResponse;
    }

}
