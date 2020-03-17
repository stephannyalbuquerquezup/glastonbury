package br.com.zup.inventory.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "inventories")
public class Inventory {

	@Id
	private String id;

	private String ticketName;

	private BigDecimal amount;

	private Integer quantity;

	public Inventory() {
	}

	public Inventory(String id, String ticketName, BigDecimal amount, Integer quantity) {
		this.setId(id);
		this.setTicketName(ticketName);
		this.setAmount(amount);
		this.setQuantity(quantity);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getTicketName() {
		return ticketName;
	}

	public void setTicketName(String ticketName) {
		this.ticketName = ticketName;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}
