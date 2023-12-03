package hr.techtitans.items.controllers;


import hr.techtitans.items.services.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
