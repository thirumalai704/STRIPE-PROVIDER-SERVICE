package com.hulkhiretech.payments.service.helper;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.hulkhiretech.payments.constant.Constant;
import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.StripeProviderException;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.pojo.CreatePayamentRequest;
import com.hulkhiretech.payments.pojo.ListItem;
import com.hulkhiretech.payments.stripe.CheckoutSessionResponse;
import com.hulkhiretech.payments.stripe.StripeError;
import com.hulkhiretech.payments.stripe.StripeErrorResponse;
import com.hulkhiretech.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreatepaymentHelper {

	@Value("${stripeApiKey}")
	String stripeApiKey;

	@Value("${stripe_create_session_url}")
	String createSessionUrl;

	private final JsonUtil jsonUtil;

	public HttpRequest prepareStripeCreateSessionRequest(CreatePayamentRequest createPayamentRequest) {
		HttpHeaders httpHeaders = new HttpHeaders();

		httpHeaders.setBasicAuth(stripeApiKey, "");
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> formData = buildStripeFormData(createPayamentRequest);

		HttpRequest httpRequest = new HttpRequest();

		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setHeaders(httpHeaders);

		httpRequest.setUrl(createSessionUrl);
		httpRequest.setRequestHttp(formData);
		return httpRequest;
	}

	public static MultiValueMap<String, String> buildStripeFormData(CreatePayamentRequest request) {

		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

		if (request == null) {
			throw new IllegalArgumentException("CreatePayamentRequest cannot be null");
		}

		formData.add(Constant.CREATE_SESSION_MODE, Constant.CREATE_SESSION_MODE_PAYMENT);
		formData.add(Constant.CREATE_SESSION_SUCCESS_URL, request.getSuccessUrl());
		formData.add(Constant.CANCEL_URL, request.getCancelUrl());

		if (request.getListItem() != null) {

			for (int i = 0; i < request.getListItem().size(); i++) {

				ListItem item = request.getListItem().get(i);

				String base = Constant.LINE_ITEMS + i;

				formData.add(base + Constant.PRICE_DATA + Constant.CURRENCY, item.getCurrency());

				formData.add(base + Constant.PRICE_DATA + Constant.UNIT_AMOUNT, String.valueOf(item.getUnitAmount()));

				formData.add(base + Constant.PRICE_DATA + Constant.PRODUCT_DATA + Constant.NAME, item.getProductName());

				formData.add(base + Constant.QUANTITY, String.valueOf(item.getQuantity()));
			}
		}

		return formData;
	}

	public CheckoutSessionResponse processStripeResponse(ResponseEntity<String> httpResponse) {

		// if successful response
		if (httpResponse.getStatusCode().is2xxSuccessful()) {
			log.info("stripe create session request successful, response: {}", httpResponse.getBody());

			CheckoutSessionResponse checkoutSessionResponse = jsonUtil.convertJsonToObject(httpResponse.getBody(),
					CheckoutSessionResponse.class);

			if (checkoutSessionResponse != null && checkoutSessionResponse.getUrl() != null) {
				log.info("checkout session response url: {}", checkoutSessionResponse.getUrl());
				return checkoutSessionResponse;
			}
			log.error("stripe create session response is missing url, response: {}", httpResponse.getBody());
			// you can throw a custom exception here if needed

		}

//		if failed response

		if (httpResponse.getStatusCode().is4xxClientError() || httpResponse.getStatusCode().is5xxServerError()) {
			log.error("stripe create session request failed, status code: {}, response: {}",
					httpResponse.getStatusCode(), httpResponse.getBody());
			// you can throw a custom exception here if needed

			StripeErrorResponse stripeError = jsonUtil.convertJsonToObject(httpResponse.getBody(),
					StripeErrorResponse.class);

			if (stripeError != null && stripeError.getError() != null) {
				log.error("Stripe API error details: Type: {}, Code: {}, Message: {}", stripeError.getError().getType(),
						stripeError.getError().getCode(), stripeError.getError().getMessage());

				String stripeConcatinatedErrorMessage = prepareStripeErrorMessage(stripeError);
				log.error("Prepared Stripe error message: {}", stripeConcatinatedErrorMessage);

				throw new StripeProviderException(ErrorCodeEnum.STRIPE_API_ERROR.getErrorCode(), // DONE
						stripeConcatinatedErrorMessage, // DONE
						HttpStatus.valueOf(httpResponse.getStatusCode().value()));// DONE
			}

			log.error("Stripe API call failed with non-JSON error response. Status code: {}, Response body: {}",
					httpResponse.getStatusCode(), httpResponse.getBody());

		}

		throw new StripeProviderException(ErrorCodeEnum.INVALID_STRIPE_RESPONSE.getErrorCode(),
				ErrorCodeEnum.INVALID_STRIPE_RESPONSE.getErrorMessage(), HttpStatus.BAD_GATEWAY);

	}

	private String prepareStripeErrorMessage(StripeErrorResponse stripeErrorResponse) {

		StripeError error = stripeErrorResponse.getError();

		return Stream.of(error.getType(), // always present
				error.getMessage(), error.getParam(), error.getCode()).filter(Objects::nonNull).map(String::trim)
				.filter(s -> !s.isEmpty()).collect(Collectors.joining(" | "));
	}

}
