package hr.techtitans.items.repositories;

import hr.techtitans.items.models.ItemCategories;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemCategoriesRepository extends MongoRepository<ItemCategories, ObjectId> {
}
