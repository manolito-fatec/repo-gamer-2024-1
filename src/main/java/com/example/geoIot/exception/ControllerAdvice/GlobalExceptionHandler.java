package com.example.geoIot.exception.ControllerAdvice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<String> handleInvalidRequestException(InvalidRequestException ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>("Solicitação inválida. Verifique os dados recebidos", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<String> handleInvalidInputException(InvalidInputException ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>("Solicitação inválida. Verifique os dados enviados", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<String> handleNoDataFoundException(NoDataFoundException ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>("Nenhum conteúdo disponível", HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>("O recurso solicitado não foi encontrado", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RequestTimeoutException.class)
    public ResponseEntity<String> handleRequestTimeoutException(RequestTimeoutException ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>("A requisição demorou muito para ser processada. Tente novamente", HttpStatus.REQUEST_TIMEOUT);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<String> handleInternalServerErrorException(InternalServerErrorException ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>("Erro interno no servidor. Tente novamente mais tarde ou acione o suporte", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
