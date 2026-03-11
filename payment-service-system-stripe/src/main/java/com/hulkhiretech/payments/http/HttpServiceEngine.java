package com.hulkhiretech.payments.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.StripeProviderException;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpServiceEngine {

	private final RestClient restClient;

	public ResponseEntity<String> makeHttpCall(HttpRequest httpRequest) {
		log.info("make http call to communicate external service ");
		try {
			ResponseEntity<String> responseEntity = restClient.method(httpRequest.getHttpMethod())
					.uri(httpRequest.getUrl())
					.headers(restClientHeader -> restClientHeader.addAll(httpRequest.getHeaders()))
					.body(httpRequest.getRequestHttp()).retrieve().toEntity(String.class);

			log.info("Http call completed status code : {}, response body : {}", responseEntity.getStatusCode(),
					responseEntity.getBody());

			return responseEntity;
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			log.error("Error occurred while making http call to external service : {}" + e.getMessage());

			// response entity with error details and error code

			// handle 503 or 504 if get throw stripeProviderException with error code and
			// message
			if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE
					|| e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT) {
				throw new StripeProviderException(ErrorCodeEnum.NO_RESPONSE_FROM_EXTERNAL_SERVICE.getErrorCode(),
						ErrorCodeEnum.NO_RESPONSE_FROM_EXTERNAL_SERVICE.getErrorMessage(),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}

			ResponseEntity<String> errorResponseEntity = ResponseEntity.status(e.getStatusCode())
					.body(e.getResponseBodyAsString());

			return errorResponseEntity;
		} catch (Exception e) {
			log.error("Error occurred call to external service : {}" + e.getMessage());
			throw new StripeProviderException(ErrorCodeEnum.NO_RESPONSE_FROM_EXTERNAL_SERVICE.getErrorCode(),
					ErrorCodeEnum.NO_RESPONSE_FROM_EXTERNAL_SERVICE.getErrorMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@PostConstruct
	public void init() {
		log.info("Initializing HttpServiceEngine... restClient: {}", restClient);
	}
}
