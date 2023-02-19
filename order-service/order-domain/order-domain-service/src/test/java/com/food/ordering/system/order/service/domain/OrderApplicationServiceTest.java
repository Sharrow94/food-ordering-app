package com.food.ordering.system.order.service.domain;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.food.ordering.system.domain.valueobjects.CustomerId;
import com.food.ordering.system.domain.valueobjects.Money;
import com.food.ordering.system.domain.valueobjects.OrderId;
import com.food.ordering.system.domain.valueobjects.OrderStatus;
import com.food.ordering.system.domain.valueobjects.ProductId;
import com.food.ordering.system.domain.valueobjects.RestaurantId;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.create.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRespository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

	@Autowired
	private OrderApplicationService orderApplicationService;

	@Autowired
	private OrderDataMapper orderDataMapper;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CustomerRespository customerRespository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	private CreateOrderCommand createOrderCommand;
	private CreateOrderCommand createOrderCommandWrongPrice;
	private CreateOrderCommand createOrderCommandWrongProductPrice;
	private final UUID CUSTOMER_ID = UUID.fromString("0e7f408e-afe1-11ed-afa1-0242ac120002");
	private final UUID RESTAURANT_ID = UUID.fromString("0e7f4368-afe1-11ed-afa1-0242ac120002");
	private final UUID PRODUCT_ID = UUID.fromString("0e7f4502-afe1-11ed-afa1-0242ac120002");
	private final UUID ORDER_ID = UUID.fromString("0e7f4692-afe1-11ed-afa1-0242ac120002");
	private final BigDecimal PRICE = new BigDecimal("200.00");

	@BeforeEach
	public void init() {
		createOrderCommand = CreateOrderCommand.builder()
				.customerId(CUSTOMER_ID)
				.restaurantId(RESTAURANT_ID)
				.address(OrderAddress.builder()
						.street("street_1")
						.postalCode("1000AB")
						.city("Paris")
						.build())
				.price(PRICE)
				.items(List.of(
						OrderItem.builder()
						.productId(PRODUCT_ID)
						.quantity(1)
						.price(new BigDecimal("50.00"))
						.subTotal(new BigDecimal("50.00"))
						.build(),
						OrderItem.builder()
						.productId(PRODUCT_ID)
						.quantity(3)
						.price(new BigDecimal("50.00"))
						.subTotal(new BigDecimal("150.00"))
						.build()))
				.build();
		
		createOrderCommandWrongPrice = CreateOrderCommand.builder()
				.customerId(CUSTOMER_ID)
				.restaurantId(RESTAURANT_ID)
				.address(OrderAddress.builder()
						.street("street_1")
						.postalCode("1000AB")
						.city("Paris")
						.build())
				.price(new BigDecimal("250.00"))
				.items(List.of(
						OrderItem.builder()
						.productId(PRODUCT_ID)
						.quantity(1)
						.price(new BigDecimal("50.00"))
						.subTotal(new BigDecimal("50.00"))
						.build(),
						OrderItem.builder()
						.productId(PRODUCT_ID)
						.quantity(3)
						.price(new BigDecimal("50.00"))
						.subTotal(new BigDecimal("150.00"))
						.build()))
				.build();
		
		createOrderCommandWrongProductPrice = CreateOrderCommand.builder()
				.customerId(CUSTOMER_ID)
				.restaurantId(RESTAURANT_ID)
				.address(OrderAddress.builder()
						.street("street_1")
						.postalCode("1000AB")
						.city("Paris")
						.build())
				.price(new BigDecimal("210.00"))
				.items(List.of(
						OrderItem.builder()
						.productId(PRODUCT_ID)
						.quantity(1)
						.price(new BigDecimal("60.00"))
						.subTotal(new BigDecimal("60.00"))
						.build(),
						OrderItem.builder()
						.productId(PRODUCT_ID)
						.quantity(3)
						.price(new BigDecimal("50.00"))
						.subTotal(new BigDecimal("150.00"))
						.build()))
				.build();
		
		Customer customer=new Customer();
		customer.setId(new CustomerId(CUSTOMER_ID));
		
		Restaurant restaurantResponse=Restaurant.builder()
				.withRestaurantId(new RestaurantId(RESTAURANT_ID))
				.withProducts(List.of(
						new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new  BigDecimal("50.00"))),
						new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new  BigDecimal("50.00")))))
				.withActive(true)
				.build();
		
		Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
		order.setId(new OrderId(ORDER_ID));

		when(customerRespository.findCustomer(CUSTOMER_ID)).thenReturn(of(customer));
		when(restaurantRepository
				.findRestaurationInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
				.thenReturn(of(restaurantResponse));
		when(orderRepository.save(Mockito.any(Order.class))).thenReturn(order);
	}
	
	@Test
	public void test_createOrder() {
		CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
		assertEquals(createOrderResponse.getStatus(), OrderStatus.PENDING);
		assertEquals(createOrderResponse.getMessage(), "Order created Sucessfully");
		assertNotNull(createOrderResponse.getOrderTrackingId());
	}
	
	@Test
	public void test_createOrderWithWrongTotalPrice() {
		OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
				() -> orderApplicationService.createOrder(createOrderCommandWrongPrice));
		assertEquals("Total price: 250.00 is not equal to Order items total: 200.00!",orderDomainException.getMessage());
	}
	
	@Test
	public void test_createOrderWithWrongProductPrice() {
		OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
				() -> orderApplicationService.createOrder(createOrderCommandWrongProductPrice));
		assertEquals("Order item price: 60.00 is not valid for product "+PRODUCT_ID,orderDomainException.getMessage());
	}
	
	@Test
	public void test_createOrderWithPassiveRestaurant() {
		Restaurant restaurantInactive = Restaurant.builder()
				.withRestaurantId(new RestaurantId(RESTAURANT_ID))
				.withProducts(List.of(
						new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
						new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))))
				.withActive(false)
				.build();
		when(restaurantRepository
				.findRestaurationInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
				.thenReturn(Optional.of(restaurantInactive));
		OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
				() -> orderApplicationService.createOrder(createOrderCommand));
		assertEquals("Restaurant with id "+restaurantInactive.getId().getValue()+" is currently not active!",orderDomainException.getMessage());
	}
}
