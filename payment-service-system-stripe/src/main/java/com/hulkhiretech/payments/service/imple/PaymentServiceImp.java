package com.hulkhiretech.payments.service.imple;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.pojo.CreatePayamentRequest;
import com.hulkhiretech.payments.pojo.PaymentResponse;
import com.hulkhiretech.payments.service.ValidationService;
import com.hulkhiretech.payments.service.helper.CreatepaymentHelper;
import com.hulkhiretech.payments.service.interfaces.PaymentService;
import com.hulkhiretech.payments.stripe.CheckoutSessionResponse;
import com.hulkhiretech.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImp implements PaymentService {

	private final HttpServiceEngine httpEngine;

	private final CreatepaymentHelper createpaymentHelper;

	private final JsonUtil jsonUtil;

	private final ValidationService validationService;

	@Override
	public PaymentResponse createPayment(CreatePayamentRequest createPaymentRequest) {

		// inject the validation service and call the validateCreatePaymentRequest
		// method to validate the createPaymentRequest before processing the payment
		// logic
		validationService.validateCreatePaymentRequest(createPaymentRequest);

		log.info("processing payment logic method");

		HttpRequest httpRequest = createpaymentHelper.prepareStripeCreateSessionRequest(createPaymentRequest);

		// write a code to check createPaymentRequest if success url is null or empty
		// throw an exception stripeProviderException with error code "30001", error
		// message "success url is required" and http status code 400

		ResponseEntity<String> httpResponse = httpEngine.makeHttpCall(httpRequest);

		CheckoutSessionResponse checkoutSessionResponse = createpaymentHelper.processStripeResponse(httpResponse);

		log.info("checkout session response: {}", checkoutSessionResponse);

		log.info("payment logic method completed");

		return mapToPaymentResponse(checkoutSessionResponse);
	}

	/*
	 * write a map method to take checkoutSessionResponse and convert it to
	 * paymentResponse which is our internal response object . this way are not
	 */
	public PaymentResponse mapToPaymentResponse(CheckoutSessionResponse checkoutSessionResponse) {

		if (checkoutSessionResponse == null) {
			log.warn("checkoutSessionResponse is null, cannot map to PaymentResponse");
			return null;
		}

		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setSessionId(checkoutSessionResponse.getId());
		paymentResponse.setSessionUrl(checkoutSessionResponse.getUrl());

		log.info("mapped payment response: {}", paymentResponse);
		return paymentResponse;
	}
}
