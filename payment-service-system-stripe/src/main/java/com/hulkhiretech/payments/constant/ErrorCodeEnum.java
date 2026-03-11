package com.hulkhiretech.payments.constant;

public enum ErrorCodeEnum {

	CREATE_PAYMENT_REQUEST_REQUIRED("30001", "CreatePaymentRequest is required"),
	SUCCESS_URL_REQUIRED("30002", "Success URL is required"), CANCEL_URL_REQUIRED("30003", "Cancel URL is required"),
	SUCCESS_URL_INVALID("30010", "Success URL must be valid http/https"),
	CANCEL_URL_INVALID("30011", "Cancel URL must be valid http/https"),
	AT_LEAST_ONE_LIST_ITEM_REQUIRED("30004", "At least one list item is required"),
	LIST_ITEM_CANNOT_BE_NULL("30005", "List item cannot be null"), CURRENCY_REQUIRED("30006", "Currency is required"),
	PRODUCT_NAME_REQUIRED("30007", "Product name is required"),
	UNIT_AMOUNT_GREATER_THAN_ZERO("30008", "Unit amount must be greater than zero"),
	QUANTITY_GREATER_THAN_ZERO("30009", "Quantity must be greater than zero"),
	GENERIC_SERVER_ERROR("30000", "Internal Server Error"),
	NO_RESPONSE_FROM_EXTERNAL_SERVICE("30021", "Invalid response from external service"),
	STRIPE_API_ERROR("30022", "Error from Stripe API"),
	INVALID_STRIPE_RESPONSE("30023", "Invalid response from Stripe API");

	private final String errorCode;
	private final String errorMessage;

	ErrorCodeEnum(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
