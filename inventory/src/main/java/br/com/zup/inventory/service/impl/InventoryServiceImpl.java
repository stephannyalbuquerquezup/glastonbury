package br.com.zup.inventory.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import br.com.zup.inventory.controller.request.CreateInventoryRequest;
import br.com.zup.inventory.controller.response.InventoryResponse;
import br.com.zup.inventory.event.OrderCreatedEvent;
import br.com.zup.inventory.repository.InventoryRepository;
import br.com.zup.inventory.service.InventoryService;

@Service
public class InventoryServiceImpl implements InventoryService {

	@Autowired
	private InventoryRepository inventoryRepository;

    private KafkaTemplate<String, OrderCreatedEvent> template;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository, KafkaTemplate<String, OrderCreatedEvent> template) {
        this.inventoryRepository = inventoryRepository;
        this.template = template;
    }

	@Override
	public String save(CreateInventoryRequest request) {
		String inventoryId = this.inventoryRepository.save(request.toEntity()).getId();

		return inventoryId;
	}

	@Override
	public List<InventoryResponse> findAll() {
		return this.inventoryRepository.findAll().stream().map(InventoryResponse::fromEntity)
				.collect(Collectors.toList());
	}

	@Override
	public void verifyInventory(OrderCreatedEvent event) {

		// Todos os itens do order existam e tenham wuantidade disponível
		if (event.getItems().keySet().stream()
				.allMatch(id -> id != null && findAll().stream().anyMatch(item -> item.getId() != null
						&& id.equals(item.getId()) && item.getQuantity() > 0)) == Boolean.TRUE) {
			System.out.println("Todos tickets no inventário");
			this.template.send("payment-orders", event);
		} else {
			System.out.println("Ticket não encontrado");
			this.template.send("payment-orders", event);
//			this.template.send("sold-out-orders", event);
		}

	}

}
