package br.com.zup.inventory.controller.response;

import java.math.BigDecimal;

import br.com.zup.inventory.entity.Inventory;

public class InventoryResponse {

	private String id;

	private String ticketName;

	private BigDecimal amount;

	private Integer quantity;

	public InventoryResponse() {
	}

	public InventoryResponse(String id, String ticketName, BigDecimal amount, Integer quantity) {
		this.id = id;
		this.ticketName = ticketName;
		this.amount = amount;
		this.quantity = quantity;
	}

	public static InventoryResponse fromEntity(Inventory inventory) {
		return new InventoryResponse(inventory.getId(), inventory.getTicketName(), inventory.getAmount(),
				inventory.getQuantity());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTicketName() {
		return ticketName;
	}

	public void setTicketName(String ticketName) {
		this.ticketName = ticketName;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
