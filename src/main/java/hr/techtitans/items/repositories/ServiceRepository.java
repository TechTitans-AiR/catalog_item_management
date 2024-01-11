package hr.techtitans.items.repositories;


import hr.techtitans.items.models.Item;
import hr.techtitans.items.models.Service;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends MongoRepository<Service, ObjectId> {
    void deleteById(ObjectId id);
    List<Service> findByIdIn(List<ObjectId> ids);
}
