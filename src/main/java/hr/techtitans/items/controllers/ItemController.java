package hr.techtitans.items.controllers;

import hr.techtitans.items.dtos.ItemDto;
import hr.techtitans.items.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/articles")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems(@RequestHeader("Authorization") String token){
        ResponseEntity<Object> adminCheckResult = itemService.checkAdminRole(token);
        if (adminCheckResult != null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return new ResponseEntity<>(itemService.allItems(token), HttpStatus.OK);
    }


    @PutMapping("/update/{articleId}")
    public ResponseEntity<?> updateArticle(@PathVariable String articleId, @RequestBody Map<String, Object> payload, @RequestHeader("Authorization") String token) {
        try {
            ResponseEntity<Object> adminCheckResult = itemService.checkAdminRole(token);
            if (adminCheckResult != null) {
                return adminCheckResult;
            }
            ResponseEntity<?> response = itemService.updateItem(articleId, payload, token);

            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok("Article updated successfully.");
            } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Article not found with id: " + articleId);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating the article: " + response.getBody());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating the article: " + e.getMessage());
        }
    }


    @GetMapping("/{itemId}")
    public ResponseEntity<?> getItemById(@PathVariable String itemId, @RequestHeader("Authorization") String token){
        try {
            ResponseEntity<Object> userCheckResult = itemService.checkUserRole(token);

            if (userCheckResult != null) {
                return userCheckResult;
            }
            ItemDto itemDto = itemService.getItemById(itemId, token);

            if (itemDto != null) {
                return new ResponseEntity<>(itemDto, HttpStatus.OK);
            } else {
                String errorMessage = "Item with id: " + itemId + " is not found.";
                return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/delete/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable String itemId, @RequestHeader("Authorization") String token) {
        ResponseEntity<Object> adminCheckResult = itemService.checkAdminRole(token);
        if (adminCheckResult != null) {
            return adminCheckResult;
        }
        return itemService.deleteItemById(itemId, token);
    }
    @DeleteMapping("/delete/")
    public ResponseEntity<Object> noItemIdProvided() {
        Map<String, Object> responseBody = Map.of("message", "Please provide an item ID");
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createItem(@RequestBody Map<String, Object> payload, @RequestHeader("Authorization") String token){
        return new ResponseEntity<>(itemService.createItem(payload, token), HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getItemsByUser(@PathVariable String userId, @RequestHeader("Authorization") String token) {
        try {
            List<ItemDto> itemsDto = itemService.getItemsByUserId(userId, token);

            if (!itemsDto.isEmpty()) {
                return new ResponseEntity<>(itemsDto, HttpStatus.OK);
            } else {
                String errorMessage = "No items found for user with ID: " + userId;
                return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
            }
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}