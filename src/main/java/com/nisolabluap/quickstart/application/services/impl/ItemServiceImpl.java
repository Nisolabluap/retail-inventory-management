package com.nisolabluap.quickstart.application.services.impl;

import com.nisolabluap.quickstart.application.exceptions.item.DuplicateItemException;
import com.nisolabluap.quickstart.application.exceptions.item.ItemNotFoundException;
import com.nisolabluap.quickstart.application.models.dtos.ItemDTO;
import com.nisolabluap.quickstart.application.models.dtos.ItemUpdateIsbnDTO;
import com.nisolabluap.quickstart.application.models.entities.Customer;
import com.nisolabluap.quickstart.application.models.entities.Item;
import com.nisolabluap.quickstart.application.repositories.CustomerRepository;
import com.nisolabluap.quickstart.application.repositories.ItemRepository;
import com.nisolabluap.quickstart.application.services.CustomerService;
import com.nisolabluap.quickstart.application.services.ItemService;
import com.nisolabluap.quickstart.application.services.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private Validator validator;
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<ItemDTO> getAllItems() {
        List<Item> inventories = itemRepository.findAll();
        return inventories.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public ItemDTO createItem(ItemDTO itemDTO) {
        validateDuplicateItemByIsbn(itemDTO.getIsbn());

        Item item = convertToEntity(itemDTO);
        Item savedItem = itemRepository.save(item);
        return convertToDTO(savedItem);
    }

    @Override
    public ItemDTO updateItemById(Long id, ItemDTO itemDTO) {
        Item existingItem = getItemByIdOrThrowException(id);
        BeanUtils.copyProperties(itemDTO, existingItem, "id", "isbn");

        Item updatedItem = itemRepository.save(existingItem);
        return convertToDTO(updatedItem);
    }

    public ItemUpdateIsbnDTO updateItemIsbnById(Long id, ItemUpdateIsbnDTO itemUpdateIsbnDTO) {
        Item existingItem = getItemByIdOrThrowException(id);
        if (!existingItem.getIsbn().equals(itemUpdateIsbnDTO.getIsbn())) {
            validateDuplicateItemByIsbn(itemUpdateIsbnDTO.getIsbn());
        }
        BeanUtils.copyProperties(itemUpdateIsbnDTO, existingItem, "id");
        Item updatedItem = itemRepository.save(existingItem);
        return convertToItemUpdateIsbnDTO(updatedItem);
    }

    @Override
    public ItemDTO getItemById(Long id) {
        Item item = getItemByIdOrThrowException(id);
        return convertToDTO(item);
    }

    @Override
    public void deleteItemById(Long id) {
        Optional<Item> optionalItem = itemRepository.findById(id);

        if (optionalItem.isEmpty()) {
            throw new ItemNotFoundException(validator.getItemNotFoundMessage(id));
        }

        Item item = optionalItem.get();

        List<Customer> customers = customerRepository.findByFavoriteItemsContaining(item);
        for (Customer customer : customers) {
            customer.getFavoriteItems().remove(item);
        }
        customerRepository.saveAll(customers);

        itemRepository.deleteById(id);
    }

    @Override
    public void deleteAllItems() {
        itemRepository.deleteAll();
    }

    private ItemDTO convertToDTO(Item item) {
        ItemDTO itemDTO = new ItemDTO();
        BeanUtils.copyProperties(item, itemDTO);
        return itemDTO;
    }

    private Item convertToEntity(ItemDTO itemDTO) {
        Item item = new Item();
        BeanUtils.copyProperties(itemDTO, item);
        return item;
    }

    private ItemUpdateIsbnDTO convertToItemUpdateIsbnDTO(Item item) {
        ItemUpdateIsbnDTO itemUpdateIsbnDTO = new ItemUpdateIsbnDTO();
        BeanUtils.copyProperties(item, itemUpdateIsbnDTO);
        return itemUpdateIsbnDTO;
    }

    private void validateDuplicateIdAndIsbn(Long id, String isbn) {
        if (itemRepository.existsByIdAndIsbnNot(id, isbn)) {
            throw new DuplicateItemException(validator.getDuplicateItemMessage(isbn, validator.findExistingIdByIsbn(isbn)));
        }
    }

    private void validateDuplicateItemByIsbn(String isbn) {
        if (itemRepository.existsByIsbn(isbn)) {
            throw new DuplicateItemException(validator.getDuplicateItemMessage(isbn, validator.findExistingIdByIsbn(isbn)));
        }
    }

    private Item getItemByIdOrThrowException(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(validator.getItemNotFoundMessage(id)));
    }
}
