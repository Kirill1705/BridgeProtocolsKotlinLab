package thor.bridge_tournament.presentation.rest.exception

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import thor.bridge_tournament.core.exception.BoardNotFoundException
import thor.bridge_tournament.core.exception.DomainValidationException
import thor.bridge_tournament.core.exception.UserNotFoundException

@RestControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(
        DomainValidationException::class,
        IllegalArgumentException::class
    )
    fun handleValidation(exception: RuntimeException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.message ?: "Request failed")
    }

    @ExceptionHandler(
        BoardNotFoundException::class,
        UserNotFoundException::class
    )
    fun handleNotFound(exception: RuntimeException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.message ?: "Request failed")
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(exception: Exception?): ProblemDetail {
        log.error("Unexpected REST request failure", exception)
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal server error"
        )
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(RestExceptionHandler::class.java)
    }
}
