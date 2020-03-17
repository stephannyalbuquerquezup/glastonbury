package br.com.zup.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.zup.inventory.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, String> {
}
