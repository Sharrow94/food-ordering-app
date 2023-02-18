package com.food.ordering.system.domain.valueobjects;

import java.util.Objects;

public abstract class BaseId<T> {

	private final T value;

	protected BaseId(T value) {
		this.value = value;
	}

	public T getValue() {
		return this.value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		BaseId<?> other = (BaseId<?>) obj;
		return value.equals(other.value);
	}
}
