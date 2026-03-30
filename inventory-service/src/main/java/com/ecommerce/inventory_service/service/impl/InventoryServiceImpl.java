package com.ecommerce.inventory_service.service.impl;

import com.ecommerce.inventory_service.dto.InventoryRequestDto;
import com.ecommerce.inventory_service.dto.InventoryResponseDto;
import com.ecommerce.inventory_service.exception.ResourceNotFoundException;
import com.ecommerce.inventory_service.mapper.InventoryMapper;
import com.ecommerce.inventory_service.model.Inventory;
import com.ecommerce.inventory_service.repository.InventoryRepository;
import com.ecommerce.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional(readOnly = true)
    public boolean isInStock(String sku, Integer quantity) {
        return inventoryRepository.findBySku(sku)
                .map(inventory -> inventory.getQuantity() >= quantity)
                .orElse(false);
    }

    @Override
    @Transactional
    public InventoryResponseDto createInventory(InventoryRequestDto inventoryRequestDto) {
        // Validamos duplicados (Opcional, pero recomendado)
        boolean exists = inventoryRepository.existsBySku(inventoryRequestDto.sku());
        if (exists) {
            throw new RuntimeException("The inventory for the SKU " + inventoryRequestDto.sku() + " already exists");
        }

        Inventory inventory = inventoryMapper.toModel(inventoryRequestDto);
        Inventory savedInventory = inventoryRepository.save(inventory);
        log.info("Inventory created for the SKU: {}", savedInventory.getSku());

        // Mapeamos el objeto que vino de la DB (con ID generado)
        return inventoryMapper.toResponse(savedInventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDto> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponseDto getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Inventory", "id", id)
        );

        return inventoryMapper.toResponse(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponseDto getInventoryBySku(String sku) {
        Inventory inventory = inventoryRepository.findBySku(sku).orElseThrow(
                () -> new ResourceNotFoundException("Inventory", "sku", sku)
        );

        return inventoryMapper.toResponse(inventory);
    }

    @Override
    @Transactional
    public InventoryResponseDto updateInventory(Long id, InventoryRequestDto inventoryRequest) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));

        inventoryMapper.updateFromDto(inventoryRequest, inventory);

        Inventory updatedInventory = inventoryRepository.save(inventory);

        log.info("Updated inventory for ID: {}", id);

        return inventoryMapper.toResponse(updatedInventory);
    }

    @Override
    @Transactional
    public void deleteInventory(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory", "id", id);
        }
        inventoryRepository.deleteById(id);
        log.info("Inventory deleted with ID: {}", id);
    }
}
