package com.nisolabluap.quickstart.application.services.impl;

import com.nisolabluap.quickstart.application.exceptions.item.DuplicateItemException;
import com.nisolabluap.quickstart.application.exceptions.item.ItemDataIntegrityException;
import com.nisolabluap.quickstart.application.exceptions.item.ItemNotFoundException;
import com.nisolabluap.quickstart.application.models.dtos.ItemDTO;
import com.nisolabluap.quickstart.application.models.dtos.ItemUpdateIsbnDTO;
import com.nisolabluap.quickstart.application.models.entities.Customer;
import com.nisolabluap.quickstart.application.models.entities.Item;
import com.nisolabluap.quickstart.application.models.enums.ProductCategory;
import com.nisolabluap.quickstart.application.repositories.CustomerRepository;
import com.nisolabluap.quickstart.application.repositories.ItemRepository;
import com.nisolabluap.quickstart.application.services.ItemService;
import com.nisolabluap.quickstart.application.services.OpenAIIntegrationService;
import com.nisolabluap.quickstart.application.services.Validator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final Validator validator;
    @Autowired
    private final CustomerRepository customerRepository;
    @Autowired
    private final OpenAIIntegrationService openAIIntegrationService;

    @Override
    public List<ItemDTO> searchItems(String name, ProductCategory category, String description, Long id, String isbn, Long availableQuantity, Double price) {
        List<Item> items = itemRepository.searchItems(name, category, description, id, isbn, availableQuantity, price);
        return items.stream()
                .map(item -> {
                    ItemDTO itemDTO = new ItemDTO();
                    itemDTO.setName(item.getName());
                    itemDTO.setProductCategory(item.getProductCategory());
                    itemDTO.setDescription(item.getDescription());
                    itemDTO.setId(item.getId());
                    itemDTO.setIsbn(item.getIsbn());
                    itemDTO.setPrice(item.getPrice());
                    itemDTO.setAvailableQuantity(item.getAvailableQuantity());
                    return itemDTO;
                })
                .toList();
    }

    @Override
    public List<ItemDTO> getAllItems() {
        List<Item> inventories = itemRepository.findAll();
        return inventories.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public ItemDTO createItem(@Valid ItemDTO itemDTO, ProductCategory productCategory) {
        validateDuplicateItemByIsbn(itemDTO.getIsbn());
        Item item = convertToEntity(itemDTO);
        item.setProductCategory(productCategory);
        Item savedItem = itemRepository.save(item);
        return convertToDTO(savedItem);
    }

    @Override
    public ItemDTO updateItemById(Long id, ItemDTO itemDTO, ProductCategory productCategory) {
        Item existingItem = getItemByIdOrThrowException(id);
        BeanUtils.copyProperties(itemDTO, existingItem, "id", "isbn");
        existingItem.setProductCategory(productCategory);
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
        try {
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
        } catch (DataIntegrityViolationException e) {
            throw new ItemDataIntegrityException(validator.getDataIntegrityItemMessage(id));
        }
    }

    @Override
    public List<String> getRecommended() {
        String openAiPrompt = """
        Based on the existing items and the number of customers for each item, we recommend adding two new items to the available options.
        The list of items is as follows:

        1. Item1 - Customers: 10
        2. Item2 - Customers: 15
        3. Item3 - Customers: 8

        Please provide two examples in the format 'Item1,10; Item2,15' where each item includes a name and a customer count.
        """;

        return Arrays.asList(openAIIntegrationService.getOpenAIResponse(openAiPrompt).split(", "));
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

    private void validateDuplicateItemByIsbn(String isbn) {
        if (itemRepository.existsByIsbn(isbn)) {
            throw new DuplicateItemException(validator.getDuplicateItemMessage(isbn, validator.getFindExistingIdByIsbn(isbn)));
        }
    }

    private Item getItemByIdOrThrowException(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(validator.getItemNotFoundMessage(id)));
    }
}
