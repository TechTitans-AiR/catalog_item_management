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
    public ResponseEntity<List<ItemDto>> getAllItems(){
        return new ResponseEntity<List<ItemDto>>(itemService.allItems(), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<?> getItemById(@PathVariable String itemId){
        try {
            ItemDto itemDto = itemService.getItemById(itemId);

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
    public ResponseEntity<Object> deleteItem(@PathVariable String itemId) {
        return itemService.deleteItemById(itemId);
    }
    @DeleteMapping("/delete/")
    public ResponseEntity<Object> noItemIdProvided() {
        Map<String, Object> responseBody = Map.of("message", "Please provide an item ID");
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }
}