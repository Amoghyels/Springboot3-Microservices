package com.dev.microservices.product_service.service;

import com.dev.microservices.product_service.dto.ProductRequest;
import com.dev.microservices.product_service.dto.ProductResponse;
import com.dev.microservices.product_service.model.Product;
import com.dev.microservices.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest){
        Product product = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .skuCode(productRequest.skuCode())
                .price(productRequest.price())
                .build();
        productRepository.save(product);
        log.info("Product created successfulyy");
        return  new ProductResponse(product.getId(),product.getSkuCode(), product.getName(), product.getDescription(), product.getPrice());
    }

    public List<ProductResponse> getAllProduct(){
        return productRepository.findAll()
        .stream()
                .map(product -> new ProductResponse(product.getId(),product.getSkuCode(), product.getName(), product.getDescription(), product.getPrice()))
                .toList();
    }

}
