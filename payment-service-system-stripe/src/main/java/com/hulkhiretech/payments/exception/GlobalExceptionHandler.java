package com.hulkhiretech.payments.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.pojo.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	// handle the StripeProviderException and return response ResponseEntity with
	// error code, error message and http status code from the exception

	@ExceptionHandler(StripeProviderException.class)
	public ResponseEntity<ErrorResponse> handleStripeProviderException(StripeProviderException ex) {
		log.error("StripeProviderException occurred: ", ex);
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode(ex.getErrorCode());
		errorResponse.setErrorMessage(ex.getErrorMessage());
		log.info("StripeProviderException handler method completed errror response: {}", errorResponse);
		return new ResponseEntity<>(errorResponse, ex.getHttpStatusCode());
	}

	// write a exception handler method to handle the generic Exception and return
	// response ResponseEntity with error code "50000", error message "Internal
	// Server Error" and http status code 500

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
		log.error("An unexpected error occurred: ", ex);
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode(ErrorCodeEnum.GENERIC_SERVER_ERROR.getErrorCode());
		errorResponse.setErrorMessage(ErrorCodeEnum.GENERIC_SERVER_ERROR.getErrorMessage());
		log.info("Generic exception handler method completed errror response: {}", errorResponse);
		return new ResponseEntity<>(errorResponse, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
