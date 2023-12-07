package hr.techtitans.items.controllers;


import hr.techtitans.items.dtos.CatalogDto;
import hr.techtitans.items.dtos.ItemDto;
import hr.techtitans.items.services.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/catalogs")
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    @PostMapping("/create")
    public ResponseEntity<Object> createCatalog(@RequestBody Map<String, Object> payload){
        return new ResponseEntity<>(catalogService.createCatalog(payload), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CatalogDto>> getAllCatalogs(){
        return new ResponseEntity<List<CatalogDto>>(catalogService.allCatalogs(), HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getCatalogByUser(@PathVariable String userId) {
        try {
            // Your logic to retrieve the catalog based on the user ID
            CatalogDto catalog = catalogService.getCatalogByUserId(userId);

            if (catalog != null) {
                return new ResponseEntity<>(catalog, HttpStatus.OK);
            } else {
                String errorMessage = "Catalog not found for user with ID: " + userId;
                return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
