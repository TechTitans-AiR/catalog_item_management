package hr.techtitans.items.services;

import hr.techtitans.items.dtos.CatalogDto;
import hr.techtitans.items.dtos.ItemDto;
import hr.techtitans.items.models.Catalog;
import hr.techtitans.items.models.Item;
import hr.techtitans.items.models.ItemCategories;
import hr.techtitans.items.repositories.CatalogRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CatalogService {
    @Autowired
    private CatalogRepository catalogRepository;

    public ResponseEntity<Object> createCatalog(Map<String, Object> payload) {

        String name = (String) payload.get("name");
        if (name == null || name.trim().isEmpty()) {
            return new ResponseEntity<>("Name is mandatory", HttpStatus.BAD_REQUEST);
        }


        List<ObjectId> articleIds = (List<ObjectId>) payload.get("articles");
        List<ObjectId> serviceIds = (List<ObjectId>) payload.get("services");
        if ((articleIds == null || articleIds.isEmpty()) && (serviceIds == null || serviceIds.isEmpty())) {
            return new ResponseEntity<>("At least one article or service is required", HttpStatus.BAD_REQUEST);
        }


        Catalog catalog = new Catalog();
        List<ObjectId> userIds = (List<ObjectId>) payload.get("users");
        LocalDateTime currentDateTime = LocalDateTime.now();

        catalog.setName(name);
        catalog.setArticles(articleIds);
        catalog.setServices(serviceIds);
        catalog.setUsers(userIds);
        catalog.setDate_created(currentDateTime);
        catalog.setDate_modified(currentDateTime);
        catalog.setDisabled(false);


        catalogRepository.insert(catalog);
        return new ResponseEntity<>("Catalog created successfully", HttpStatus.CREATED);
    }

    public List<CatalogDto> allCatalogs(){
        List<Catalog> catalogs = catalogRepository.findAll();
        return catalogs.stream().map(this::mapToCatalogDto).collect(Collectors.toList());

    }

    private CatalogDto mapToCatalogDto(Catalog catalog){
        return new CatalogDto(
                catalog.getId(),
                catalog.getName(),
                catalog.getArticles(),
                catalog.getServices(),
                catalog.getUsers(),
                catalog.getDate_created(),
                catalog.getDate_modified(),
                catalog.getDisabled()
        );
    }
    private List<CatalogDto> mapToCatalogDtoList(List<Catalog> catalogs) {
        List<CatalogDto> catalogDtos = new ArrayList<>();
        for (Catalog catalog : catalogs) {

            if (catalog.getUsers() != null && !catalog.getUsers().isEmpty()) {
                catalogDtos.add(mapToCatalogDto(catalog));
            } else {

                System.out.println("Warning: Catalog with ID " + catalog.getId() + " does not have 'users' property.");
            }
        }
        return catalogDtos;
    }

    public CatalogDto getCatalogById(String catalogId) {
        ObjectId objectId = new ObjectId(catalogId);
        Optional<Catalog> optionalCatalog=catalogRepository.findById(objectId);
        if(optionalCatalog.isPresent()){
            Catalog catalog = optionalCatalog.get();
            return mapToCatalogDto(catalog);
        }else{
            return null;
        }
    }

    public class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message);
        }
    }

    public class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }


    public List<CatalogDto> getCatalogsByUserId(String userId) {
        try {
            ObjectId objectId = new ObjectId(userId);
            List<Catalog> catalogs = new ArrayList<>();
            catalogs = catalogRepository.findByUsersContains(objectId);


            if (catalogs != null ) {
                return mapToCatalogDtoList(catalogs);
            } else {

                throw new NotFoundException("Catalogs not found for user with ID: " + userId);
            }
        } catch (IllegalArgumentException e) {

            throw new BadRequestException("Invalid user ID: " + userId);
        }
    }

    public boolean disableCatalog(String catalogId){
        ObjectId objectId = new ObjectId(catalogId);
        Optional<Catalog> optionalCatalog=catalogRepository.findById(objectId);
        if (optionalCatalog.isPresent()) {
            Catalog catalog = optionalCatalog.get();

            if (!catalog.getDisabled()) {
                catalog.setDisabled(true);
                catalogRepository.save(catalog);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean enableCatalog(String catalogId){
        ObjectId objectId = new ObjectId(catalogId);
        Optional<Catalog> optionalCatalog=catalogRepository.findById(objectId);
        if (optionalCatalog.isPresent()) {
            Catalog catalog = optionalCatalog.get();
            if (catalog.getDisabled()) {
                catalog.setDisabled(false);
                catalogRepository.save(catalog);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
