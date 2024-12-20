package com.shop.order_service.service;

import com.shop.order_service.dto.OrderLineItemsDto;
import com.shop.order_service.dto.OrderRequest;
import com.shop.order_service.model.Order;
import com.shop.order_service.model.OrderLineItems;
import com.shop.order_service.repository.OrderRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;

  public void placeOrder(OrderRequest orderRequest) {
    Order order = new Order();
    order.setOrderNumber(UUID.randomUUID().toString());

    List<OrderLineItems> orderLineItems =
        orderRequest.getOrderLineItemsDtoList().stream().map(this::mapToDto).toList();

    order.setOrderLineItemsList(orderLineItems);

    orderRepository.save(order);
  }

  private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
    OrderLineItems orderLineItems = new OrderLineItems();
    orderLineItems.setPrice(orderLineItemsDto.getPrice());
    orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
    orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());

    return orderLineItems;
  }
}
