package com.food.ordering.system.order.service.domain.dto.create;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderAddress {
	@NotNull
	private final String street;
	@NotNull
	private final String postalCode;
	@NotNull
	private final String city;
}
