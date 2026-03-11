package com.hulkhiretech.payments.service.interfaces;

import com.hulkhiretech.payments.pojo.CreatePayamentRequest;
import com.hulkhiretech.payments.pojo.PaymentResponse;

public interface PaymentService {
	PaymentResponse createPayment(CreatePayamentRequest createPayamentRequest);
}
