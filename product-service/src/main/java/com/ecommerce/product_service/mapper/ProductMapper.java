package com.ecommerce.product_service.mapper;

import com.ecommerce.product_service.dto.ProductRequestDTO;
import com.ecommerce.product_service.dto.ProductResponseDTO;
import com.ecommerce.product_service.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    Product toProduct(ProductRequestDTO requestDTO);

    ProductResponseDTO toProductResponseDTO(Product product);

    // @MappingTarget para indicar que el segundo parámetro es el objeto que se va a actualizar con los valores del DTO,
    // en lugar de crear un nuevo objeto user
    // lo recibe por referencia, no por valor, por lo que se actualiza directamente el objeto
    // original sin necesidad de devolverlo
    @Mapping(target = "id", ignore = true)
    void updateProductFromRequest(ProductRequestDTO productRequest, @MappingTarget Product product);
}