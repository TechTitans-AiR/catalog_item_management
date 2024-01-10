package hr.techtitans.items.services;



import hr.techtitans.items.dtos.ItemDto;
import hr.techtitans.items.models.Item;
import hr.techtitans.items.models.ItemCategories;
import hr.techtitans.items.repositories.ItemRepository;
import hr.techtitans.items.repositories.ItemCategoriesRepository;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;


    @Autowired
    private ItemCategoriesRepository itemCategoriesRepository;

    public ResponseEntity<Object> checkAdminRole(String token) {
        try {
            String role = getRoleFromToken(token);
            if (!Objects.equals(role, "admin")) {
                return new ResponseEntity<>("Only admin users can perform this action", HttpStatus.UNAUTHORIZED);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred while checking admin role", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public String getRoleFromToken(String token) {
        try {
            String[] tokenParts = token.split("\\.");

            if (tokenParts.length != 3) {
                System.out.println("Invalid token format");
                System.out.println(tokenParts.length);
                return null;
            }

            String payload = tokenParts[1];

            byte[] decodedPayload = java.util.Base64.getUrlDecoder().decode(payload);
            String decodedPayloadString = new String(decodedPayload, StandardCharsets.UTF_8);

            JSONObject payloadJson = new JSONObject(decodedPayloadString);

            String role = payloadJson.getString("role");

            System.out.println("Role from Token in getRoleFromToken: " + role);
            System.out.println(payloadJson);

            return role;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ItemDto> allItems(String token){
        String role = getRoleFromToken(token);
        if (!Objects.equals(role, "admin")) {
            return Collections.emptyList();
        }
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

    public ItemDto getItemById(String userId, String token) {
        ResponseEntity<Object> adminCheckResult = checkAdminRole(token);
        if (adminCheckResult != null) {
            System.out.println("Unauthorized: " + adminCheckResult.getBody());
            return null;
        }
        ObjectId objectId = new ObjectId(userId);
        Optional<Item> optionalItem = itemRepository.findById(objectId);

        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();
            return mapToItemDto(item);
        } else {
            return null;
        }
    }

    public ResponseEntity<?> updateItem(String itemId, Map<String, Object> payload, String token){
        try {
            ResponseEntity<Object> adminCheckResult = checkAdminRole(token);
            if (adminCheckResult != null) {
                return adminCheckResult;
            }
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

    public ResponseEntity<Object> deleteItemById(String itemId, String token) {
        try {
            ResponseEntity<Object> adminCheckResult = checkAdminRole(token);
            if (adminCheckResult != null) {
                return adminCheckResult;
            }
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

    public ResponseEntity<Object> createItem(Map<String, Object> payload, String token) {

        ResponseEntity<Object> adminCheckResult = checkAdminRole(token);
        if (adminCheckResult != null) {
            return adminCheckResult;
        }
        String name = (String) payload.get("name");
        if (name == null || name.trim().isEmpty()) {
            return new ResponseEntity<>("Article name is mandatory", HttpStatus.BAD_REQUEST);
        }

        String category = (String) payload.get("itemCategory");
        if (category == null || category.trim().isEmpty()) {
            return new ResponseEntity<>("Article category is mandatory", HttpStatus.BAD_REQUEST);
        }

        String categoryId = (String) payload.get("itemCategory");
        ObjectId newCategoryId = new ObjectId(categoryId);


        if(!itemCategoriesRepository.findById(newCategoryId).isPresent()){
            System.out.println("Nema kategorije s tim ID");
            return new ResponseEntity<>("Article category not found", HttpStatus.BAD_REQUEST);
        }else{
            System.out.println("Category id->"+itemCategoriesRepository.findById(newCategoryId));
        }

        hr.techtitans.items.models.Item item = new hr.techtitans.items.models.Item();

        item.setName((String) payload.get("name"));

        item.setItemCategory(newCategoryId);
        item.setDescription((String) payload.get("description"));
        item.setMaterial((String) payload.get("material"));
        item.setPrice((Double) payload.get("price"));
        item.setQuantity_in_stock((Integer) payload.get("quantity_in_stock"));
        item.setWeight((Double) payload.get("weight"));
        item.setBrand((String) payload.get("brand"));
        item.setCurrency((String) payload.get("currency"));

        itemRepository.insert(item);
        return new ResponseEntity<>("New article created successfully", HttpStatus.CREATED);
    }
}

