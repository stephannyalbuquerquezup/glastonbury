package br.com.zup.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import br.com.zup.repositories.entity.Order;
import br.com.zup.repositories.event.OrderCreatedEvent;
import br.com.zup.repositories.repository.OrderRepository;
import br.com.zup.repositories.request.CreateOrderRequest;
import br.com.zup.repositories.response.OrderResponse;
import br.com.zup.services.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private KafkaTemplate<String, OrderCreatedEvent> template;

	@Override
	public String save(CreateOrderRequest request) {
		String orderId = this.orderRepository.save(request.toEntity()).getId();

		OrderCreatedEvent event = new OrderCreatedEvent(orderId, request.getCustomerId(), request.getAmount(),
				createItemMap(request));

		this.template.send("verify-inventory", event);

		return orderId;
	}

	private Map<String, Integer> createItemMap(CreateOrderRequest request) {
		Map<String, Integer> result = new HashMap<>();
		for (CreateOrderRequest.OrderItemPart item : request.getItems()) {
			result.put(item.getId(), item.getQuantity());
		}

		return result;
	}

	@Override
	public List<OrderResponse> findAll() {
		return this.orderRepository.findAll().stream().map(OrderResponse::fromEntity).collect(Collectors.toList());
	}

	@Override
	public void soldoutOrder(OrderCreatedEvent event) {
		if (event != null) {
			Order order = orderRepository.findById(event.getOrderId()).orElse(null);
			if (order != null) {
				order.setStatus("Sold-Out");
				orderRepository.save(order);
				this.template.send("soldout-email-order", event);
			}
		}
	}

}
