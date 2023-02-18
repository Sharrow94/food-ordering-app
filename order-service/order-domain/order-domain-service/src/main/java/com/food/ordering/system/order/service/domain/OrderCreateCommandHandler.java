package com.food.ordering.system.order.service.domain;

import java.util.Optional;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRespository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderCreateCommandHandler {

	private OrderDomainService orderDomainService;

	private final OrderRepository orderRepository;

	private final CustomerRespository customerRespository;

	private final RestaurantRepository restaurantRepository;

	private final OrderDataMapper orderDataMapper;

	public OrderCreateCommandHandler(OrderDomainService orderDomainService, OrderRepository orderRepository,
			CustomerRespository customerRespository, RestaurantRepository restaurantRepository,
			OrderDataMapper orderDataMapper) {
		this.orderDomainService = orderDomainService;
		this.orderRepository = orderRepository;
		this.customerRespository = customerRespository;
		this.restaurantRepository = restaurantRepository;
		this.orderDataMapper = orderDataMapper;
	}

	@Transactional
	public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
		checkCustomer(createOrderCommand.getCustomerId());
		Restaurant restaurant = checkRestaurant(createOrderCommand);
		Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
		OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant);
		Order orderResult = saveOrder(order);
		log.info("Order is created with id: {}",orderResult.getId().getValue());
		return orderDataMapper.orderToCreateOrderResponse(orderResult);
	}

	private void checkCustomer(@NotNull UUID customerId) {
		Optional<Customer> customer = customerRespository.findCustomer(customerId);
		if (customer.isEmpty()) {
			log.warn("Could not find customer with customer id: {}", customerId);
			throw new OrderDomainException("Could not find customer with customer id: " + customerId);
		}
	}

	private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
		Restaurant restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
		Optional<Restaurant> optionalRestaurant = restaurantRepository.findRestaurationInformation(restaurant);
		if (optionalRestaurant.isEmpty()) {
			log.warn("Could not find restaurant with restaurant id: {}", createOrderCommand.getRestaurantId());
			throw new OrderDomainException(
					"Could not find restaurant with restaurant id: " + createOrderCommand.getRestaurantId());
		}
		return optionalRestaurant.get();
	}

	private Order saveOrder(Order order) {
		Order orderResult = orderRepository.save(order);
		if (orderResult == null) {
			log.error("Could not save order!");
			throw new OrderDomainException("Could not save order!");
		}
		log.info("Order is saved with id: {}", orderResult.getId().getValue());
		return orderResult;
	}

	public CreateOrderResponse orderToCreateOrderResponse(Order order) {
		return CreateOrderResponse.builder()
				.orderTrackingId(order.getTrackingId().getValue())
				.status(order.getOrderStatus())
				.build();
	}
}
