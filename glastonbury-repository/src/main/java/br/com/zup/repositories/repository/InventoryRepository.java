package br.com.zup.repositories.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.zup.repositories.entity.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {
}