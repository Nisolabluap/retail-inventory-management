package com.nisolabluap.quickstart.application.services.impl;

import com.nisolabluap.quickstart.application.exceptions.customer.CustomerNotFoundException;
import com.nisolabluap.quickstart.application.exceptions.item.ItemNotFoundException;
import com.nisolabluap.quickstart.application.models.dtos.ItemDTO;
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
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ItemRepository itemRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    @Override
    public OrderDTO createOrder(OrderDTO orderDTO, Long customerId, List<Long> itemIds, List<Long> itemQuantities) {
        Map<Long, Long> itemIdQuantityMap = getLongLongMap(itemIds, itemQuantities);

        // Validate quantities for each item
        if (itemIdQuantityMap.values().stream().anyMatch(qty -> qty <= 0)) {
            throw new CustomerNotFoundException("Invalid quantities. Each quantity should be greater than 0.");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found!"));

        List<Item> items = itemRepository.findAllById(itemIds);

        if (items.isEmpty()) {
            throw new ItemNotFoundException("Items not found!");
        }

        // Assuming you want to check the stock of each item in the order
        for (Item item : items) {
            Long itemId = item.getId();
            Long quantity = itemIdQuantityMap.get(itemId);
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
        order.setCreatedAt(LocalDateTime.now());
        order.setItems(new HashSet<>());
        order.setQuantity(itemIdQuantityMap.values().stream().mapToInt(Long::intValue).sum());
        order.setTotalPrice(totalPrice);

        List<OrderItem> orderItems = new ArrayList<>();
        for (Item item : items) {
            Long itemId = item.getId();
            Long quantity = itemIdQuantityMap.get(itemId);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setQuantity(quantity.intValue());
            orderItem.setTotalPrice(item.getPrice() * quantity);

            orderItems.add(orderItem);

            // Update stock for each item
            updateStock(item, quantity);
        }

        order.getItems().addAll(orderItems.stream().map(OrderItem::getItem).collect(Collectors.toSet()));

        Order savedOrder = orderRepository.save(order);

        OrderDTO responseOrderDTO = new OrderDTO();
        responseOrderDTO.setId(savedOrder.getId());
        responseOrderDTO.setCustomerId(savedOrder.getCustomerId().getId());
        responseOrderDTO.setOrderStatus(savedOrder.getOrderStatus());
        responseOrderDTO.setCreatedAt(savedOrder.getCreatedAt());
        responseOrderDTO.setQuantity(savedOrder.getQuantity());
        responseOrderDTO.setTotalPrice(savedOrder.getTotalPrice());

        List<OrderItemDTO> orderItemDTOs = orderItems.stream()
                .map(this::mapOrderItemToDTO)
                .collect(Collectors.toList());

        responseOrderDTO.setItems(orderItemDTOs);

        return responseOrderDTO;
    }

    @NotNull
    private static Map<Long, Long> getLongLongMap(List<Long> itemIds, List<Long> itemQuantities) {
        if (itemIds.size() != itemQuantities.size()) {
            throw new ItemNotFoundException("itemIds and itemQuantities must have the same size.");
        }

        Map<Long, Long> itemIdQuantityMap = new HashMap<>();
        for (int i = 0; i < itemIds.size(); i++) {
            Long itemId = itemIds.get(i);
            Long quantity = itemQuantities.get(i);

            // Check for duplicate itemIds
            if (itemIdQuantityMap.containsKey(itemId)) {
                // If duplicate, accumulate quantity
                itemIdQuantityMap.put(itemId, itemIdQuantityMap.get(itemId) + quantity);
            } else {
                // If not a duplicate, add to the map
                itemIdQuantityMap.put(itemId, quantity);
            }
        }
        return itemIdQuantityMap;
    }

    // Additional method to update item stock
    private void updateStock(Item item, Long quantity) {
        long updatedStock = item.getAvailableQuantity() - quantity.intValue();
        item.setAvailableQuantity(updatedStock);
        itemRepository.save(item);
    }

    private OrderItemDTO mapOrderItemToDTO(OrderItem orderItem) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setItemId(orderItem.getItem().getId());
        orderItemDTO.setItemName(orderItem.getItem().getName());
        orderItemDTO.setQuantity(orderItem.getQuantity());
        orderItemDTO.setTotalPrice(orderItem.getTotalPrice());
        return orderItemDTO;
    }
}