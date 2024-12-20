package com.shop.product_service.service;

import com.shop.product_service.dto.ProductRequest;
import com.shop.product_service.model.Product;
import com.shop.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

  private final ProductRepository productRepository;

  public void createProduct(ProductRequest productRequest) {
    Product product =
        Product.builder()
            .name(productRequest.getName())
            .description(productRequest.getDescription())
            .price(productRequest.getPrice())
            .build();

    productRepository.save(product);
    log.info("Prodcut {} is saved", product.getId());
  }
}
