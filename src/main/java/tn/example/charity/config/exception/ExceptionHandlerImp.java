package tn.example.charity.config.exception;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import java.util.Arrays;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import tn.example.charity.exception.BadRequestException;
import tn.example.charity.exception.NotFoundException;
import tn.example.charity.exception.UnauthorisedException;

@ControllerAdvice
public class ExceptionHandlerImp extends ResponseEntityExceptionHandler {

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ExceptionResponse> notFoundException(final NotFoundException e, final WebRequest request) {
		final var exceptionResponse = new ExceptionResponse(NOT_FOUND.value(), "Not Found Exception", e.getMessage(),
				request.getContextPath(), Arrays.asList(e.getLocalizedMessage()));
		return ResponseEntity.status(NOT_FOUND).contentType(APPLICATION_JSON).body(exceptionResponse);
	}

	@ExceptionHandler(UnauthorisedException.class)
	public ResponseEntity<ExceptionResponse> unauthorisedException(final UnauthorisedException e,
			final WebRequest request) {
		final var exceptionResponse = new ExceptionResponse(UNAUTHORIZED.value(), "unauthorised Exception",
				e.getMessage(), request.getContextPath(), Arrays.asList(e.getLocalizedMessage()));
		return ResponseEntity.status(UNAUTHORIZED).contentType(APPLICATION_JSON).body(exceptionResponse);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ExceptionResponse> badRequestException(final BadRequestException e,
			final WebRequest request) {
		final var exceptionResponse = new ExceptionResponse(BAD_REQUEST.value(), "bad request Exception",
				e.getMessage(), request.getContextPath(), Arrays.asList(e.getLocalizedMessage()));
		return ResponseEntity.status(BAD_REQUEST).contentType(APPLICATION_JSON).body(exceptionResponse);
	}
}