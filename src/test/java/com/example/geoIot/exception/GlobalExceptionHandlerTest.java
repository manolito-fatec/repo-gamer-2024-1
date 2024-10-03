package com.example.geoIot.exception;

import com.example.geoIot.exception.ControllerAdvice.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    public void testHandleInvalidRequestException() {
        InvalidRequestException ex = new InvalidRequestException("Invalid request");
        ResponseEntity<String> response = exceptionHandler.handleInvalidRequestException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Solicitação inválida. Verifique os dados recebidos");
    }

    @Test
    public void testHandleInvalidInputException() {
        InvalidInputException ex = new InvalidInputException("Invalid input");
        ResponseEntity<String> response = exceptionHandler.handleInvalidInputException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Solicitação inválida. Verifique os dados enviados");
    }

    @Test
    public void testHandleNoDataFoundException() {
        NoDataFoundException ex = new NoDataFoundException("No data found");
        ResponseEntity<String> response = exceptionHandler.handleNoDataFoundException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isEqualTo("Nenhum conteúdo disponível");
    }

    @Test
    public void testHandleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");
        ResponseEntity<String> response = exceptionHandler.handleResourceNotFoundException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("O recurso solicitado não foi encontrado");
    }

    @Test
    public void testHandleRequestTimeoutException() {
        RequestTimeoutException ex = new RequestTimeoutException("Request timeout");
        ResponseEntity<String> response = exceptionHandler.handleRequestTimeoutException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.REQUEST_TIMEOUT);
        assertThat(response.getBody()).isEqualTo("A requisição demorou muito para ser processada. Tente novamente");
    }

    @Test
    public void testHandleInternalServerErrorException() {
        InternalServerErrorException ex = new InternalServerErrorException("Internal server error");
        ResponseEntity<String> response = exceptionHandler.handleInternalServerErrorException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("Erro interno no servidor. Tente novamente mais tarde");
    }
}