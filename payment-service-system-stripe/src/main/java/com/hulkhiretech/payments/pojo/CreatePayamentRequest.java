package com.hulkhiretech.payments.pojo;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Represents a request to create a payment, including success and cancel URLs, and a list of items to be paid for.")
public class CreatePayamentRequest {

	// write a description for successUrl and cancelUrl and examples for both of
	// them in the schema annotation
	@Schema(description = "The URL to which the user will be redirected after a successful payment. This URL should handle the post-payment logic, such as confirming the payment and providing a receipt.", example = "https://www.example.com/payment-success")
	private String successUrl;

	@Schema(description = "The URL to which the user will be redirected if the payment is canceled. This URL should handle the logic for canceled payments, such as allowing the user to retry or providing information on how to proceed.", example = "https://www.example.com/payment-cancel")
	private String cancelUrl;

	@Schema(description = "A list of items that the user is paying for. Each item should include details such as name, quantity, and price.", example = "[{\"name\": \"Item 1\", \"quantity\": 2, \"price\": 10.00}, {\"name\": \"Item 2\", \"quantity\": 1, \"price\": 20.00}]")
	List<ListItem> listItem;

}
