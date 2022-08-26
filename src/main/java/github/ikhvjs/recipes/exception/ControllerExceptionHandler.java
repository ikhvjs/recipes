package github.ikhvjs.recipes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolation;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage resourceNotFoundException(ResourceNotFoundException e, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                List.of(e.getMessage()),
                request.getDescription(false));
    }

    @ExceptionHandler({URISyntaxException.class, Exception.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage globalExceptionHandler(Exception e, WebRequest request) {
        return new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                List.of(e.getMessage()),
                request.getDescription(false));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e, WebRequest request) {
        List<String> messages = e.getBindingResult().getAllErrors().stream()
                .map(err -> err.unwrap(ConstraintViolation.class))
                .map(err -> String.format("%s : %s", err.getPropertyPath(), err.getMessage()))
                .collect(Collectors.toList());

        return new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                messages,
                request.getDescription(false));
    }

}
