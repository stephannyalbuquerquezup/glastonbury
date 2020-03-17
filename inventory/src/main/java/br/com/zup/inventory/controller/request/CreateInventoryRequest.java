package br.com.zup.inventory.controller.request;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.zup.inventory.entity.Inventory;

public class CreateInventoryRequest {

	private String ticketName;

	private BigDecimal amount;

	private Integer quantity;

	public Inventory toEntity() {
		return new Inventory(UUID.randomUUID().toString(), this.ticketName, this.amount, this.quantity);
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
