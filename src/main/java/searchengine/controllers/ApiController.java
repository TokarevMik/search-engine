package searchengine.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.statistics.PageIndResponse;
import searchengine.dto.statistics.SearchResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.IndexServiceImpl;
import searchengine.services.ParsingServiceImpl;
import searchengine.services.StatisticsService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;
    private final ParsingServiceImpl parsingService;
    private final IndexServiceImpl indexService ;

    public ApiController(StatisticsService statisticsService, ParsingServiceImpl parsingService, IndexServiceImpl indexService) {
        this.statisticsService = statisticsService;
        this.parsingService = parsingService;
        this.indexService = indexService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public Map<String, String> StartSearching() {
        Map<String, String> response = new HashMap<>();
        if (!parsingService.isStarted()) {
            parsingService.startParsing();
            response.put("result", "true");
        } else {
            response.put("result", "false");
            response.put("error", "Индексация уже запущена");
        }
        ;
        return response;
    }

    @GetMapping("/stopIndexing")
    public Map<String, String> StopSearching() {
        Map<String, String> response = new HashMap<>();
        if (parsingService.isNotShutdown()) {
            response.put("result", "true");
            parsingService.stopParsing();
        } else {
            response.put("result", "false");
            response.put("error", "Индексация не запущена");
        }
        return response;
    }

    @PostMapping("/indexPage")
    public ResponseEntity<PageIndResponse> indexingPage(@RequestParam(name = "page", required = false) String page) {
            PageIndResponse pageIndResponse = indexService.processIndex(page);
        return new ResponseEntity<>(pageIndResponse, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResponse> search(String query, String site) {

        return null; //TO DO
    }
}
