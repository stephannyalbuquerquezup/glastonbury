package br.com.zup.order.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import br.com.zup.inventory.entity.Inventory;
import br.com.zup.inventory.repository.InventoryRepository;
import br.com.zup.order.controller.request.CreateOrderRequest;
import br.com.zup.order.controller.response.OrderResponse;
import br.com.zup.order.entity.Order;
import br.com.zup.order.event.OrderCreatedEvent;
import br.com.zup.order.repository.OrderRepository;
import br.com.zup.order.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	private OrderRepository orderRepository;
//	@Autowired
	private InventoryRepository inventoryRepository;

	private KafkaTemplate<String, OrderCreatedEvent> template;

	@Autowired
	public OrderServiceImpl(OrderRepository orderRepository
			, InventoryRepository inventoryRepository
			,  KafkaTemplate<String, OrderCreatedEvent> template) {
		this.orderRepository = orderRepository;
		this.inventoryRepository = inventoryRepository;
		this.template = template;
	}

	@Override
	public String save(CreateOrderRequest request) {
		String orderId = this.orderRepository.save(request.toEntity()).getId();

		OrderCreatedEvent event = new OrderCreatedEvent(orderId, request.getCustomerId(), request.getAmount(),
				createItemMap(request));

		sendKafka("created-orders", event);

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
	public void sendKafka(String topic, OrderCreatedEvent event) {
		this.template.send(topic, event);
	}

	@Override
	public void soldOutOrder(OrderCreatedEvent event) {
		if (event != null) {
			Order order = orderRepository.findById(event.getOrderId()).orElse(null);
			if (order != null) {
				order.setStatus("Sold-out");
				orderRepository.save(order);
			}
		}
	}

	@Override
	public void paymentOrder(OrderCreatedEvent event) {
		if (event != null) {
			Order order = orderRepository.findById(event.getOrderId()).orElse(null);
			if (order != null) {
				for (String inventoryId : event.getItems().keySet()) {
					Inventory inventory = inventoryRepository.findById(inventoryId).orElse(null);
					if (inventory != null) {
						inventory.setQuantity(inventory.getQuantity() - 1);
						inventoryRepository.save(inventory);
						order.setAmount(order.getAmount().subtract(inventory.getAmount()));
						orderRepository.save(order);
					}
				}
				this.template.send("aproved-order", event);
			}
		}
	}
	
	@Override
	public void aprovedOrder(OrderCreatedEvent event) {
		if (event != null) {
			Order order = orderRepository.findById(event.getOrderId()).orElse(null);
			if (order != null) {
				order.setStatus("Aproved");
				orderRepository.save(order);
			}
		}
	}


}
