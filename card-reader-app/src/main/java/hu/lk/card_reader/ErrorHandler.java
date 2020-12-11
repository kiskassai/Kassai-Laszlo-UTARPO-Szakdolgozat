package hu.lk.card_reader;

import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandler {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CardReaderApplication.class);
    @ExceptionHandler({Exception.class})
    ResponseEntity<String> handleUnknownError(Exception e) {
        log.error("Unexpected error", e);
        return ResponseEntity.status(500).body(e.getMessage());
    }
    
}
