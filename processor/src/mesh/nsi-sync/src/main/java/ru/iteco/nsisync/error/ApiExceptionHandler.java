package ru.iteco.nsisync.error;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.iteco.nsisync.error.dto.ApiErrorFactory;
import ru.iteco.nsisync.error.dto.ApiErrorResponse;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ApplicationException.class})
    public ResponseEntity<Object> handleApplicationException(Exception ex, WebRequest request) {
        return prepareRestException(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NotFoundException.class, EntityNotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(Exception ex, WebRequest request) {
        return prepareRestException(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({InternalException.class})
    public ResponseEntity<Object> handleInternalException(Exception ex, WebRequest request) {
        return prepareRestException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        return prepareRestException(ex, request, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        String message = String.format("Validation failed for object='%s'. Error count: %s",
                bindingResult.getTarget().getClass().getSimpleName(),
                bindingResult.getErrorCount());
        final ApiErrorResponse response = ApiErrorResponse.valueOf(
                HttpStatus.BAD_REQUEST.value(), getPath(request), message, ex.getClass().getName());
        bindingResult.getAllErrors().forEach(e ->
                response.getErrors().add(ApiErrorFactory.buildFromObjectError(e)));
        return handleExceptionInternal(ex, response, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return prepareRestException(ex, request, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return prepareRestException(ex, request, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return prepareRestException(ex, request, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return prepareRestException(ex, request, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> prepareRestException(Exception ex, WebRequest request, HttpStatus status) {
        String error = ex.getLocalizedMessage();
        if (ex.getCause() != null) {
            error += String.format(". %s", ex.getCause().getMessage());
        } else if (StringUtils.isEmpty(error)) {
            error = "message not available";
        }
        final ApiErrorResponse response = ApiErrorResponse.valueOf(
                status.value(), getPath(request), error, ex.getClass().getName());
        return new ResponseEntity<>(response, new HttpHeaders(), status);
    }

    private String getPath(WebRequest request) {
        return ((ServletWebRequest) request)
                .getRequest()
                .getRequestURI();
    }
}

