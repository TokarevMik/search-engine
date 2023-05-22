package searchengine.controllers.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import searchengine.dto.statistics.ErrorDetails;
import searchengine.exceptions.ErrorIndexingException;
import searchengine.exceptions.NoPageInSiteException;

@RestControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(NoPageInSiteException.class)
    public ResponseEntity<ErrorDetails> exceptionNotEnoughMoneyHandler() {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setError("Данная страница находится за пределами сайтов," +
                "указанных в конфигурационном файле");
        return ResponseEntity
                .badRequest()
                .body(errorDetails);
    }
    @ExceptionHandler(ErrorIndexingException.class)
    public ResponseEntity<ErrorDetails> exceptionIndexErrorHandler() {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setError("Индексация не запущена");
        return ResponseEntity
                .badRequest()
                .body(errorDetails);
    }
}
