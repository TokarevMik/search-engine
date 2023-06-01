package searchengine.services;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ConnectSiteService {
    String url;
    private Document doc;
    private int responseStatus;
    Connection.Response response;


    public Connection.Response getResponse() throws IOException {
        try{response = Jsoup.connect(url).timeout(0).userAgent
                        ("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6)" +
                                " Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com").maxBodySize(0).execute();}
        catch (HttpStatusException httpEx){
            responseStatus = response.statusCode();
        } catch (IOException ex){
            response.statusCode();
        }
        return response;
    }

    public ConnectSiteService(String url) throws IOException {
    }
}
