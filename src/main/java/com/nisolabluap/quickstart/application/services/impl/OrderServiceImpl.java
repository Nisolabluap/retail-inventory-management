package com.nisolabluap.quickstart.application.services.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nisolabluap.quickstart.application.exceptions.customer.CustomerNotFoundException;
import com.nisolabluap.quickstart.application.exceptions.item.ItemsNotFoundException;
import com.nisolabluap.quickstart.application.exceptions.item.ItemsOutOfStockException;
import com.nisolabluap.quickstart.application.exceptions.order.InvalidQuantitiesException;
import com.nisolabluap.quickstart.application.exceptions.order.OrderAlreadyRefundedException;
import com.nisolabluap.quickstart.application.exceptions.order.OrderNotFoundException;
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
import com.nisolabluap.quickstart.application.services.Validator;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private Validator validator;

    @Transactional
    @Override
    public OrderDTO createOrder(OrderDTO orderDTO, Long customerId, List<Long> itemIds, List<Long> itemQuantities) {
        Map<Long, Long> itemIdQuantityMap = getLongLongMap(itemIds, itemQuantities);
        validateCustomerAndItems(customerId, itemIds, itemIdQuantityMap);

        List<Item> items = fetchItems(itemIdQuantityMap);

        validateOutOfStock(items, itemIdQuantityMap);

        double totalPrice = calculateTotalPrice(items, itemIdQuantityMap);

        Order savedOrder = saveOrder(customerId, itemIdQuantityMap, totalPrice);

        List<OrderItem> orderItems = saveOrderItems(items, savedOrder, itemIdQuantityMap);

        return mapOrderToDTO(savedOrder, orderItems);
    }

    @Transactional
    @Override
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(validator.getOrderNotFoundMessage(orderId)));

        validateOrderStatus(order);

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
                .orElseThrow(() -> new OrderNotFoundException(validator.getOrderNotFoundMessage(orderId)));

        return mapOrderToDTO(order);
    }

    private Map<Long, Long> getLongLongMap(List<Long> itemIds, List<Long> itemQuantities) {
        if (itemIds.size() != itemQuantities.size()) {
            throw new InvalidQuantitiesException(validator.getInvalidQuantitiesMessage());
        }

        Map<Long, Long> itemIdQuantityMap = new HashMap<>();
        for (int i = 0; i < itemIds.size(); i++) {
            Long itemId = itemIds.get(i);
            Long quantity = itemQuantities.get(i);
            itemIdQuantityMap.put(itemId, itemIdQuantityMap.getOrDefault(itemId, 0L) + quantity);
        }
        return itemIdQuantityMap;
    }

    private void validateCustomerAndItems(Long customerId, List<Long> itemIds, Map<Long, Long> itemIdQuantityMap) {
        customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException(validator.getCustomerNotFoundMessage(customerId)));
        List<Item> items = fetchItems(itemIdQuantityMap);
        validateItemsExist(new ArrayList<>(itemIds), items);
    }

    private List<Item> fetchItems(Map<Long, Long> itemIdQuantityMap) {
        List<Item> items = itemRepository.findAllById(new ArrayList<>(itemIdQuantityMap.keySet()));
        validateItemsExist(new ArrayList<>(itemIdQuantityMap.keySet()), items);
        return items;
    }

    private void validateItemsExist(List<Long> itemIds, List<Item> items) {
        Set<Long> fetchedItemIds = items.stream().map(Item::getId).collect(Collectors.toSet());
        Set<Long> missingItemIds = new HashSet<>(itemIds);
        missingItemIds.removeAll(fetchedItemIds);

        if (!missingItemIds.isEmpty()) {
            throw new ItemsNotFoundException(validator.getItemsNotFoundMessage(new ArrayList<>(missingItemIds)));
        }
    }

    private void validateOutOfStock(List<Item> items, Map<Long, Long> itemIdQuantityMap) {
        List<Item> outOfStockItems = new ArrayList<>();

        for (Item item : items) {
            Long quantity = itemIdQuantityMap.get(item.getId());
            if (quantity > item.getAvailableQuantity()) {
                outOfStockItems.add(item);
            }
        }

        if (!outOfStockItems.isEmpty()) {
            List<Long> outOfStockItemIds = outOfStockItems.stream().map(Item::getId).toList();
            throw new ItemsOutOfStockException(validator.getItemsOutOfStockMessage(outOfStockItemIds));
        }
    }

    private double calculateTotalPrice(List<Item> items, Map<Long, Long> itemIdQuantityMap) {
        return items.stream()
                .mapToDouble(item -> item.getPrice() * itemIdQuantityMap.get(item.getId()))
                .sum();
    }

    private Order saveOrder(Long customerId, Map<Long, Long> itemIdQuantityMap, double totalPrice) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(validator.getCustomerNotFoundMessage(customerId)));

        Order order = new Order();
        order.setCustomerId(customer);
        order.setOrderStatus(OrderStatus.PAYED);
        order.setOrderItems(new HashSet<>());
        order.setTotalQuantity(itemIdQuantityMap.values().stream().mapToInt(Long::intValue).sum());
        order.setTotalPrice(totalPrice);

        return orderRepository.save(order);
    }

    private List<OrderItem> saveOrderItems(List<Item> items, Order savedOrder, Map<Long, Long> itemIdQuantityMap) {
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
        orderRepository.save(savedOrder);

        return orderItems;
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

            long updatedStock = item.getAvailableQuantity() + refundedQuantity;
            item.setAvailableQuantity(updatedStock);
            itemRepository.save(item);
        }

        order.setOrderStatus(OrderStatus.REFUNDED);
        orderRepository.save(order);
    }

    private void validateOrderStatus(Order order) {
        OrderStatus currentStatus = order.getOrderStatus();

        if (currentStatus == OrderStatus.REFUNDED) {
            throw new OrderAlreadyRefundedException(validator.getOrderAlreadyRefundedMessage(order.getId()));
        }
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
                .toList();
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
}
