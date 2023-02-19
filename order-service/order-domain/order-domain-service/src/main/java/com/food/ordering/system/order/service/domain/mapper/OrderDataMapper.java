package com.food.ordering.system.order.service.domain.mapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

import com.food.ordering.system.domain.valueobjects.CustomerId;
import com.food.ordering.system.domain.valueobjects.Money;
import com.food.ordering.system.domain.valueobjects.ProductId;
import com.food.ordering.system.domain.valueobjects.RestaurantId;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;

@Component
public class OrderDataMapper {

	public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
		return Restaurant.builder()
				.withRestaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
				.withProducts(createOrderCommand.getItems().stream()
						.map(orderItem -> new Product(new ProductId(orderItem.getProductId()))).collect(Collectors.toList()))
				.build();
	}
	
	public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
		return Order.builder()
				.withCustomerId(new CustomerId(createOrderCommand.getCustomerId()))
				.withRestaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
				.withDeliveryAddress(orderAddressToStreetAddress(createOrderCommand.getAddress()))
				.withPrice(new Money(createOrderCommand.getPrice()))
				.withItems(orderItemsToOrderItemEntities(createOrderCommand.getItems()))
				.build();
	}
	
	public CreateOrderResponse orderToCreateOrderResponse(Order order,String message) {
        return CreateOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .status(order.getOrderStatus())
                .message(message)
                .build();
    }
	
	public TrackOrderResponse orderToTrackOrderResponse(Order order) {
		return TrackOrderResponse.builder()
				.orderTrackingId(order.getTrackingId().getValue())
				.orderStatus(order.getOrderStatus())
				.failureMessages(order.getFailureMessages())
				.build();
	}

	private StreetAddress orderAddressToStreetAddress(@NotNull OrderAddress address) {
		return new StreetAddress(
				UUID.randomUUID(),
				address.getStreet(), 
				address.getPostalCode(), 
				address.getCity());
	}
	
	private List<OrderItem> orderItemsToOrderItemEntities(
			@NotNull List<com.food.ordering.system.order.service.domain.dto.create.OrderItem> orderItems) {
		return orderItems.stream().map(orderItem->{
			return OrderItem.builder()
			.withProduct(new Product(new ProductId(orderItem.getProductId())))
			.withPrice(new Money(orderItem.getPrice()))
			.withQuantity(orderItem.getQuantity())
			.withSubtotal(new Money(orderItem.getSubTotal()))
			.build();
		}).collect(Collectors.toList());
	}
}
