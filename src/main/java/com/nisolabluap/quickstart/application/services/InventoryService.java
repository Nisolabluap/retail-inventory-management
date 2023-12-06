package com.nisolabluap.quickstart.application.services;

import com.nisolabluap.quickstart.application.models.dtos.InventoryDTO;

import java.util.List;

public interface InventoryService {
    List<InventoryDTO> getAllInventory();
    InventoryDTO createInventoryItem(InventoryDTO inventoryDTO);
    InventoryDTO updateInventoryItem(Long id, InventoryDTO inventoryDTO);
    InventoryDTO getInventoryByItemId(Long id);
    void deleteInventoryItem(Long id);
    void deleteAllInventory();
}
