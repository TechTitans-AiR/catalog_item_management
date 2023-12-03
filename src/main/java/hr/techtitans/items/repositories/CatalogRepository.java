package hr.techtitans.items.repositories;

import hr.techtitans.items.models.Catalog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CatalogRepository extends MongoRepository<Catalog, ObjectId> {
}
