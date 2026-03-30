package com.ecommerce.inventory_service.mapper;

import com.ecommerce.inventory_service.dto.InventoryRequestDto;
import com.ecommerce.inventory_service.dto.InventoryResponseDto;
import com.ecommerce.inventory_service.model.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    @Mapping(target = "id", ignore = true)
    Inventory toModel(InventoryRequestDto inventoryRequest);

    // @MappingTarget para indicar que el segundo parámetro es el objeto que se va a
    // actualizar con los valores del DTO,
    // en lugar de crear un nuevo objeto user
    // lo recibe por referencia, no por valor, por lo que se actualiza directamente
    // el objeto
    // original sin necesidad de devolverlo
    @Mapping(target = "id", ignore = true)
    void updateFromDto(InventoryRequestDto inventoryRequest, @MappingTarget Inventory inventory);

    // calcula la propiedad instock, ya que Inventory no la tiene
    @Mapping(target = "inStock", expression = "java(inventory.getQuantity() > 0)")
    InventoryResponseDto toResponse(Inventory inventory);
}
