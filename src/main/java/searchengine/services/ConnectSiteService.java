package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.helper.ValidationException;

import java.io.IOException;
@Slf4j
public class ConnectSiteService {
    String url;
    private Document doc;
    private int responseStatus;
    Connection.Response response;


    public Connection.Response getResponse(){
        try{response = Jsoup.connect(url).timeout(0).userAgent
                        ("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6)" +
                                " Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com").maxBodySize(0).execute();}
        catch (HttpStatusException httpEx){
            responseStatus = response.statusCode(); //переделать
        }
        catch (ValidationException e) {
            log.error("ValidationException: The 'url' parameter must not be empty. URL: {}", url);
        }
        catch (IOException ex){
            response.statusCode(); //переделать
        }
        catch (NullPointerException en){
            log.error("NullPointerException Connect: The 'null' parameter must not be empty. URL: {}", url);
        }
        return response;
    }

    public ConnectSiteService(String url) throws IOException {
        this.url=url;
    }
}
