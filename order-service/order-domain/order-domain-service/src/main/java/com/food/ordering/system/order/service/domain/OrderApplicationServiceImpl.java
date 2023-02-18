package com.food.ordering.system.order.service.domain;

import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderQuery;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Service
class OrderApplicationServiceImpl implements OrderApplicationService {
	
	private final OrderCreateCommandHandler orderCreateCommandHandler;
	
	private final OrderTrackCommandHandler orderTrackCommandHandler;

	public OrderApplicationServiceImpl(OrderCreateCommandHandler orderCreateCommandHandler,
			OrderTrackCommandHandler orderTrackCommandHandler) {
		this.orderCreateCommandHandler = orderCreateCommandHandler;
		this.orderTrackCommandHandler = orderTrackCommandHandler;
	}

	@Override
	public CreateOrderResponse cerateOrder(@Valid CreateOrderCommand createOrderCommand) {
		return orderCreateCommandHandler.createOrder(createOrderCommand);
	}

	@Override
	public TrackOrderResponse trackOrder(@Valid TrackOrderQuery trackOrderQuery) {
		return orderTrackCommandHandler.trackOrder(trackOrderQuery);
	}
}