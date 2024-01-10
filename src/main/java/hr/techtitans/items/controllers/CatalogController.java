package hr.techtitans.items.controllers;


import hr.techtitans.items.dtos.CatalogDto;
import hr.techtitans.items.dtos.ItemDto;
import hr.techtitans.items.services.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import hr.techtitans.items.controllers.UnauthorizedException;
import hr.techtitans.items.controllers.InternalServerErrorException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/catalogs")
public class CatalogController {

    @Autowired
    private CatalogService catalogService;

    @PostMapping("/create")
    public ResponseEntity<Object> createCatalog(@RequestBody Map<String, Object> payload, @RequestHeader("Authorization") String token){
        return new ResponseEntity<>(catalogService.createCatalog(payload, token), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Object> getAllCatalogs(@RequestHeader("Authorization") String token) {
        ResponseEntity<Object> adminCheckResult = catalogService.checkAdminRole(token);
        if (adminCheckResult != null) {
            return adminCheckResult;
        }

        List<CatalogDto> allCatalogs = catalogService.allCatalogs(token);

        if (allCatalogs.isEmpty()) {
            return new ResponseEntity<>("Unauthorized or an error occurred", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(allCatalogs, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCatalogsByUser(@PathVariable String userId, @RequestHeader("Authorization") String token) {
        try {
            List<CatalogDto> catalogsDto = catalogService.getCatalogsByUserId(userId, token);

            if (catalogsDto != null && !catalogsDto.isEmpty()) {
                return new ResponseEntity<>(catalogsDto, HttpStatus.OK);
            } else {
                String errorMessage = "Catalogs not found for user with ID: " + userId;
                return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
            }
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (CatalogService.BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (InternalServerErrorException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{catalogId}")
    public ResponseEntity<?> getCatalogById(@PathVariable String catalogId, @RequestHeader("Authorization") String token) {
        try {
            ResponseEntity<Object> userAccessCheckResult = catalogService.checkUserAccess(token, catalogId);
            if (userAccessCheckResult != null) {
                return userAccessCheckResult;
            }

            CatalogDto catalogDto = catalogService.getCatalogById(catalogId, token);

            if (catalogDto != null) {
                return new ResponseEntity<>(catalogDto, HttpStatus.OK);
            } else {
                String errorMessage = "Catalog with id: " + catalogId + " is not found.";
                return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PatchMapping("/disable/{catalogId}")
    public ResponseEntity<Object> disableCatalog(@PathVariable String catalogId, @RequestHeader("Authorization") String token){
        try {
            ResponseEntity<Object> adminCheckResult = catalogService.checkAdminRole(token);
            if (adminCheckResult != null) {
                return adminCheckResult;
            }
            boolean isDisabled = catalogService.disableCatalog(catalogId, token);
            if (isDisabled) {
                return new ResponseEntity<>("Catalog successfully disabled.", HttpStatus.OK);
            } else {
                String errorMessage = "Catalog with id: " + catalogId + " is not found or already disabled.";
                return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/enable/{catalogId}")
    public ResponseEntity<Object> enableCatalog(@PathVariable String catalogId, @RequestHeader("Authorization") String token){
        try {
            ResponseEntity<Object> adminCheckResult = catalogService.checkAdminRole(token);
            if (adminCheckResult != null) {
                return adminCheckResult;
            }
            boolean isDisabled = catalogService.enableCatalog(catalogId, token);
            if (!isDisabled) {
                return new ResponseEntity<>("Catalog successfully enabled.", HttpStatus.OK);
            } else {
                String errorMessage = "Catalog with id: " + catalogId + " is not found or already enabled.";
                return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/update/{catalogId}")
    public ResponseEntity<Object> updateCatalog(@PathVariable String catalogId, @RequestBody CatalogDto updatedCatalogDto, @RequestHeader("Authorization") String token) {
        return catalogService.updateCatalog(catalogId, updatedCatalogDto, token);
    }
}
