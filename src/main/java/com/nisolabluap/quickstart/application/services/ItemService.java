package com.nisolabluap.quickstart.application.services;

import com.nisolabluap.quickstart.application.models.dtos.ItemDTO;
import com.nisolabluap.quickstart.application.models.dtos.ItemUpdateIsbnDTO;

import java.util.List;

public interface ItemService {

    List<ItemDTO> getAllItems();

    ItemDTO createItem(ItemDTO itemDTO);

    ItemDTO updateItemById(Long id, ItemDTO itemDTO);

    ItemUpdateIsbnDTO updateItemIsbnById(Long id, ItemUpdateIsbnDTO itemUpdateIsbnDTO);

    ItemDTO getItemById(Long id);

    void deleteItemById(Long id);

    void deleteAllItems();
}
