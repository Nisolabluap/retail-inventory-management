package com.nisolabluap.quickstart.application.services;

import com.nisolabluap.quickstart.application.exceptions.item.ItemNotFoundException;
import com.nisolabluap.quickstart.application.models.entities.Item;
import com.nisolabluap.quickstart.application.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class Validator {

    @Autowired
    private ItemRepository itemRepository;

    public Long findExistingIdByIsbn(String isbn) {
        Optional<Item> optionalInventory = itemRepository.findByIsbn(isbn);

        return optionalInventory.map(Item::getId)
                .orElseThrow(() -> new ItemNotFoundException("Item with ISBN: '" + isbn + "' not found."));
    }

    public String getDuplicateItemMessage(String isbn, Long existingId) {
        return String.format("An item with this ISBN: '%s' already exists. Duplicate with existing ID: '%s'.", isbn, existingId);
    }

    public String getItemNotFoundMessage(Long id) {
        return String.format("Item with ID: '%d' not found.", id);
    }

    public String getDuplicateEmailMessage(String email) {
        return String.format("An user with this EMAIL: '%s' already exists.", email);
    }

    public String getCustomerNotFoundMessage(Long id) {
        return String.format("Customer with ID: '%d' not found.", id);
    }

    /*public void validateInventoryItem(InventoryDTO inventoryDTO) {
        validateName(inventoryDTO.getName());
        validateLocation(inventoryDTO.getLocation());
        validateDescription(inventoryDTO.getDescription());
        validateIsbn(inventoryDTO.getIsbn());
        validatePrice(inventoryDTO.getPrice());
    }

    private void validateName(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Name must not be empty.");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("Name must not exceed 50 characters.");
        }
    }

    private void validateLocation(String location) {
        if (StringUtils.isEmpty(location)) {
            throw new IllegalArgumentException("Location must not be empty.");
        }
        if (location.length() > 20) {
            throw new IllegalArgumentException("Location must not exceed 20 characters.");
        }
    }

    private void validateDescription(String description) {
        if (StringUtils.isEmpty(description)) {
            throw new IllegalArgumentException("Description must not be empty.");
        }
        if (description.length() > 500) {
            throw new IllegalArgumentException("Description must not exceed 500 characters.");
        }
    }

    private void validateIsbn(String isbn) {
        if (StringUtils.isEmpty(isbn)) {
            throw new IllegalArgumentException("ISBN must not be empty.");
        }
        if (!isbn.matches("^[0-9]{13}$")) {
            throw new IllegalArgumentException("ISBN format is invalid.");
        }
    }

    private void validatePrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0.");
        }
        if (price > Double.MAX_VALUE) {
            throw new IllegalArgumentException("Price exceeds the maximum value.");
        }
    }*/
}
