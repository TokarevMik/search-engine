package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.ParsingServiceImpl;
import searchengine.services.StatisticsService;

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
    public void StartSearching(){

        parsingService.startParsing();
    }
    @GetMapping("/stopIndexing")
    public void StopSearching(){

        parsingService.startParsing();
    }
}
