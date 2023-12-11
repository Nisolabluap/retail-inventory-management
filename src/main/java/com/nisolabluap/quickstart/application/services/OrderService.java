package com.nisolabluap.quickstart.application.services;

import com.nisolabluap.quickstart.application.models.dtos.OrderDTO;
import com.nisolabluap.quickstart.application.models.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderDTO createOrder(OrderDTO orderDTO, Long customerId, List<Long> itemIds, List<Long> itemQuantities);

    OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus);

    OrderDTO findOrderById(Long orderId);
}
