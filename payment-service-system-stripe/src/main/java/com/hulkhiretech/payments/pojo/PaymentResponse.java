package com.hulkhiretech.payments.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Represents the response of a payment creation request, including the session ID and session URL for the created payment.")
public class PaymentResponse {

	private String sessionId;
	private String sessionUrl;
}
