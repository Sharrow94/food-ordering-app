package com.food.ordering.system.order.service.domain;

import java.util.List;

import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRespository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.domain.ports.output.repository.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.domain.ports.output.repository.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;

@SpringBootApplication(scanBasePackages = "com.food.ordering.system")
public class OrderTestConfiguration {

	@Bean
	public OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher() {
		return Mockito.mock(OrderCreatedPaymentRequestMessagePublisher.class);
	};
	
	@Bean
	public OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher() {
		return Mockito.mock(OrderCancelledPaymentRequestMessagePublisher.class);
	}
	
	@Bean
	public OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher() {
		return Mockito.mock(OrderPaidRestaurantRequestMessagePublisher.class);
	}
	
	@Bean
	public OrderRepository orderRepository() {
		return Mockito.mock(OrderRepository.class);
	}
	
	@Bean
	public CustomerRespository customerRespository() {
		return Mockito.mock(CustomerRespository.class);
	}
	
	@Bean
	public RestaurantRepository restaurantRepository() {
		return Mockito.mock(RestaurantRepository.class);
	}
	
	@Bean
	public OrderDomainService orderDomainService() {
		return new OrderDomainServiceImpl();
	}
}
