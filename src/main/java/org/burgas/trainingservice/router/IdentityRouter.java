package org.burgas.trainingservice.router;

import lombok.RequiredArgsConstructor;
import org.burgas.trainingservice.filter.IdentityFilter;
import org.burgas.trainingservice.handler.ExceptionHandler;
import org.burgas.trainingservice.handler.IdentityHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class IdentityRouter {

    @Bean
    public RouterFunction<ServerResponse> identityRouting(
            IdentityHandler identityHandler, ExceptionHandler exceptionHandler, IdentityFilter identityFilter
    ) {
        return RouterFunctions.route()
                .path("/api/v1/identities", builder -> builder
                        .filter(identityFilter)
                        .GET("", identityHandler::getAllIdentities)
                        .GET("/by-id", identityHandler::getIdentityById)
                        .POST("/create", identityHandler::createIdentity)
                        .POST("/update", identityHandler::updateIdentity)
                        .DELETE("/delete", identityHandler::deleteIdentity)
                        .POST("/upload-image", identityHandler::uploadImage)
                        .DELETE("/remove-image", identityHandler::removeImage)
                        .POST("/upload-files", identityHandler::uploadFiles)
                        .DELETE("/remove-files", identityHandler::removeFiles)
                        .PUT("/change-password", identityHandler::changePassword)
                        .PUT("/change-status", identityHandler::changeStatus)
                        .PUT("/add-course", identityHandler::addCourse)
                        .PUT("/remove-course", identityHandler::removeCourse)
                        .onError(Throwable.class, exceptionHandler::throwException)
                        .build()
                )
                .build();
    }
}
