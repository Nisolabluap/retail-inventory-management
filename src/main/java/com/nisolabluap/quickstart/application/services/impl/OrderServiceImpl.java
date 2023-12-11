package com.nisolabluap.quickstart.application.services.impl;

import com.nisolabluap.quickstart.application.exceptions.customer.CustomerNotFoundException;
import com.nisolabluap.quickstart.application.exceptions.item.ItemNotFoundException;
import com.nisolabluap.quickstart.application.models.dtos.OrderDTO;
import com.nisolabluap.quickstart.application.models.dtos.OrderItemDTO;
import com.nisolabluap.quickstart.application.models.entities.Customer;
import com.nisolabluap.quickstart.application.models.entities.Item;
import com.nisolabluap.quickstart.application.models.entities.Order;
import com.nisolabluap.quickstart.application.models.entities.OrderItem;
import com.nisolabluap.quickstart.application.models.enums.OrderStatus;
import com.nisolabluap.quickstart.application.repositories.CustomerRepository;
import com.nisolabluap.quickstart.application.repositories.ItemRepository;
import com.nisolabluap.quickstart.application.repositories.OrderRepository;
import com.nisolabluap.quickstart.application.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    @Override
    public OrderDTO createOrder(OrderDTO orderDTO, Long customerId, List<Long> itemIds, List<Long> itemQuantities) {
        Map<Long, Long> itemIdQuantityMap = getLongLongMap(itemIds, itemQuantities);

        if (!isValidQuantities(new ArrayList<>(itemIdQuantityMap.values()), itemIdQuantityMap.size())) {
            throw new ItemNotFoundException("Invalid quantities. Each quantity should be greater than 0 and not exceed the number of itemIds.");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found!"));

        List<Item> items = itemRepository.findAllById(new ArrayList<>(itemIdQuantityMap.keySet()));

        if (items.isEmpty()) {
            throw new ItemNotFoundException("Items not found!");
        }

        for (Item item : items) {
            Long quantity = itemIdQuantityMap.get(item.getId());
            if (quantity > item.getAvailableQuantity()) {
                throw new ItemNotFoundException("Insufficient stock for one or more items in the order.");
            }
        }

        double totalPrice = items.stream()
                .mapToDouble(item -> item.getPrice() * itemIdQuantityMap.get(item.getId()))
                .sum();

        Order order = new Order();
        order.setCustomerId(customer);
        order.setOrderStatus(OrderStatus.PAYED);
        order.setOrderItems(new HashSet<>());
        order.setTotalQuantity(itemIdQuantityMap.values().stream().mapToInt(Long::intValue).sum());
        order.setTotalPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (Item item : items) {
            Long quantity = itemIdQuantityMap.get(item.getId());
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setQuantityPerItem(quantity.intValue());
            orderItem.setPrice(item.getPrice() * quantity);
            orderItem.setOrder(savedOrder);

            orderItems.add(orderItem);
            updateStock(item, quantity);
        }

        savedOrder.getOrderItems().addAll(orderItems);
        Order updatedOrder = orderRepository.save(savedOrder);

        return mapOrderToDTO(updatedOrder, orderItems);
    }

    @Transactional
    @Override
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ItemNotFoundException("Order not found!"));

        OrderStatus currentStatus = order.getOrderStatus();

        if (currentStatus == OrderStatus.REFUNDED) {
            throw new ItemNotFoundException("Order is already refunded and locked.");
        }

        order.setOrderStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        if (newStatus == OrderStatus.REFUNDED) {
            handleRefundLogic(updatedOrder);
        }

        return mapOrderToDTO(updatedOrder);
    }

    @Override
    public OrderDTO findOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ItemNotFoundException("Order not found!"));

        return mapOrderToDTO(order);
    }

    private Map<Long, Long> getLongLongMap(List<Long> itemIds, List<Long> itemQuantities) {
        if (itemIds.size() != itemQuantities.size()) {
            throw new ItemNotFoundException("itemIds and itemQuantities lists must have the same size.");
        }

        Map<Long, Long> itemIdQuantityMap = new HashMap<>();
        for (int i = 0; i < itemIds.size(); i++) {
            Long itemId = itemIds.get(i);
            Long quantity = itemQuantities.get(i);
            itemIdQuantityMap.put(itemId, itemIdQuantityMap.getOrDefault(itemId, 0L) + quantity);
        }
        return itemIdQuantityMap;
    }

    private void updateStock(Item item, Long quantity) {
        long updatedStock = item.getAvailableQuantity() - quantity.intValue();
        item.setAvailableQuantity(updatedStock);
        itemRepository.save(item);
    }

    private void handleRefundLogic(Order order) {
        List<OrderItem> orderItems = new ArrayList<>(order.getOrderItems());

        for (OrderItem orderItem : orderItems) {
            Item item = orderItem.getItem();
            int refundedQuantity = orderItem.getQuantityPerItem();

            // Update the stock by adding the refunded quantity
            long updatedStock = item.getAvailableQuantity() + refundedQuantity;
            item.setAvailableQuantity(updatedStock);
            itemRepository.save(item);
        }

        // Lock the order to prevent further changes
        order.setOrderStatus(OrderStatus.REFUNDED);
        orderRepository.save(order);
    }

    private OrderDTO mapOrderToDTO(Order order) {
        List<OrderItem> orderItems = new ArrayList<>(order.getOrderItems());
        return mapOrderToDTO(order, orderItems);
    }

    private OrderDTO mapOrderToDTO(Order order, List<OrderItem> orderItems) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setCustomerId(order.getCustomerId().getId());
        orderDTO.setOrderStatus(order.getOrderStatus());
        orderDTO.setCreatedAt(order.getCreatedAt());
        orderDTO.setTotalQuantity(order.getTotalQuantity());
        orderDTO.setTotalPrice(order.getTotalPrice());

        List<OrderItemDTO> orderItemDTOs = orderItems.stream()
                .map(this::mapOrderItemToDTO)
                .collect(Collectors.toList());

        orderDTO.setItems(orderItemDTOs);

        return orderDTO;
    }

    private OrderItemDTO mapOrderItemToDTO(OrderItem orderItem) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setItemId(orderItem.getItem().getId());
        orderItemDTO.setItemName(orderItem.getItem().getName());
        orderItemDTO.setQuantityPerItem(orderItem.getQuantityPerItem());
        orderItemDTO.setPrice(orderItem.getPrice());
        return orderItemDTO;
    }

    private boolean isValidQuantities(List<Long> quantities, int expectedSize) {
        return quantities.size() == expectedSize && quantities.stream().allMatch(qty -> qty > 0);
    }
}
