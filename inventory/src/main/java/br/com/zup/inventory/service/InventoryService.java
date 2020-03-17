package br.com.zup.inventory.service;

import java.util.List;

import br.com.zup.inventory.controller.request.CreateInventoryRequest;
import br.com.zup.inventory.controller.response.InventoryResponse;
import br.com.zup.inventory.event.OrderCreatedEvent;

public interface InventoryService {

	String save(CreateInventoryRequest request);

	List<InventoryResponse> findAll();

	void verifyInventory(OrderCreatedEvent event);

}
