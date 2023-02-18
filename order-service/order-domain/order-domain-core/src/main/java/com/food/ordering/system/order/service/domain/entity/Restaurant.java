package com.food.ordering.system.order.service.domain.entity;

import java.util.Collections;
import java.util.List;

import com.food.ordering.system.domain.entity.AggregateRoot;
import com.food.ordering.system.domain.valueobjects.RestaurantId;

public class Restaurant extends AggregateRoot<RestaurantId> {

	private final List<Product> products;

	private boolean active;

	private Restaurant(Builder builder) {
		super.setId(builder.restaurantId);
		this.products = builder.products;
		this.active = builder.active;
	}

	public static Builder builder() {
		return new Builder();
	}

	public List<Product> getProducts() {
		return products;
	}

	public boolean isActive() {
		return active;
	}

	public static final class Builder {
		private RestaurantId restaurantId;
		private List<Product> products = Collections.emptyList();
		private boolean active;

		private Builder() {
		}

		public Builder withRestaurantId(RestaurantId restaurantId) {
			this.restaurantId = restaurantId;
			return this;
		}

		public Builder withProducts(List<Product> products) {
			this.products = products;
			return this;
		}

		public Builder withActive(boolean active) {
			this.active = active;
			return this;
		}

		public Restaurant build() {
			return new Restaurant(this);
		}
	}
}
