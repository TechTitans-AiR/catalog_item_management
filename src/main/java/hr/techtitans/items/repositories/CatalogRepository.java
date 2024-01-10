package hr.techtitans.items.repositories;

import hr.techtitans.items.models.Catalog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CatalogRepository extends MongoRepository<Catalog, ObjectId> {
    List<Catalog> findByUsersContains(ObjectId userId);
    List<Catalog> findByNameContainingIgnoreCase(String name);

    List<Catalog> findByArticlesContaining(ObjectId articleId);

    List<Catalog> findByServicesContaining(ObjectId serviceId);
}
