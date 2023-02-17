package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.ParsingServiceImpl;
import searchengine.services.StatisticsService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;
    private final ParsingServiceImpl parsingService;

    public ApiController(StatisticsService statisticsService, ParsingServiceImpl parsingService) {
        this.statisticsService = statisticsService;
        this.parsingService = parsingService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
    @GetMapping("/startIndexing")
    public Map<String,String> StartSearching(){
        Map<String,String> response= new HashMap<>();
        if(parsingService.startParsing()){
            response.put("result","true");
        } else {
            response.put("result","false");
            response.put("error","Индексация уже запущена");
        };
        return response;
    }
    @GetMapping("/stopIndexing")
    public Map<String,String> StopSearching(){
        Map<String,String> response= new HashMap<>();
        if(!parsingService.isStarted()){
            parsingService.stopParsing();
            response.put("result","true");
        }else {
            response.put("result","false");
            response.put("error","Индексация не запущена");
        }
        return response;
    }
}
