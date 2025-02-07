package in.hashconnect.controller.code.CustomException;


import in.hashconnect.api.vo.Response;
import in.hashconnect.controller.code.InvoiceNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvoiceNotFound.class)
    public ResponseEntity<Map<String, Object> > invoiceNotFoundException(Exception ex){
        Map<String, Object> exceptionMap = new HashMap<>();
        exceptionMap.put("status", 200);
        exceptionMap.put("desc", ex.getMessage());

        return new ResponseEntity<>(exceptionMap, HttpStatus.OK);
    }

//    @ExceptionHandler(OrderNotFound.class)
//    public ResponseEntity<Map<String, Object> > invoiceNotFoundException(Exception ex){
//        Map<String, Object> exceptionMap = new HashMap<>();
//        exceptionMap.put("status", 200);
//        exceptionMap.put("desc", ex.getMessage());
//
//        return new ResponseEntity<>(exceptionMap, HttpStatus.OK);
//    }
}
