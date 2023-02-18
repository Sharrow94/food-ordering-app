package com.food.ordering.system.domain.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Money {
	
	public static final Money ZERO=new Money(BigDecimal.ZERO);
	
	private final BigDecimal amount;

	public Money(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmount() {
		return amount;
	}
	
	public boolean isGreatherThanZero() {
		return this.amount!=null && this.amount.compareTo(BigDecimal.ZERO)>0;
	}
	
	public boolean isGreatherThan(Money money) {
		return this.amount!=null && this.amount.compareTo(money.getAmount())>0;
	}
	
	public Money add(Money money) {
		return new Money(setScale(this.amount.add(money.getAmount())));
	}
	
	public Money substract(Money money) {
		return new Money(setScale(this.amount.subtract(money.getAmount())));
	}
	
	public Money multiply(int multipler) {
		return new Money(setScale(this.amount.multiply(new BigDecimal(multipler))));
	}
	
	private BigDecimal setScale(BigDecimal input) {
		return input.setScale(2,RoundingMode.HALF_EVEN);
	}

	@Override
	public int hashCode() {
		return Objects.hash(amount);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Money other = (Money) obj;
		return amount.equals(other.amount);
	}
}
