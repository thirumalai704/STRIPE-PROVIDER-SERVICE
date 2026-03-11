package com.hulkhiretech.payments.service;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.hulkhiretech.payments.constant.ErrorCodeEnum;
import com.hulkhiretech.payments.exception.StripeProviderException;
import com.hulkhiretech.payments.pojo.CreatePayamentRequest;
import com.hulkhiretech.payments.pojo.ListItem;

@Service
public class ValidationService {

	public void validateCreatePaymentRequest(CreatePayamentRequest request) {

		if (request == null) {
			throw new StripeProviderException(ErrorCodeEnum.CREATE_PAYMENT_REQUEST_REQUIRED.getErrorCode(),
					ErrorCodeEnum.CREATE_PAYMENT_REQUEST_REQUIRED.getErrorMessage(), HttpStatus.BAD_REQUEST);
		}

		if (request.getSuccessUrl() == null || request.getSuccessUrl().trim().isEmpty()) {

			throw new StripeProviderException(ErrorCodeEnum.SUCCESS_URL_REQUIRED.getErrorCode(),
					ErrorCodeEnum.SUCCESS_URL_REQUIRED.getErrorMessage(), HttpStatus.BAD_REQUEST);
		}

		if (request.getCancelUrl() == null || request.getCancelUrl().trim().isEmpty()) {

			throw new StripeProviderException(ErrorCodeEnum.CANCEL_URL_REQUIRED.getErrorCode(),
					ErrorCodeEnum.CANCEL_URL_REQUIRED.getErrorMessage(), HttpStatus.BAD_REQUEST);
		}

		if (!isValidUrl(request.getSuccessUrl())) {
			throw new StripeProviderException(ErrorCodeEnum.SUCCESS_URL_INVALID.getErrorCode(),
					ErrorCodeEnum.SUCCESS_URL_INVALID.getErrorMessage(), HttpStatus.BAD_REQUEST);
		}

		if (!isValidUrl(request.getCancelUrl())) {
			throw new StripeProviderException(ErrorCodeEnum.CANCEL_URL_INVALID.getErrorCode(),
					ErrorCodeEnum.CANCEL_URL_INVALID.getErrorMessage(), HttpStatus.BAD_REQUEST);
		}

		if (request.getListItem() == null || request.getListItem().isEmpty()) {

			throw new StripeProviderException(ErrorCodeEnum.AT_LEAST_ONE_LIST_ITEM_REQUIRED.getErrorCode(),
					ErrorCodeEnum.AT_LEAST_ONE_LIST_ITEM_REQUIRED.getErrorMessage(), HttpStatus.BAD_REQUEST);
		}

		for (ListItem item : request.getListItem()) {

			if (item == null) {
				throw new StripeProviderException(ErrorCodeEnum.LIST_ITEM_CANNOT_BE_NULL.getErrorCode(),
						ErrorCodeEnum.LIST_ITEM_CANNOT_BE_NULL.getErrorMessage(), HttpStatus.BAD_REQUEST);
			}

			if (item.getCurrency() == null || item.getCurrency().trim().isEmpty()) {

				throw new StripeProviderException(ErrorCodeEnum.CURRENCY_REQUIRED.getErrorCode(),
						ErrorCodeEnum.CURRENCY_REQUIRED.getErrorMessage(), HttpStatus.BAD_REQUEST);
			}

			if (item.getProductName() == null || item.getProductName().trim().isEmpty()) {

				throw new StripeProviderException(ErrorCodeEnum.PRODUCT_NAME_REQUIRED.getErrorCode(),
						ErrorCodeEnum.PRODUCT_NAME_REQUIRED.getErrorMessage(), HttpStatus.BAD_REQUEST);
			}

			if (item.getUnitAmount() <= 0) {
				throw new StripeProviderException(ErrorCodeEnum.UNIT_AMOUNT_GREATER_THAN_ZERO.getErrorCode(),
						ErrorCodeEnum.UNIT_AMOUNT_GREATER_THAN_ZERO.getErrorMessage(),
						HttpStatus.BAD_REQUEST);
			}

			if (item.getQuantity() <= 0) {
				throw new StripeProviderException(ErrorCodeEnum.QUANTITY_GREATER_THAN_ZERO.getErrorCode(),
						ErrorCodeEnum.QUANTITY_GREATER_THAN_ZERO.getErrorMessage(), HttpStatus.BAD_REQUEST);
			}
		}
	}

	private boolean isValidUrl(String url) {
		try {
			URL u = new URL(url.trim());
			String protocol = u.getProtocol();

			return ("http".equalsIgnoreCase(protocol) || "https".equalsIgnoreCase(protocol)) && u.getHost() != null
					&& !u.getHost().trim().isEmpty();

		} catch (MalformedURLException e) {
			return false;
		}
	}
}
