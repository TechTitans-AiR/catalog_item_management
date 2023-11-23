package hr.techtitans.items.repositories;


import hr.techtitans.items.models.Service;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends MongoRepository<Service, ObjectId> {
}
