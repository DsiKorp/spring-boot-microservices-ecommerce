package com.ecommerce.inventory_service.repository;

import com.ecommerce.inventory_service.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository  extends JpaRepository<Inventory, Long> {
    // Query method para buscar un usuario por su nombre de usuario, devuelve la
    // primera coincidencia
    // o null si no se encuentra ningún usuario con ese nombre de usuario
    // Spring Data JPA genera automáticamente la implementación de este método a
    // partir del nombre del método, siguiendo la convención de nomenclatura
    Optional<Inventory> findBySku(String sku);
    boolean existsBySku(String sku);
}