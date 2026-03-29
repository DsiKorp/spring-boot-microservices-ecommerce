package com.ecommerce.product_service.service.impl;

import com.ecommerce.product_service.dto.ProductRequestDTO;
import com.ecommerce.product_service.dto.ProductResponseDTO;
import com.ecommerce.product_service.exception.ResourceNotFoundException;
import com.ecommerce.product_service.mapper.ProductMapper;
import com.ecommerce.product_service.model.Product;
import com.ecommerce.product_service.repository.ProductRepository;
import com.ecommerce.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO requestDTO) {

        Product product = mapper.toProduct(requestDTO);

        Product savedProduct = productRepository.save(product);
        log.info("Product {} saved", savedProduct.getName());

        return mapper.toProductResponseDTO(savedProduct);
    }

    @Override
    public List<ProductResponseDTO> getAllsProducts() {
        return productRepository.findAll()
                .stream()
                .map(mapper::toProductResponseDTO)
                .toList();
    }

    @Override
    public ProductResponseDTO getProductById(String id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Producto", "id",  id)
        );
        return mapper.toProductResponseDTO(product);
    }

    @Override
    public ProductResponseDTO updateProduct(String id, ProductRequestDTO productRequest) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Producto", "id",  id)
        );
        mapper.updateProductFromRequest(productRequest, product);

        Product updatedProduct = productRepository.save(product);
        log.info("Product {} updated", updatedProduct.getName());

        return mapper.toProductResponseDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(String id) {

        if(!productRepository.existsById(id)){
            throw new ResourceNotFoundException("Producto", "id",  id);
        }

        productRepository.deleteById(id);
        log.info("Product with id:{} deleted", id);
    }
}
