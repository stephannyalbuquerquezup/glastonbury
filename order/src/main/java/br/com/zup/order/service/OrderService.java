package br.com.zup.order.service;

import java.util.List;

import br.com.zup.order.controller.request.CreateOrderRequest;
import br.com.zup.order.controller.response.OrderResponse;
import br.com.zup.order.event.OrderCreatedEvent;

public interface OrderService {

	void sendKafka(String topic, OrderCreatedEvent event);

	String save(CreateOrderRequest request);

	List<OrderResponse> findAll();

	void soldOutOrder(OrderCreatedEvent event);

	void paymentOrder(OrderCreatedEvent event);

	void aprovedOrder(OrderCreatedEvent event);
	
}
