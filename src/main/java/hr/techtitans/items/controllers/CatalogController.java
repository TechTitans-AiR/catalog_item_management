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

            List<CatalogDto> catalogsDto = catalogService.getCatalogsByUserId(userId);

            if (catalogsDto != null && !catalogsDto.isEmpty()) {
                return new ResponseEntity<>(catalogsDto, HttpStatus.OK);
            } else {
                String errorMessage = "Catalog not found for user with ID: " + userId;
                return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{catalogId}")
    public ResponseEntity<?> getCatalogById(@PathVariable String catalogId){
        try{
            CatalogDto catalogDto = catalogService.getCatalogById(catalogId);
            if(catalogDto!=null){
                return new ResponseEntity<>(catalogDto, HttpStatus.OK);
            }else{
                String errorMessage = "Catalog with id: " + catalogId + " is not found.";
                return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
            }

        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
