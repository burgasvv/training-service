package org.burgas.trainingservice.handler;

import org.burgas.trainingservice.dto.exception.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Component
public class ExceptionHandler {

    public ServerResponse throwException(Throwable throwable, ServerRequest ignoredServerRequest) {
        var exceptionResponse = ExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .code(HttpStatus.BAD_REQUEST.value())
                .message(throwable.getLocalizedMessage())
                .build();
        return ServerResponse.badRequest().body(exceptionResponse);
    }
}
