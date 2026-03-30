package com.ecommerce.inventory_service.service;

import com.ecommerce.inventory_service.dto.InventoryRequestDto;
import com.ecommerce.inventory_service.dto.InventoryResponseDto;

import java.util.List;

public interface InventoryService {
    boolean isInStock(String sku, Integer quantity);
    InventoryResponseDto createInventory(InventoryRequestDto inventoryRequest);
    List<InventoryResponseDto> getAllInventory();
    InventoryResponseDto getInventoryById(Long id);
    InventoryResponseDto getInventoryBySku(String sku);
    InventoryResponseDto updateInventory(Long id, InventoryRequestDto inventoryRequest);
    void deleteInventory(Long id);
}
