package br.com.zup.services;

import br.com.zup.repositories.event.OrderCreatedEvent;

public interface InventoryService {

	void verifyInventory(OrderCreatedEvent order);
}
