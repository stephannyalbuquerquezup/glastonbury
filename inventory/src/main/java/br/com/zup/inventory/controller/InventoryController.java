package br.com.zup.inventory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.zup.inventory.controller.request.CreateInventoryRequest;
import br.com.zup.inventory.controller.response.InventoryResponse;
import br.com.zup.inventory.service.InventoryService;

@RestController
@RequestMapping("/inventories")
public class InventoryController {

	private InventoryService inventoryService;

	@Autowired
	public InventoryController(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public String create(@RequestBody CreateInventoryRequest request) {
		return this.inventoryService.save(request);
	}

	@ResponseStatus(HttpStatus.CREATED)
	@GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public List<InventoryResponse> getInventories() {
		return this.inventoryService.findAll();
	}
}
