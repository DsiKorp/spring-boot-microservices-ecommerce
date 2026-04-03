package com.ecommerce.inventory_service.controller;

import com.ecommerce.inventory_service.dto.InventoryRequestDto;
import com.ecommerce.inventory_service.dto.InventoryResponseDto;
import com.ecommerce.inventory_service.service.InventoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory") // Igualamos el versionado v1
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    // --- Endpoint Especial (Lógica de Negocio) ---
    @GetMapping("/{sku}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@PathVariable String sku, @RequestParam("quantity") Integer quantity) {
        return inventoryService.isInStock(sku, quantity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponseDto createInventory(@RequestBody @Valid InventoryRequestDto inventoryRequestDto) {
        return inventoryService.createInventory(inventoryRequestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponseDto> getAllInventory(HttpServletRequest request) {

        log.info("Request processed from port : {}", request.getServerPort());
        return inventoryService.getAllInventory();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InventoryResponseDto updateInventory(@PathVariable Long id,
                                             @RequestBody @Valid InventoryRequestDto inventoryRequestDto) {
        return inventoryService.updateInventory(id, inventoryRequestDto);
    }


    @PutMapping("/reduce/{sku}")
    @ResponseStatus(HttpStatus.OK)
    public String reduceStock(@PathVariable String sku, @RequestParam Integer quantity) {
//        try {
//            int timeout = 5000;
//            log.info("Inventory sleep for: {} seconds", timeout);
//            Thread.sleep(timeout);
//        } catch (Exception e) {
//            throw new RuntimeException("Error reducing stock: " + e.getMessage());
//        }

        inventoryService.reduceStock(sku, quantity);
        return "Stock reduced successfully";
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
    }
}
