package com.hulkhiretech.payments.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Represents an item in the payment request, including currency, product name, unit amount, and quantity.")
public class ListItem {
	private String currency;
	private String productName;
	private int unitAmount;
	private int quantity;
}
