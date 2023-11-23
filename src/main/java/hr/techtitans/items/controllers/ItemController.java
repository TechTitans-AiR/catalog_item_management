package hr.techtitans.items.controllers;

import hr.techtitans.items.dtos.ItemDto;
import hr.techtitans.items.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/articles")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems(){
        return new ResponseEntity<List<ItemDto>>(itemService.allItems(), HttpStatus.OK);
    }
}
