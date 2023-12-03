package hr.techtitans.items.services;

import hr.techtitans.items.models.Catalog;
import hr.techtitans.items.repositories.CatalogRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

        catalogRepository.insert(catalog);
        return new ResponseEntity<>("Catalog created successfully", HttpStatus.CREATED);
    }
}
