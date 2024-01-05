package hr.techtitans.items.services;



import hr.techtitans.items.dtos.ItemDto;
import hr.techtitans.items.models.Item;
import hr.techtitans.items.models.ItemCategories;
import hr.techtitans.items.repositories.ItemRepository;
import hr.techtitans.items.repositories.ItemCategoriesRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;


    @Autowired
    private ItemCategoriesRepository itemCategoriesRepository;

    public List<ItemDto> allItems(){
        List<Item> items = itemRepository.findAll();
        return items.stream().map(this::mapToItemDto).collect(Collectors.toList());

    }

    private ItemDto mapToItemDto(Item item){
        ItemCategories itemCategories = itemCategoriesRepository.findById(item.getItemCategory()).orElse(null);
        return new ItemDto(
                item.getId(),
                itemCategories,
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getQuantity_in_stock(),
                item.getWeight(),
                item.getMaterial(),
                item.getBrand(),
                item.getCurrency()
        );
    }

    public ItemDto getItemById(String userId) {
        ObjectId objectId = new ObjectId(userId);
        Optional<Item> optionalItem = itemRepository.findById(objectId);

        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();
            return mapToItemDto(item);
        } else {
            return null;
        }
    }

    public ResponseEntity<?> updateItem(String itemId, Map<String, Object> payload){
        try {
            if (itemId == null || itemId.isEmpty()) {
                return new ResponseEntity<>("Item ID not provided", HttpStatus.BAD_REQUEST);
            }
            ObjectId objectId=new ObjectId(itemId);
            Optional<Item> existingItem = itemRepository.findById(objectId);

            if (existingItem.isPresent()) {
                Item itemToUpdate = existingItem.get();
                if(payload.containsKey("name")){
                    itemToUpdate.setName((String) payload.get("name"));
                }
                if (payload.containsKey("description")) {
                    itemToUpdate.setDescription((String) payload.get("description"));
                }
                if (payload.containsKey("price")) {
                    itemToUpdate.setPrice((Double) payload.get("price"));
                }
                if (payload.containsKey("weight")) {
                    itemToUpdate.setWeight((Double) payload.get("weight"));
                }
                if (payload.containsKey("quantity_in_stock")) {
                    itemToUpdate.setQuantity_in_stock((Integer) payload.get("quantity_in_stock"));
                }
                if (payload.containsKey("material")) {
                    itemToUpdate.setMaterial((String) payload.get("material"));
                }
                if (payload.containsKey("brand")) {
                    itemToUpdate.setBrand((String) payload.get("brand"));
                }
                if (payload.containsKey("currency")) {
                    itemToUpdate.setCurrency((String) payload.get("currency"));
                }
                if (payload.containsKey("category")) {
                    String categoryId = (String) payload.get("category");
                    ObjectId newId = new ObjectId(categoryId);
                    itemToUpdate.setItemCategory(newId);
                }
                itemRepository.save(itemToUpdate);
                return new ResponseEntity<>("Item updated successfully", HttpStatus.OK);
            }else {
                return new ResponseEntity<>("Item not found", HttpStatus.NOT_FOUND);
            }

        } catch (IllegalArgumentException e) {
            Map<String, Object> responseBody = Map.of("message", "Item not found");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> responseBody = Map.of("message", "An error occurred");
            return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }





    public ResponseEntity<Object> deleteItemById(String itemId) {
        try {
            if (itemId == null || itemId.isEmpty()) {
                return new ResponseEntity<>("Item ID not provided", HttpStatus.BAD_REQUEST);
            }
            ObjectId objectId = new ObjectId(itemId);
            if (itemRepository.existsById(objectId)) {
                itemRepository.deleteById(objectId);
                Map<String, Object> responseBody = Map.of("message", "Item deleted successfully");
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            } else {
                Map<String, Object> responseBody = Map.of("message", "Item not found");
                return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
            }

        } catch (IllegalArgumentException e) {
            Map<String, Object> responseBody = Map.of("message", "Item not found");
            return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> responseBody = Map.of("message", "An error occurred");
            return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

