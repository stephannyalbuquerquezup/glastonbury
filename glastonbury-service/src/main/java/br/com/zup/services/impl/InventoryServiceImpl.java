package br.com.zup.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import br.com.zup.repositories.entity.Inventory;
import br.com.zup.repositories.entity.Order;
import br.com.zup.repositories.entity.OrderItem;
import br.com.zup.repositories.event.OrderCreatedEvent;
import br.com.zup.repositories.repository.InventoryRepository;
import br.com.zup.repositories.repository.OrderRepository;
import br.com.zup.services.InventoryService;

@Service
public class InventoryServiceImpl implements InventoryService {

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private KafkaTemplate<String, OrderCreatedEvent> template;

	@Override
	public void verifyInventory(OrderCreatedEvent event) {

		Order order = orderRepository.findById(event.getOrderId()).orElse(null);

		if (verifyInventoryQuantity(order)) {
			for (OrderItem items : order.getItems()) {
				Inventory inventory = inventoryRepository.findById(items.getId()).orElse(null);
				if (inventory != null && inventory.getQuantity() > items.getQuantity()) {
					inventory.setQuantity(inventory.getQuantity() - 1);
					inventoryRepository.save(inventory);
				}
			}
			order.setStatus("Ticket-booked");
			orderRepository.save(order);
			this.template.send("payment-order", event);

		} else {
			this.template.send("soldout-order", event);
		}
	}

	private Boolean verifyInventoryQuantity(Order order) {
		for (OrderItem items : order.getItems()) {
			Inventory inventory = inventoryRepository.findById(items.getId()).orElse(null);
			if (inventory != null && inventory.getQuantity() > items.getQuantity()) {
				continue;
			} else {
				return Boolean.FALSE;
			}
		}
		return (order != null && order.getItems() != null && order.getItems().size() > 0);
	}

}
