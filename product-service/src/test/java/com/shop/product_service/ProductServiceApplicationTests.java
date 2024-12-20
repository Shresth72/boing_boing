package com.shop.product_service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.product_service.dto.ProductRequest;
import com.shop.product_service.dto.ProductResponse;
import com.shop.product_service.model.Product;
import com.shop.product_service.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

  @Container static MongoDBContainer mongoDbContainer = new MongoDBContainer("mongo:6.0");

  @Autowired private MongoTemplate mongoTemplate;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ProductRepository productRepository;

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDbContainer::getReplicaSetUrl);
  }

  @BeforeEach
  void startContainer() {
    mongoDbContainer.start();
  }

  @AfterEach
  void cleanUp() {
    mongoTemplate.dropCollection(Product.class);
    // assert productRepository.findAll().isEmpty();
  }

  @Test
  void shouldCreateProduct() throws Exception {
    ProductRequest productRequest = getProductRequest();
    String productRequestString = objectMapper.writeValueAsString(productRequest);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productRequestString))
        .andExpect(MockMvcResultMatchers.status().isCreated());

    assertEquals(1, productRepository.findAll().size());
  }

  @Test
  void shouldGetProductById() throws Exception {
    ProductRequest productRequest = getProductRequest();
    String productRequestString = objectMapper.writeValueAsString(productRequest);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productRequestString))
        .andExpect(MockMvcResultMatchers.status().isCreated());
    assertEquals(1, productRepository.findAll().size());

    List<ProductResponse> productResponse = getProductResponseList();
    String productResponseString = objectMapper.writeValueAsString(productResponse);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/product"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(productResponseString))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(productRequest.getName()));
  }

  @Test
  void shouldDeleteProduct() throws Exception {
    ProductRequest productRequest = getProductRequest();
    String productRequestString = objectMapper.writeValueAsString(productRequest);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productRequestString))
        .andExpect(MockMvcResultMatchers.status().isCreated());

    assertEquals(1, productRepository.findAll().size());

    String productId = productRepository.findAll().get(0).getId();
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/api/product/" + productId))
        .andExpect(MockMvcResultMatchers.status().isNoContent());

    assertEquals(0, productRepository.findAll().size());
  }

  @Test
  void shouldReturn404WhenDeletingNonExistentProduct() throws Exception {
    String productId = "default";
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/api/product/" + productId))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  private ProductRequest getProductRequest() {
    return ProductRequest.builder()
        .name("IPhone 13")
        .description("IPhone phone")
        .price(BigDecimal.valueOf(1200))
        .build();
  }

  public List<ProductResponse> getProductResponseList() {
    List<Product> products = productRepository.findAll();

    return products.stream().map(this::mapToProductResponse).toList();
  }

  private ProductResponse mapToProductResponse(Product product) {
    return ProductResponse.builder()
        .id(product.getId())
        .name(product.getName())
        .description(product.getDescription())
        .price(product.getPrice())
        .build();
  }
}
