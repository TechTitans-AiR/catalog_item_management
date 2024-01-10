package hr.techtitans.items.controllers;

import hr.techtitans.items.dtos.ItemCategoryDto;
import hr.techtitans.items.dtos.ItemDto;
import hr.techtitans.items.services.ItemCategoryService;
import hr.techtitans.items.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/itemCategory")
public class ItemCategoryController {
    @Autowired
    private ItemCategoryService itemCategoryService;

    @GetMapping
    public ResponseEntity<List<ItemCategoryDto>> getAllCategories(){
        return new ResponseEntity<>(itemCategoryService.getAllCategories(), HttpStatus.OK);
    }
}
