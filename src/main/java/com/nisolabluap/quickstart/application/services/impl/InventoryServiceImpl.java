package com.nisolabluap.quickstart.application.services.impl;

import com.nisolabluap.quickstart.application.exceptions.inventory_controller.DuplicateInventoryItemException;
import com.nisolabluap.quickstart.application.exceptions.inventory_controller.InventoryNotFoundException;
import com.nisolabluap.quickstart.application.models.dtos.InventoryDTO;
import com.nisolabluap.quickstart.application.models.entities.Inventory;
import com.nisolabluap.quickstart.application.repositories.InventoryRepository;
import com.nisolabluap.quickstart.application.services.InventoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public List<InventoryDTO> getAllInventory() {
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public InventoryDTO createInventoryItem(InventoryDTO inventoryDTO) {
        if (inventoryRepository.existsByIsbn(inventoryDTO.getIsbn())) {
            throw new DuplicateInventoryItemException("Item with the same ISBN " + inventoryDTO.getIsbn() + " already exists.");
        }

        Inventory inventory = convertToEntity(inventoryDTO);
        Inventory savedInventory = inventoryRepository.save(inventory);
        return convertToDTO(savedInventory);
    }

    @Override
    public InventoryDTO updateInventoryItem(Long id, InventoryDTO updatedInventoryDTO) {
        Inventory existingInventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new InventoryNotFoundException("Item " + id + " not found."));

        BeanUtils.copyProperties(updatedInventoryDTO, existingInventory, "id", "isbn");

        Inventory updatedInventory = inventoryRepository.save(existingInventory);
        return convertToDTO(updatedInventory);
    }

    @Override
    public InventoryDTO getInventoryByItemId(Long id) {
        Optional<Inventory> packageOptional = inventoryRepository.findById(id);
        if (packageOptional.isPresent()) {
            Inventory inventory = packageOptional.get();
            return convertToDTO(inventory);
        } else {
            throw new EntityNotFoundException("Item with ID " + id + " not found.");
        }
    }

    @Override
    public void deleteInventoryItem(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Item not found.");
        }
        inventoryRepository.deleteById(id);
    }

    @Override
    public void deleteAllInventory() {
        inventoryRepository.deleteAll();
    }

    private InventoryDTO convertToDTO(Inventory inventory) {
        InventoryDTO inventoryDTO = new InventoryDTO();
        BeanUtils.copyProperties(inventory, inventoryDTO);
        return inventoryDTO;
    }

    private Inventory convertToEntity(InventoryDTO inventoryDTO) {
        Inventory inventory = new Inventory();
        BeanUtils.copyProperties(inventoryDTO, inventory);
        return inventory;
    }
}
