package com.nisolabluap.quickstart.application.services;

import com.nisolabluap.quickstart.application.models.dtos.ItemDTO;
import com.nisolabluap.quickstart.application.models.dtos.ItemUpdateIsbnDTO;
import com.nisolabluap.quickstart.application.models.enums.ProductCategory;

import java.util.List;

public interface ItemService {

    List<ItemDTO> getAllItems();

    ItemDTO createItem(ItemDTO itemDTO, ProductCategory productCategory);

    ItemDTO updateItemById(Long id, ItemDTO itemDTO, ProductCategory productCategory);

    ItemUpdateIsbnDTO updateItemIsbnById(Long id, ItemUpdateIsbnDTO itemUpdateIsbnDTO);

    ItemDTO getItemById(Long id);

    void deleteItemById(Long id);

    List<ItemDTO> searchItems(String name, ProductCategory category, String description, Long id, String isbn, Long availableQuantity, Double price);

    List<String> getRecommended();
}
