package com.food.ordering.system.order.service.domain.exception;

public class OrderNotFoundException extends OrderDomainException{
	
	private static final long serialVersionUID = 2826937326136770542L;
	
	public OrderNotFoundException(String message) {
		super(message);
	}

	public OrderNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
