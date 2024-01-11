package hr.techtitans.items.repositories;


import hr.techtitans.items.models.Item;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends MongoRepository<Item, ObjectId> {
    void deleteById(ObjectId id);
    List<Item> findByIdIn(List<ObjectId> ids);
}
