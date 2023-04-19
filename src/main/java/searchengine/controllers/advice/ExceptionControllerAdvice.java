package searchengine.controllers.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import searchengine.dto.statistics.ErrorDetails;
import searchengine.exceptions.ErrorIndexingException;
import searchengine.exceptions.NoPageInSateException;

@RestControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(NoPageInSateException.class)
    public ResponseEntity<ErrorDetails> exceptionNotEnoughMoneyHandler() {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage("Данная страница находится за пределами сайтов," +
                "указанных в конфигурационном файле");
        return ResponseEntity
                .badRequest()
                .body(errorDetails);
    }
    @ExceptionHandler(ErrorIndexingException.class)
    public ResponseEntity<ErrorDetails> exceptionIndexErrorHandler() {
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage("Индексация не запущена");
        return ResponseEntity
                .badRequest()
                .body(errorDetails);
    }
}
