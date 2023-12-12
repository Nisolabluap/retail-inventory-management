package com.nisolabluap.quickstart.application.services;

import com.nisolabluap.quickstart.application.exceptions.item.ItemNotFoundException;
import com.nisolabluap.quickstart.application.models.entities.Item;
import com.nisolabluap.quickstart.application.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Validator {

    @Autowired
    private ItemRepository itemRepository;

    public Long getFindExistingIdByIsbn(String isbn) {
        Optional<Item> optionalInventory = itemRepository.findByIsbn(isbn);

        return optionalInventory.map(Item::getId)
                .orElseThrow(() -> new ItemNotFoundException("Item with ISBN: '" + isbn + "' not found."));
    }

    public String getDuplicateItemMessage(String isbn, Long existingId) {
        return String.format("An item with this ISBN: '%s' already exists. Duplicate with existing ID: '%d'.", isbn, existingId);
    }

    public String getItemNotFoundMessage(Long id) {
        return String.format("Item with ID: '%d' not found.", id);
    }

    public String getItemsNotFoundMessage(List<Long> itemIds) {
        return String.format("One or more items not found: %s.", itemIds);
    }

    public String getItemsOutOfStockMessage(List<Long> itemIds) {
        return String.format("One or more items is out of stock: %s.", itemIds);
    }

    public String getDuplicateEmailMessage(String email) {
        return String.format("An user with this EMAIL: '%s' already exists.", email);
    }

    public String getInvalidQuantitiesMessage() {
        return "Invalid quantities. Each quantity should be greater than 0 and their total number must not exceed the number of 'items'.";
    }

    public String getCustomerNotFoundMessage(Long id) {
        return String.format("Customer with ID: '%d' not found.", id);
    }

    public String getOrderNotFoundMessage(Long id) {
        return String.format("Order with ID: '%d' not found.", id);
    }

    public String getOrderAlreadyRefundedMessage(Long id) {
        return String.format("Order with ID: '%d' was already refunded and cannot be modified.", id);
    }

    public String getDataIntegrityItemMessage(Long id) {
        return String.format("Item with ID: '%d' cannot be deleted because it is still referenced in other entities.", id);
    }

    public String getDataIntegrityCustomerMessage(Long id) {
        return String.format("Customer with ID: '%d' cannot be deleted because it is still referenced in other entities.", id);
    }

    public String getOpenAIMessage() {
        return "An error occur while trying to obtain the recommended products.";
    }
}
